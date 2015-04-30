package no.ntnu.idi.watchdogprod.privacyProfile;

import android.content.Context;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import no.ntnu.idi.watchdogprod.domain.Answer;
import no.ntnu.idi.watchdogprod.domain.ExtendedPackageInfo;
import no.ntnu.idi.watchdogprod.domain.ProfileEvent;
import no.ntnu.idi.watchdogprod.helpers.ApplicationHelperSingleton;
import no.ntnu.idi.watchdogprod.sqlite.answers.AnswersDataSource;
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
    public static final int APP_TREND_NEUTRAL = -1;

    public static final String BEHAVIOR_APPS_KEY = "behaviorAppsList";
    public static final String BEHAVIOR_INSTALLED_APPS = "behaviorInstalledApps";
    public static final String BEHAVIOR_HARMONY_APPS = "behaviorHarmonyApps";

    private double understandingOfPermissions;
    private double interestInPrivacy;
    private double utilityOverPrivacy;
    private double concernedForLeaks;
    private int installTrendRiskIncreasing;
    private int uninstallTrendRiskIncreasing;
    private ArrayList<String> disharmonyApps;
    private ArrayList<String> installedApps;

    private ApplicationHelperSingleton applicationHelperSingleton;

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

    public void createProfile(Context context) throws SQLException {
//        double[] answers = getUserQuestions(context);
        applicationHelperSingleton = ApplicationHelperSingleton.getInstance(context);
        installTrendRiskIncreasing = getInstallTrend(getInstalledDataValues(context, Profile.INSTALLED_DANGEROUS_APP), context, Profile.AVG_INSTALLS_VALUE);
        uninstallTrendRiskIncreasing = getUninstallTrend(getInstalledDataValues(context, Profile.UNINSTALLED_DANGEROUS_APP), context, Profile.AVG_UNINSTALL_VALUE);
        disharmonyApps = getHarmonyApps(context);
    }

    private ArrayList<String> getHarmonyApps(Context context) throws SQLException {
        AnswersDataSource answersDataSource = new AnswersDataSource(context);
        answersDataSource.open();
        ArrayList<Answer> answers = answersDataSource.getAllAnswers();
        answersDataSource.close();

        Map<String, Integer> answerCount = new HashMap<>();

        int sadCount;

        for (Answer answer : answers) {
            if (answer.getAnswer() == Answer.ANSWER_SAD) {
                if (!answerCount.containsKey(answer.getPackageName())) {
                    answerCount.put(answer.getPackageName(), 1);
                } else {
                    sadCount = answerCount.get(answer.getPackageName());
                    sadCount = sadCount + 1;
                    answerCount.put(answer.getPackageName(), sadCount);
                }
            }
        }

        ArrayList<String> disharmonyApps = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : answerCount.entrySet()) {
            disharmonyApps.add(entry.getKey());
        }
        return disharmonyApps;
    }

    public boolean isInDatabase(Context context, String type, String packageName) {
        ProfileDataSource dataSource = new ProfileDataSource(context);
        dataSource.open();
        boolean exists = dataSource.isInDatabase(packageName, type);
        dataSource.close();
        return exists;
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

    private double getCurrentInstalledAverage() {
        double sum = 0;
        ArrayList<ExtendedPackageInfo> extendedPackageInfos = applicationHelperSingleton.getApplications();
        for (ExtendedPackageInfo extendedPackageInfo : extendedPackageInfos) {
            sum += extendedPackageInfo.getPrivacyScore();
        }
        return sum / extendedPackageInfos.size();
    }

    private boolean isTrendIncreasing(double[] history) {

        double avgOld = 0;
        double avgNew = 0;
        double sumOld = 0;
        double sumNew = 0;

        for (int i = 0; i < history.length - 1; i++) {
            sumOld += history[i];
        }

        for (int i = 0; i < history.length; i++) {
            sumNew += history[i];
        }

        avgOld = sumOld / history.length - 1;
        avgNew = sumNew / history.length;

        return (avgNew > avgOld);
    }

    private int getInstallTrend(double[] history, Context context, String type) {

        System.out.println("INSTALL");

        double oldAvg = getOldAverage(context, type);

        double avg = 0;

        if (oldAvg == -1) {
            avg = getCurrentInstalledAverage();
            saveNewAverage(context, avg, type);
            if (avg >= PrivacyScoreCalculator.MEDIUM_THRESHOLD) {
                return APP_TREND_FIXED_HIGH;
            } else {
                return APP_TREND_FIXED_LOW;
            }
        }

        avg = getAverage(history);

        if(oldAvg == avg) {
            if (avg >= PrivacyScoreCalculator.MEDIUM_THRESHOLD) {
                return APP_TREND_FIXED_HIGH;
            } else {
                return APP_TREND_FIXED_LOW;
            }
        }

        if (history.length <= 2) {
            if (avg >= PrivacyScoreCalculator.MEDIUM_THRESHOLD) {
                return APP_TREND_FIXED_HIGH;
            } else {
                return APP_TREND_FIXED_LOW;
            }
        }

        if (isTrendIncreasing(history) && avg >= PrivacyScoreCalculator.MEDIUM_THRESHOLD) {
            return APP_TREND_INCREASING;
        } else if (avg >= PrivacyScoreCalculator.MEDIUM_THRESHOLD) {
            return APP_TREND_FIXED_HIGH;
        } else {
            return APP_TREND_FIXED_LOW;
        }
    }

    private int getUninstallTrend(double[] history, Context context, String type) {
        System.out.println("UNINSTALL");

        double oldAvg = getOldAverage(context, type);
        double avg = 0;

        if (oldAvg == -1) {
            saveNewAverage(context, avg, type);
            return APP_TREND_NEUTRAL;
        }

        avg = getAverage(history);

        if (history.length <= 2) {
            if (avg >= PrivacyScoreCalculator.MEDIUM_THRESHOLD) {
                return APP_TREND_FIXED_HIGH;
            } else {
                return APP_TREND_FIXED_LOW;
            }
        }

        if (!isTrendIncreasing(history)) {
            return APP_TREND_INCREASING;
        } else if (avg >= PrivacyScoreCalculator.MEDIUM_THRESHOLD) {
            return APP_TREND_FIXED_HIGH;
        } else {
            return APP_TREND_FIXED_LOW;
        }
    }

    public double getAverage(double[] history) {
        double temp = 0;
        for (int i = 0; i < history.length; i++) {
            temp += history[i];
        }

        return temp / history.length;
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
        profileDataSource.close();

        if (profileEvent == null) {
            return -1;
        } else {
            return Double.parseDouble(profileEvent.getValue());
        }
    }

    public int getUninstallTrendRiskIncreasing() {
        return uninstallTrendRiskIncreasing;
    }

    public int getInstallTrendRiskIncreasing() {
        return installTrendRiskIncreasing;
    }

    public ArrayList<String> getDisharmonyApps() {
        return disharmonyApps;
    }
}
