package no.ntnu.idi.watchdogprod.helpers;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import au.com.bytecode.opencsv.CSVReader;
import no.ntnu.idi.watchdogprod.domain.PermissionFact;

/**
 * Created by sigurdhf on 25.03.2015.
 */
public class PermissionFactHelperSingleton {
    public static final String FILE_NAME = "permissionfacts.csv";

    private static PermissionFactHelperSingleton instance;

    private ArrayList<PermissionFact> permissionFacts;
    private Context context;

    private PermissionFactHelperSingleton(Context context) {
        this.context = context;
        this.permissionFacts = readPermissionFactsFromCsv();
    }

    public static PermissionFactHelperSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new PermissionFactHelperSingleton(context);
        }

        return instance;
    }

    public ArrayList<PermissionFact> getPermissionFacts() {
        return permissionFacts;
    }

    public ArrayList<PermissionFact> getApplicationPermissionFacts(String[] reqPermissions) {
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

    private ArrayList<PermissionFact> readPermissionFactsFromCsv() {
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

    private PermissionFact getPermissionFactsFromCSVLine(String[] line) {
        int id = Integer.parseInt(line[0]);
        String[] permissions = line[1].split(",");
        String header = line[2];
        String fact = line[3];

        return new PermissionFact(id, permissions, header.trim(), fact.trim());
    }
}
