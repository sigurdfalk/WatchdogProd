package no.ntnu.idi.watchdogprod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sigurdhf on 09.03.2015.
 */
public class Rule {

    public static final int KEY = 0;
    public static final int NAME = 1;
    public static final int RULE = 2;
    public static final int DESCRIPTION = 3;

    private String key;
    private String name;
    private String rule;
    private String description;

    public Rule(String key, String name, String rule, String description) {
        this.key = key;
        this.name = name;
        this.rule = rule;
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getRule() {
        return rule;
    }

    public String getDescription() {
        return description;
    }

    public boolean isViolated(String[] reqPermissions) {
        // ToDo implement
        return true;
    }
}
