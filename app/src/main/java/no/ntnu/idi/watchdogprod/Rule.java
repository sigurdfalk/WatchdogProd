package no.ntnu.idi.watchdogprod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sigurdhf on 09.03.2015.
 */
public class Rule {
    public static final int RISK_LEVEL_LOW = 1;
    public static final int RISK_LEVEL_MEDIUM = 2;
    public static final int RISK_LEVEL_HIGH = 3;

    public static final int NAME = 0;
    public static final int PERMISSIONS = 1;
    public static final int RISK_LEVEL = 2;
    public static final int DESCRIPTION = 3;

    private String name;
    private String[] permissions;
    private int riskLevel;
    private String description;

    public Rule(String name, String[] permissions, int riskLevel, String description) {
        this.name = name;
        this.permissions = permissions;
        this.riskLevel = riskLevel;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String[] getPermissions() {
        return permissions;
    }

    public int getRiskLevel() {
        return riskLevel;
    }

    public String getDescription() {
        return description;
    }

    public boolean isViolated(String[] reqPermissions) {
        List<String> reqPermList = Arrays.asList(reqPermissions);

        for (String permission : permissions) {
            if (!reqPermList.contains(permission)) {
                return false;
            }
        }

        return true;
    }
}
