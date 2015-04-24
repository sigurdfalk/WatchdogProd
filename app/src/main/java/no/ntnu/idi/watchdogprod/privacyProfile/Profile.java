package no.ntnu.idi.watchdogprod.privacyProfile;

import android.content.Context;

import java.util.ArrayList;

import no.ntnu.idi.watchdogprod.domain.ProfileEvent;
import no.ntnu.idi.watchdogprod.sqlite.profile.ProfileDataSource;

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
    public static final String AVG_INSTALLS_VALUE = "averageInstallValue";
    public static final String AVG_UNINSTALL_VALUE = "averageUninstallValue";

    public static final int APP_TREND_FIXED_HIGH = 4;
    public static final int APP_TREND_FIXED_LOW = 3;
    public static final int APP_TREND_DECREASING = 2;
    public static final int APP_TREND_INCREASING = 1;

    public static final int APP_DISHARMONY = 1;
    public static final int APP_HARMONY = 2;


    private double understandingOfPermissions;
    private double interestInPrivacy;
    private double utilityOverPrivacy;
    private double concernedForLeaks;
    private int installTrendRiskIncreasing;
    private int uninstallTrendRiskIncreasing;

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

    public void createProfile(Context context) {
//        double[] answers = getUserQuestions(context);
        installTrendRiskIncreasing = isTrendIncreasing(getInstalledDataValues(context, Profile.INSTALLED_DANGEROUS_APP), context, Profile.AVG_INSTALLS_VALUE);
        uninstallTrendRiskIncreasing = isTrendIncreasing(getInstalledDataValues(context, Profile.UNINSTALLED_DANGEROUS_APP), context, Profile.AVG_UNINSTALL_VALUE);
    }

    private double[] getUserQuestions(Context context) {
        ProfileDataSource dataSource = new ProfileDataSource(context);
        dataSource.open();
        double[] answers = dataSource.getUserQuestions();
        dataSource.close();
        return answers;
    }

    private double[] getInstalledDataValues(Context context, String type) {
        double[] appValues;
        ProfileDataSource dataSource = new ProfileDataSource(context);
        dataSource.open();
        appValues = dataSource.getInstallData(type);
        dataSource.close();
        return appValues;
    }

    private int isTrendIncreasing(double[] history, Context context, String type) {

        double oldAvg = getOldAverage(context,type);
        double avg = getAverage(history);

        if(oldAvg != avg) {
            saveNewAverage(context, avg, type);
        }

        System.out.println("Old avg " + oldAvg);
        System.out.println("New avg " + avg);

        if (oldAvg == -1) {
            if(avg > 50) {
                return APP_TREND_FIXED_HIGH;
            }
            else if(avg <= 50) {
                return APP_TREND_FIXED_LOW;
            }
        }
        return avg >= oldAvg  ? APP_TREND_INCREASING : APP_TREND_DECREASING ;
    }

    public double  getAverage(double [] history) {
        double temp = 0;
        int startingpoint = 0;
        int total = 10;

        if(history.length > 10) {
            startingpoint = history.length -10;
        } else {
            total = history.length;
        }

        for (int i = startingpoint; i < history.length; i++) {
            temp += history[i];
        }

        return temp / total;
    }

    public long saveNewAverage(Context context, double average, String type) {
        ProfileDataSource profileDataSource = new ProfileDataSource(context);
        profileDataSource.open();
        long id = profileDataSource.insertEvent("", type, average + "");
        profileDataSource.close();
        return id;
    }

    public double getOldAverage(Context context, String type) {
        ProfileDataSource profileDataSource = new ProfileDataSource(context);
        profileDataSource.open();
        ProfileEvent profileEvent = profileDataSource.getSpecificEvent(type);
        if(profileEvent != null) {
            return Double.parseDouble(profileEvent.getValue());
        } else
            return -1;
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

    public int getUninstallTrendRiskIncreasing() {
        return uninstallTrendRiskIncreasing;
    }

    public int getInstallTrendRiskIncreasing() {
        return installTrendRiskIncreasing;
    }

}
