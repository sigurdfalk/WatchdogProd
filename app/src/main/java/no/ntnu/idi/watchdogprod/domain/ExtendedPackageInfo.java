package no.ntnu.idi.watchdogprod.domain;

import android.content.pm.PackageInfo;

import java.util.ArrayList;

import no.ntnu.idi.watchdogprod.privacyProfile.PrivacyScoreCalculator;
import no.ntnu.idi.watchdogprod.recommender.AppVector;

/**
 * Created by sigurdhf on 20.03.2015.
 */
public class ExtendedPackageInfo implements Comparable<ExtendedPackageInfo> {
    private PackageInfo packageInfo;
    private ArrayList<PermissionDescription> permissionDescriptions;
    private ArrayList<Rule> violatedRules;
    private ArrayList<PermissionFact> permissionFacts;
    private ArrayList<AppInfo> updateLog;
    private double privacyScore;
    private AppVector vector;


    public ExtendedPackageInfo(PackageInfo packageInfo, ArrayList<PermissionDescription> permissionDescriptions, ArrayList<Rule> violatedRules, ArrayList<PermissionFact> permissionFacts, ArrayList<AppInfo> updateLog) {
        this.packageInfo = packageInfo;
        this.permissionDescriptions = permissionDescriptions;
        this.violatedRules = violatedRules;
        this.permissionFacts = permissionFacts;
        this.updateLog = updateLog;
        this.privacyScore = PrivacyScoreCalculator.calculateScore(permissionDescriptions);
        this.vector = new AppVector(permissionDescriptions);
    }

    public AppVector getVector() {
        return vector;
    }

    public void setVector(AppVector vector) {
        this.vector = vector;
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

    public ArrayList<PermissionFact> getPermissionFacts() {
        return permissionFacts;
    }

    public ArrayList<AppInfo> getUpdateLog() {
        return updateLog;
    }

    public double getPrivacyScore() {
        return privacyScore;
    }

    @Override
    public int compareTo(ExtendedPackageInfo another) {
        return Double.compare(another.getPrivacyScore(), this.getPrivacyScore());
    }


}
