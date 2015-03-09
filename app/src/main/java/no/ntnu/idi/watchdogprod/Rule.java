package no.ntnu.idi.watchdogprod;

import java.util.ArrayList;

/**
 * Created by sigurdhf on 09.03.2015.
 */
public class Rule {
    private String name;
    private ArrayList<String> permissions;
    private int riskLevel;
    private String description;

    public Rule(String name, ArrayList<String> permissions, int riskLevel, String description) {
        this.name = name;
        this.permissions = permissions;
        this.riskLevel = riskLevel;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getPermissions() {
        return permissions;
    }

    public int getRiskLevel() {
        return riskLevel;
    }

    public String getDescription() {
        return description;
    }
}
