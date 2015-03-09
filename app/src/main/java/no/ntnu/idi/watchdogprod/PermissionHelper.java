package no.ntnu.idi.watchdogprod;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Created by sigurdhf on 09.03.2015.
 */
public class PermissionHelper {
    public static final String FILE_NAME = "permissiondescriptions.txt";

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
        //int riskLevel = Integer.parseInt(line[PermissionDescription.RISK_LEVEL]);
        int riskLevel = 0;
        String description = line[PermissionDescription.DESCRIPTION];

        return new PermissionDescription(name, description, riskLevel);
    }
}
