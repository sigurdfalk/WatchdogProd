package no.ntnu.idi.watchdogprod.helpers;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import no.ntnu.idi.watchdogprod.domain.AppInfo;
import no.ntnu.idi.watchdogprod.domain.ExtendedPackageInfo;
import no.ntnu.idi.watchdogprod.domain.PermissionDescription;
import no.ntnu.idi.watchdogprod.domain.PermissionFact;
import no.ntnu.idi.watchdogprod.domain.Rule;
import no.ntnu.idi.watchdogprod.privacyProfile.PrivacyScoreCalculator;
import no.ntnu.idi.watchdogprod.sqlite.applicationupdates.ApplicationUpdatesDataSource;

/**
 * Created by sigurdhf on 28.04.2015.
 */
public class ApplicationHelperSingleton {
    private static ApplicationHelperSingleton instance;

    private Context context;
    private ArrayList<ExtendedPackageInfo> applications;
    private PrivacyScoreCalculator privacyScoreCalculator;
    private PermissionFactHelperSingleton permissionFactHelper;
    private PermissionHelperSingleton permissionHelper;
    private RuleHelperSingleton ruleHelper;

    private ApplicationHelperSingleton(Context context) {
        this.context = context;
        this.applications = new ArrayList<>();
        this.privacyScoreCalculator = PrivacyScoreCalculator.getInstance(context);
        this.permissionFactHelper = PermissionFactHelperSingleton.getInstance(context);
        this.permissionHelper = PermissionHelperSingleton.getInstance(context);
        this.ruleHelper = RuleHelperSingleton.getInstance(context);
        initializeInstance();
    }

    public static ApplicationHelperSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new ApplicationHelperSingleton(context);
        }

        return instance;
    }

    private void initializeInstance() {
        List<PackageInfo> allApplications = context.getPackageManager().getInstalledPackages(PackageManager.GET_META_DATA | PackageManager.GET_PERMISSIONS);
        ApplicationUpdatesDataSource dataSource = new ApplicationUpdatesDataSource(context);
        dataSource.open();

        for (PackageInfo packageInfo : allApplications) {
            if (!((packageInfo.applicationInfo.flags & packageInfo.applicationInfo.FLAG_SYSTEM) != 0)) {
                ArrayList<PermissionDescription> permissionDescriptions = permissionHelper.getApplicationPermissionDescriptions(packageInfo.requestedPermissions);
                ArrayList<Rule> violatedRules = ruleHelper.getViolatedRules(packageInfo.requestedPermissions);
                ArrayList<PermissionFact> permissionFacts = permissionFactHelper.getApplicationPermissionFacts(packageInfo.requestedPermissions);
                ArrayList<AppInfo> updateLog = dataSource.getApplicationUpdatesByPackageName(packageInfo.packageName);
                Collections.sort(updateLog);
                this.applications.add(new ExtendedPackageInfo(packageInfo, permissionDescriptions, violatedRules, permissionFacts, updateLog, privacyScoreCalculator));
            }
        }

        dataSource.close();
        Collections.sort(this.applications);
    }

    public PrivacyScoreCalculator getPrivacyScoreCalculator() {
        return privacyScoreCalculator;
    }

    public RuleHelperSingleton getRuleHelper() {
        return ruleHelper;
    }

    public ArrayList<ExtendedPackageInfo> getApplications() {
        return applications;
    }

    public void removeApplication(String packageName) {
        for(ExtendedPackageInfo extendedPackageInfo : applications) {
            if(extendedPackageInfo.getPackageInfo().packageName.equals(packageName)) {
                applications.remove(extendedPackageInfo);
                return;
            }
        }
        return;
    }

    public PermissionFactHelperSingleton getPermissionFactHelper() {
        return permissionFactHelper;
    }

    public PermissionHelperSingleton getPermissionHelper() {
        return permissionHelper;
    }

    public ExtendedPackageInfo getApplicationByPackageName(String packageName) {
        for (ExtendedPackageInfo application : applications) {
            if (application.getPackageInfo().packageName.equals(packageName)) {
                return application;
            }
        }

        throw new NoSuchElementException("Application matching " + packageName + " not found.");
    }

    public void updateApplicationPrivacyScores() {
        privacyScoreCalculator.calculatePermissionWeights();

        for (ExtendedPackageInfo application : applications) {
            application.setPrivacyScore(privacyScoreCalculator.calculatePrivacyScore(application));
        }
    }

    public void updateInstance() {
        applications.clear();
        initializeInstance();
    }

    public static String getApplicationName(Context context, PackageInfo packageInfo) {
        return context.getPackageManager().getApplicationLabel(packageInfo.applicationInfo).toString();
    }

    public static PackageInfo getApplicationPackageInfo(Context context, String packageName) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_META_DATA | PackageManager.GET_PERMISSIONS);
    }
}
