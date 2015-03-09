package no.ntnu.idi.watchdogprod;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Created by sigurdhf on 09.03.2015.
 */
public class RuleHelper {
    public static final String FILE_NAME = "ruleset.txt";

    private static ArrayList<Rule> rules;

    public static ArrayList<Rule> getAllRules(Context context) {
        if (isRulesPopulated()) {
            return rules;
        }

        rules = readRulesFromCsv(context);
        return rules;
    }

    public static ArrayList<Rule> getViolatedRules(String[] reqPermissions, Context context) {
        if (!isRulesPopulated()) {
            rules = readRulesFromCsv(context);
        }

        ArrayList<Rule> violatedRules = new ArrayList<>();

        if (reqPermissions == null) {
            return violatedRules;
        }

        for (Rule rule : rules) {
            if (rule.isViolated(reqPermissions)) {
                violatedRules.add(rule);
            }
        }

        return violatedRules;
    }

    private static boolean isRulesPopulated() {
        return rules != null && !rules.isEmpty();
    }

    private static ArrayList<Rule> readRulesFromCsv(Context context) {
        ArrayList<Rule> ruleList = new ArrayList<>();
        AssetManager assetManager = context.getAssets();

        try {
            InputStream csvStream = assetManager.open(FILE_NAME);
            InputStreamReader csvStreamReader = new InputStreamReader(csvStream);
            CSVReader csvReader = new CSVReader(csvStreamReader);
            String[] line;

            // throw away the header
            csvReader.readNext();

            while ((line = csvReader.readNext()) != null) {
                ruleList.add(getRuleFromCSVLine(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ruleList;
    }

    private static Rule getRuleFromCSVLine(String[] line) {
        String name = line[Rule.NAME];
        String[] permissions = line[Rule.PERMISSIONS].split(":");
        int riskLevel = Integer.parseInt(line[Rule.RISK_LEVEL]);
        String description = line[Rule.DESCRIPTION];

        return new Rule(name, permissions, riskLevel, description);
    }
}
