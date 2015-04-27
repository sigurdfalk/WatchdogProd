package no.ntnu.idi.watchdogprod.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by sigurdhf on 09.03.2015.
 */
public class Rule implements Comparable<Rule> {

    public static final int KEY = 0;
    public static final int NAME = 1;
    public static final int RULE = 2;
    public static final int DESCRIPTION = 3;

    private String key;
    private String name;
    private String ruleString;
    private String description;

    public Rule(String key, String name, String ruleString, String description) {
        this.key = key;
        this.name = name;
        this.ruleString = ruleString;
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getRuleString() {
        return ruleString;
    }

    public String getDescription() {
        return description;
    }

    public boolean isViolated(String[] reqPermissions) {
        String[] rules = ruleString.split(":");

        for (String rule : rules) {
            if (rule.startsWith("_or")) {
                rule = rule.substring(4, rule.length() - 1);
                String[] select = rule.split("&");

                boolean containsPerm = false;

                for (String or : select) {
                    if (containsPermission(or, reqPermissions)) {
                        containsPerm = true;
                    }
                }

                if (!containsPerm) {
                    return false;
                }
            } else {
                if (!containsPermission(rule, reqPermissions)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean containsPermission(String permission, String[] reqPermissions) {
        for (String reqPermission : reqPermissions) {
            if (reqPermission.trim().contains(permission.trim())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int compareTo(Rule another) {
        return this.getName().compareTo(another.getName());
    }
}
