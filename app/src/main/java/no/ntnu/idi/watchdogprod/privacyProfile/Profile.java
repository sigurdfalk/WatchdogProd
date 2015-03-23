package no.ntnu.idi.watchdogprod.privacyProfile;

import android.content.Context;

import java.util.ArrayList;

import no.ntnu.idi.watchdogprod.sqlite.profile.ProfileDataSource;
import no.ntnu.idi.watchdogprod.sqlite.profile.ProfileEvent;

/**
 * Created by sigurdhf on 20.03.2015.
 */
public class Profile {
    public static final String DID_READ_EULA = "didReadEULA";
    public static final String DID_NOT_READ_EULA = "didNotReadEULA";
    public static final String INSTALLED_DANGEROUS_APP = "installedDangerousApp";
    public static final String UNINSTALLED_DANGEROUS_APP = "uninstalledDangerousApp";
    public static final String CHANGED_SCREEN_LOCK = "changedScreenLock";
    public static final String UNDERSTANDING_OF_PERMISSIONS = "understandingOfPermissions";
    public static final String INTEREST_IN_PRIVACY = "interestInPrivacy";
    public static final String UTILITY_OVER_PRIVACY = "utilityOverPrivacy";
    public static final String CONCERNED_FOR_LEAKS = "concernedForLeaks";

    private double understandingOfPermissions;
    private double interestInPrivacy;
    private double utilityOverPrivacy;
    private double concernedForLeaks;

    public Profile() {
        this.understandingOfPermissions = 3.0;
        this.interestInPrivacy = 5.0;
        this.concernedForLeaks = 5.0;
        this.utilityOverPrivacy = 3.0;
    }

    public Profile(double understandingOfPermissions, double interestInPrivacy, double utilityOverPrivacy, double concernedForLeaks) {
        this.understandingOfPermissions = understandingOfPermissions;
        this.interestInPrivacy = interestInPrivacy;
        this.concernedForLeaks = concernedForLeaks;
        this.utilityOverPrivacy = utilityOverPrivacy;
    }

    public double getUnderstandingOfPermissions() {
        return understandingOfPermissions;
    }

    public double getInterestInPrivacy() {
        return interestInPrivacy;
    }

    public double getUtilityOverPrivacy() {
        return utilityOverPrivacy;
    }

    public double getConcernedForLeaks() {
        return concernedForLeaks;
    }
}
