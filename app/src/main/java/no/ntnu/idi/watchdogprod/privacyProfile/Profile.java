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
import no.ntnu.idi.watchdogprod.domain.ProfileEvent;
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

    private double understandingOfPermissions;
    private double interestInPrivacy;
    private double utilityOverPrivacy;
    private double concernedForLeaks;
    private int installTrendRiskIncreasing;
    private int uninstallTrendRiskIncreasing;
    private ArrayList<String> disharmonyApps;

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
        installTrendRiskIncreasing = getInstallTrend(getInstalledDataValues(context, Profile.INSTALLED_DANGEROUS_APP), context, Profile.AVG_INSTALLS_VALUE);
        uninstallTrendRiskIncreasing = getUninstallTrend(getInstalledDataValues(context, Profile.UNINSTALLED_DANGEROUS_APP), context, Profile.AVG_UNINSTALL_VALUE);
        disharmonyApps = getHarmony(context);
    }

    private ArrayList<String> getHarmony(Context context) throws SQLException {
        AnswersDataSource answersDataSource = new AnswersDataSource(context);
        answersDataSource.open();
        ArrayList<Answer> answers = answersDataSource.getAllAnswers();
        answersDataSource.close();

        Map<String, Integer> answerCount = new HashMap<>();

        int sadCount = 0;

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
            if (entry.getValue() >= 2) {
                disharmonyApps.add(entry.getKey());
            }
        }
        return disharmonyApps;
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

    private int getInstallTrend(double[] history, Context context, String type) {
        double oldAvg = 0;
        ProfileEvent event = getOldAverage(context, type);
        if (event == null) {
            System.out.println("EVENT ER NULL");
            oldAvg = -1;
        } else {
            oldAvg = Double.parseDouble(event.getValue());
        }

        double avg = getAverage(history);

        System.out.println("Old avg " + oldAvg);
        System.out.println("New avg " + avg);

        if (oldAvg == -1) {
            if (history.length == 0) {
                return APP_TREND_NEUTRAL;
            }

            saveNewAverage(context, avg, type);

            if (avg >= PrivacyScoreCalculator.MEDIUM_THRESHOLD) {
                return APP_TREND_INCREASING;
            } else  {
                return APP_TREND_DECREASING;
            }
        }

        if (oldAvg != avg) {
            saveNewAverage(context, avg, type);
            return avg > oldAvg ? APP_TREND_INCREASING : APP_TREND_DECREASING;
        }

        Date now = new Date();
        final int hoursInDay = 24;
        long diff = now.getTime() - dateConverter(event.getTimestamp());

        if (TimeUnit.MILLISECONDS.toHours(diff) < (hoursInDay * 2)) {
            if (avg >= PrivacyScoreCalculator.MEDIUM_THRESHOLD) {
                return APP_TREND_INCREASING;
            } else  {
                return APP_TREND_DECREASING;
            }
        } else {
            if (avg > PrivacyScoreCalculator.MEDIUM_THRESHOLD) {
                return APP_TREND_FIXED_HIGH;
            } else {
                return APP_TREND_FIXED_LOW;
            }
        }
    }

    private long dateConverter (String timestamp) {
        Date date = new Date();
        DateFormat dateFormat= new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        Date formattedDate = null;

        try{
            formattedDate = dateFormat.parse(dateFormat.format(date));
            System.out.println(formattedDate.toString());
        }catch(ParseException parseEx){
            parseEx.printStackTrace();
        }
        return formattedDate.getTime();
    }

    private int getUninstallTrend(double[] history, Context context, String type) {
        double oldAvg = 0;

        ProfileEvent event = getOldAverage(context, type);
        if(event == null) {
            System.out.println("Event er null");
            oldAvg = -1;
        } else {
            oldAvg = Double.parseDouble(event.getValue());
        }

        double avg = getAverage(history);

        System.out.println("Old avg " + oldAvg);
        System.out.println("New avg " + avg);

        if (oldAvg == -1) {
            if (history.length == 0) {
                return -1;
            }

            saveNewAverage(context, avg, type);

            if (avg >= PrivacyScoreCalculator.MEDIUM_THRESHOLD) {
                return APP_TREND_INCREASING;
            } else  {
                return APP_TREND_NEUTRAL;
            }
        }

        if (oldAvg != avg) {
            saveNewAverage(context, avg, type);
            return avg > oldAvg ? APP_TREND_INCREASING : APP_TREND_NEUTRAL;
        }

        Date now = new Date();
        final int hoursInDay = 24;
        long diff = now.getTime() - dateConverter(event.getTimestamp());

        if (TimeUnit.MILLISECONDS.toHours(diff) < (hoursInDay * 2)) {
            return avg > oldAvg ? APP_TREND_INCREASING : APP_TREND_NEUTRAL;
        } else {
            return APP_TREND_NEUTRAL;
        }
    }

    public double getAverage(double[] history) {
        double temp = 0;
        int startingpoint = 0;
        int total = 10;

        System.out.println("HISTORY LENGTH " + history.length);
        if (history.length > 10) {
            startingpoint = history.length - 10;
        } else {
            total = history.length;
        }

        for (int i = startingpoint; i < history.length; i++) {
            temp += history[i];
        }

        System.out.println("AVG ER " + (temp/total));
        System.out.println("TEMP " + temp  + " " + total);
        return temp / total;
    }

    public long saveNewAverage(Context context, double average, String type) {
        ProfileDataSource profileDataSource = new ProfileDataSource(context);
        profileDataSource.open();
        long id = profileDataSource.insertEvent("", type, average + "");
        profileDataSource.close();
        return id;
    }

    public ProfileEvent getOldAverage(Context context, String type) {
        ProfileDataSource profileDataSource = new ProfileDataSource(context);
        profileDataSource.open();
        ProfileEvent profileEvent = profileDataSource.getSpecificEvent(type);
        profileDataSource.close();
        return profileEvent;

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

    public ArrayList<String> getDisharmonyApps() {
        return disharmonyApps;
    }
}
