package no.ntnu.idi.watchdogprod.domain;

/**
 * Created by sigurdhf on 09.03.2015.
 */
public class PermissionDescription implements Comparable<PermissionDescription> {
    public static final int NAME = 0;
    public static final int DESIGNATION = 1;
    public static final int GROUP = 2;
    public static final int LEVEL = 3;
    public static final int RISK = 4;
    public static final int DESCRIPTION = 5;

    private String name;
    private String designation;
    private String group;
    private String level;
    private int risk;
    private String description;

    public PermissionDescription(String name, String designation, String group, String level, int risk, String description) {
        this.name = name;
        this.designation = designation;
        this.group = group;
        this.level = level;
        this.risk = risk;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDesignation() {
        return designation;
    }

    public String getGroup() {
        return group;
    }

    public String getLevel() {
        return level;
    }

    public int getRisk() {
        return risk;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int compareTo(PermissionDescription another) {
        if (this.getRisk() > another.getRisk()) {
            return -1;
        } else if (this.getRisk() < another.getRisk()) {
            return 1;
        } else {
            return this.getDesignation().compareTo(another.getDesignation());
        }
    }

    @Override
    public boolean equals(Object o) {
        return ((PermissionDescription) o).getName().equals(this.getName());
    }
}
