package no.ntnu.idi.watchdogprod.helpers;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import au.com.bytecode.opencsv.CSVReader;
import no.ntnu.idi.watchdogprod.domain.Rule;

/**
 * Created by sigurdhf on 09.03.2015.
 */
public class RuleHelperSingleton {
    public static final String FILE_NAME = "ruleset.csv";

    private static RuleHelperSingleton instance;

    private ArrayList<Rule> rules;
    private Context context;

    private RuleHelperSingleton(Context context) {
        this.context = context;
        this.rules = readRulesFromCsv();
    }

    public static RuleHelperSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new RuleHelperSingleton(context);
        }

        return instance;
    }

    public ArrayList<Rule> getRules() {
        return rules;
    }

    public ArrayList<Rule> getViolatedRules(String[] reqPermissions) {
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

    private ArrayList<Rule> readRulesFromCsv() {
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

    private Rule getRuleFromCSVLine(String[] line) {
        String key = line[Rule.KEY];
        String name = line[Rule.NAME];
        String rule = line[Rule.RULE];
        String description = line[Rule.DESCRIPTION];

        return new Rule(key, name, rule, description);
    }
}
