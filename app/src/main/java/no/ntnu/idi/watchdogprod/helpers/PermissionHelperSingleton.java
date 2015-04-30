package no.ntnu.idi.watchdogprod.helpers;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import au.com.bytecode.opencsv.CSVReader;
import no.ntnu.idi.watchdogprod.domain.PermissionDescription;

/**
 * Created by sigurdhf on 09.03.2015.
 */
public class PermissionHelperSingleton {
    public static final String FILE_NAME = "permissiondescriptions.csv";
    public static final String ALL_PERMISSIONS_KEY = "allPermissionsList";
    public static final String TAG = "permissionHelper";

    private static PermissionHelperSingleton instance;

    private ArrayList<PermissionDescription> permissionDescriptions;
    private Context context;

    private PermissionHelperSingleton(Context context) {
        this.context = context;
        this.permissionDescriptions = readPermissionDescriptionsFromCsv();
    }

    public static PermissionHelperSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new PermissionHelperSingleton(context);
        }

        return instance;
    }

    public ArrayList<PermissionDescription> getPermissionDescriptions() {
        return permissionDescriptions;
    }

    public ArrayList<PermissionDescription> getApplicationPermissionDescriptions(String[] reqPermissions) {
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

    public PermissionDescription getPermissionDescription(String permission) {
        for (PermissionDescription permissionDescription : permissionDescriptions) {
            if (permission.contains(permissionDescription.getName())) {
                return permissionDescription;
            }
        }

        throw new IllegalArgumentException("Permissiondescription of permission " + permission + " do not exist!");
    }
    public ArrayList<PermissionDescription> parseArray(String[] array){
        PermissionHelperSingleton permissionHelper = PermissionHelperSingleton.getInstance(context);

        ArrayList<PermissionDescription> list = new ArrayList<>();
        for (String s : array) {

            try {
                list.add(permissionHelper.getPermissionDescription(s));
            } catch (Exception e) {
                Log.e(TAG, "Permissiondescription of permission " + s + " do not exist!");
            }
        }
        return list;
    }

    private ArrayList<PermissionDescription> readPermissionDescriptionsFromCsv() {
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

    private PermissionDescription getPermissionDescriptionFromCSVLine(String[] line) {
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

    public ArrayList<PermissionDescription> newRequestedPermissions(String[] oldPermissions, String[] newPermissions) {
        ArrayList<PermissionDescription> newReqPerm = new ArrayList<>();

        for (String newPermission : newPermissions) {
            PermissionDescription newPermissionDescription = null;

            try {
                newPermissionDescription = getPermissionDescription(newPermission);
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

    public ArrayList<PermissionDescription> removedPermissions(String[] oldPermissions, String[] newPermissions) {
        ArrayList<PermissionDescription> removedPerm = new ArrayList<>();

        for (String oldPermission : oldPermissions) {
            PermissionDescription removedPermissionDescription = null;

            try {
                removedPermissionDescription = getPermissionDescription(oldPermission);
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
