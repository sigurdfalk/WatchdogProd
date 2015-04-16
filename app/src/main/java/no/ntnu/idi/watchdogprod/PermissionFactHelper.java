package no.ntnu.idi.watchdogprod;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Created by sigurdhf on 25.03.2015.
 */
public class PermissionFactHelper {
    public static final String FILE_NAME = "permissionfacts.csv";

    private static ArrayList<PermissionFact> permissionFacts;

    public static ArrayList<PermissionFact> getAllPermissionFacts(Context context) {
        if (isPermissionDescriptionsPopulated()) {
            return permissionFacts;
        }

        permissionFacts = readPermissionFactsFromCsv(context);
        return permissionFacts;
    }

    public static ArrayList<PermissionFact> getAppPermissionFacts(Context context, String[] reqPermissions) {
        if (!isPermissionDescriptionsPopulated()) {
            permissionFacts = readPermissionFactsFromCsv(context);
        }

        ArrayList<PermissionFact> appFacts = new ArrayList<>();

        if (reqPermissions == null) {
            return appFacts;
        }

        for (PermissionFact fact : permissionFacts) {
            if (fact.matchesApp(reqPermissions)) {
                appFacts.add(fact);
            }
        }

        return appFacts;
    }

    private static boolean isPermissionDescriptionsPopulated() {
        return permissionFacts != null && !permissionFacts.isEmpty();
    }

    private static ArrayList<PermissionFact> readPermissionFactsFromCsv(Context context) {
        ArrayList<PermissionFact> facts = new ArrayList<>();
        AssetManager assetManager = context.getAssets();

        try {
            InputStream csvStream = assetManager.open(FILE_NAME);
            InputStreamReader csvStreamReader = new InputStreamReader(csvStream);
            CSVReader csvReader = new CSVReader(csvStreamReader);
            String[] line;

            // throw away the header
            csvReader.readNext();

            while ((line = csvReader.readNext()) != null) {
                facts.add(getPermissionFactsFromCSVLine(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return facts;
    }

    private static PermissionFact getPermissionFactsFromCSVLine(String[] line) {
        int id = Integer.parseInt(line[0]);
        String[] permissions = line[1].split(",");
        String header = line[2];
        String fact = line[3];

        return new PermissionFact(id, permissions, header.trim(), fact.trim());
    }
}
