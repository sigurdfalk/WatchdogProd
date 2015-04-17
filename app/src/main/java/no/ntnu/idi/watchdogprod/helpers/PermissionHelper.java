package no.ntnu.idi.watchdogprod.helpers;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import au.com.bytecode.opencsv.CSVReader;
import no.ntnu.idi.watchdogprod.domain.PermissionDescription;

/**
 * Created by sigurdhf on 09.03.2015.
 */
public class PermissionHelper {
    public static final String FILE_NAME = "permissiondescriptions.csv";

    private static ArrayList<PermissionDescription> permissionDescriptions;

    public static ArrayList<PermissionDescription> getAllPermissionDescriptions(Context context) {
        if (isPermissionDescriptionsPopulated()) {
            return permissionDescriptions;
        }

        permissionDescriptions = readPermissionDescriptionsFromCsv(context);
        return permissionDescriptions;
    }

    public static ArrayList<PermissionDescription> getApplicationPermissionDescriptions(String[] reqPermissions, Context context) {
        if (!isPermissionDescriptionsPopulated()) {
            permissionDescriptions = readPermissionDescriptionsFromCsv(context);
        }

        ArrayList<PermissionDescription> appPermDesc = new ArrayList<>();

        if (reqPermissions == null) {
            return appPermDesc;
        }

        for (String permission : reqPermissions) {
            for (PermissionDescription permissionDescription : permissionDescriptions) {
                if (permission.contains(permissionDescription.getName())) {
                    appPermDesc.add(permissionDescription);
                }
            }
        }

        return appPermDesc;
    }

    public static PermissionDescription getPermissionDescription(Context context, String permission) {
        ArrayList<PermissionDescription> allPermissionDescriptions = getAllPermissionDescriptions(context);

        for (PermissionDescription permissionDescription : allPermissionDescriptions) {
            if (permission.contains(permissionDescription.getName())) {
                return permissionDescription;
            }
        }

        throw new IllegalArgumentException("Permissiondescription of permission " + permission + " do not exist!");
    }

    private static boolean isPermissionDescriptionsPopulated() {
        return permissionDescriptions != null && !permissionDescriptions.isEmpty();
    }

    private static ArrayList<PermissionDescription> readPermissionDescriptionsFromCsv(Context context) {
        ArrayList<PermissionDescription> permDescList = new ArrayList<>();
        AssetManager assetManager = context.getAssets();

        try {
            InputStream csvStream = assetManager.open(FILE_NAME);
            InputStreamReader csvStreamReader = new InputStreamReader(csvStream);
            CSVReader csvReader = new CSVReader(csvStreamReader);
            String[] line;

            // throw away the header
            csvReader.readNext();

            while ((line = csvReader.readNext()) != null) {
                permDescList.add(getPermissionDescriptionFromCSVLine(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return permDescList;
    }

    private static PermissionDescription getPermissionDescriptionFromCSVLine(String[] line) {
        String name = line[PermissionDescription.NAME];
        String designation = line[PermissionDescription.DESIGNATION];
        String group = line[PermissionDescription.GROUP];
        String level = line[PermissionDescription.LEVEL];
        int risk = 0;
        try {
            risk = Integer.parseInt(line[PermissionDescription.RISK]);
        } catch (NumberFormatException e) {
            // Do nothing.
        }
        String description = line[PermissionDescription.DESCRIPTION];

        return new PermissionDescription(name.trim(), designation.trim(), group.trim(), level.trim(), risk, description.trim());
    }

    public static ArrayList<PermissionDescription> newRequestedPermissions(Context context, String[] oldPermissions, String[] newPermissions) {
        ArrayList<PermissionDescription> newReqPerm = new ArrayList<>();

        for (String newPermission : newPermissions) {
            PermissionDescription newPermissionDescription = null;

            try {
                newPermissionDescription = getPermissionDescription(context, newPermission);
            } catch (Exception e) {
                continue;
            }

            boolean exists = false;

            for (String oldPermission : oldPermissions) {
                if (oldPermission.contains(newPermissionDescription.getName())) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                newReqPerm.add(newPermissionDescription);
            }
        }

        return newReqPerm;
    }

    public static ArrayList<PermissionDescription> removedPermissions(Context context, String[] oldPermissions, String[] newPermissions) {
        ArrayList<PermissionDescription> removedPerm = new ArrayList<>();

        for (String oldPermission : oldPermissions) {
            PermissionDescription removedPermissionDescription = null;

            try {
                removedPermissionDescription = getPermissionDescription(context, oldPermission);
            } catch (Exception e) {
                continue;
            }

            boolean exist = false;

            for (String newPermission : newPermissions) {
                if (newPermission.contains(removedPermissionDescription.getName())) {
                    exist = true;
                    break;
                }
            }

            if (!exist) {
                removedPerm.add(removedPermissionDescription);
            }
        }

        return removedPerm;
    }
}
