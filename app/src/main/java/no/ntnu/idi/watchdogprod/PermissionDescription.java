package no.ntnu.idi.watchdogprod;

/**
 * Created by sigurdhf on 09.03.2015.
 */
public class PermissionDescription {
    public static final int NAME = 0;
    public static final int DESCRIPTION = 1;
    public static final int RISK_LEVEL = 2;

    private String name;
    private String description;
    private int riskLevel;

    public PermissionDescription(String name, String description, int riskValue) {
        this.name = name;
        this.description = description;
        this.riskLevel = riskValue;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getRiskLevel() {
        return riskLevel;
    }
}
