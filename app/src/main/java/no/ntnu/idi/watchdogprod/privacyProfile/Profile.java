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
        installTrendRiskIncreasing = isTrendIncreasing(getInstalledAppsValues(context), context, Profile.AVG_INSTALLS_VALUE);
    }

    private double[] getUserQuestions(Context context) {
        ProfileDataSource dataSource = new ProfileDataSource(context);
        dataSource.open();
        double[] answers = dataSource.getUserQuestions();
        dataSource.close();
        return answers;
    }

    private double[] getUninstalledAppsValues(Context context) {
        double[] appValues;
        ProfileDataSource dataSource = new ProfileDataSource(context);
        dataSource.open();
        appValues = dataSource.getUninstalledApps();
        dataSource.close();
        return appValues;
    }

    private double[] getInstalledAppsValues(Context context) {
        double[] appValues;
        ProfileDataSource dataSource = new ProfileDataSource(context);
        dataSource.open();
        appValues = dataSource.getInstalledApps();
        dataSource.close();
        return appValues;
    }

    private boolean isTrendIncreasing(double[] history, int start, int limit, int oldTrendValue) {
        final int DELTA_T = 1;
        double sum1 = 0;
        double sum2 = 0;
        for (int i = start; i <= limit; i++) {
            sum1 = history[i];
            if (i == 0) {
                sum2 = history[i] * 1;
            } else {
                sum2 = history[i] * (i + 1);
            }
        }

        double b0 = (2 * (2 * history.length) * sum1 - (6 * sum2))
                / (history.length * (history.length - 1));

        double b1 = ((12 * sum2) - ((6 * (history.length + 1)) * sum1))
                / (DELTA_T * history.length * ((history.length - 1) * (history.length + 1)));

        System.out.println("i = " + limit + " B0 + B1 = " + (b0 + b1) + "\t  B1 = " + b1 + "\t B0 = " + b0);

        if (b1 > 0)
            return true;
        return false;
    }

    private int isTrendIncreasing(double[] history, Context context, String type) {

        double oldAvg = getOldAverage(context);

        double avg = getAverage(history);

        if(oldAvg != avg) {
            saveNewAverage(context, avg, type);
        }

        System.out.println("Old avg " + oldAvg);
        System.out.println("New avg " + avg);

        if (oldAvg == -1) {
            return 0;
        }
        return avg >= oldAvg ? 1 : -1 ;
    }

    public double  getAverage(double [] history) {
        double avg = 0;
        double temp = 0;
        double MIN_STEP = 10;

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

    public double getOldAverage(Context context) {
        ProfileDataSource profileDataSource = new ProfileDataSource(context);
        profileDataSource.open();
        ProfileEvent profileEvent = profileDataSource.getSpecificEvent(Profile.AVG_INSTALLS_VALUE);
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
