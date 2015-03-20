package no.ntnu.idi.watchdogprod;

import android.content.pm.PackageInfo;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by sigurdhf on 20.03.2015.
 */
public class ExtendedPackageInfo implements Comparable<ExtendedPackageInfo> {
    private PackageInfo packageInfo;
    private ArrayList<PermissionDescription> permissionDescriptions;
    private ArrayList<Rule> violatedRules;
    private double privacyScore;

    public ExtendedPackageInfo(PackageInfo packageInfo, ArrayList<PermissionDescription> permissionDescriptions, ArrayList<Rule> violatedRules) {
        this.packageInfo = packageInfo;
        this.permissionDescriptions = permissionDescriptions;
        this.violatedRules = violatedRules;
        this.privacyScore = PrivacyScoreCalculator.calculateScore(permissionDescriptions);
    }

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public ArrayList<PermissionDescription> getPermissionDescriptions() {
        return permissionDescriptions;
    }

    public ArrayList<Rule> getViolatedRules() {
        return violatedRules;
    }

    public double getPrivacyScore() {
        return privacyScore;
    }

    @Override
    public int compareTo(ExtendedPackageInfo another) {
        return Double.compare(another.getPrivacyScore(), this.getPrivacyScore());
    }
}
