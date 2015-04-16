package no.ntnu.idi.watchdogprod.helpers;

import android.content.Context;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import no.ntnu.idi.watchdogprod.domain.Answer;
import no.ntnu.idi.watchdogprod.domain.ExtendedPackageInfo;
import no.ntnu.idi.watchdogprod.domain.PermissionDescription;
import no.ntnu.idi.watchdogprod.domain.PermissionFact;
import no.ntnu.idi.watchdogprod.domain.Rule;
import no.ntnu.idi.watchdogprod.sqlite.answers.AnswersDataSource;

/**
 * Created by sigurdhf on 20.03.2015.
 */
public class PrivacyScoreCalculator {
    public static final int RISK_HIGH = 3;
    public static final int RISK_MEDIUM = 2;
    public static final int RISK_LOW = 1;

    public static final int HIGH_MULTIPLIER = 4;
    public static final int MEDIUM_MULTIPLIER = 2;
    public static final int LOW_MULTIPLIER = 1;

    public static final int HAS_HIGH_BONUS = 40;
    public static final int HAS_MEDIUM_BONUS = 20;

    public static final int MAX_SCORE = 100;

    public static final int LOW_THRESHOLD = 0;
    public static final int MEDIUM_THRESHOLD = 40;
    public static final int HIGH_THRESHOLD = 70;

    public static double calculateScore(ArrayList<PermissionDescription> permissions) {
        boolean hasMediumPermission = false;
        boolean hasHighPermission = false;

        int score = 0;

        for (PermissionDescription permission : permissions) {
            switch (permission.getRisk()) {
                case RISK_LOW:
                    score += LOW_MULTIPLIER;
                    break;
                case RISK_MEDIUM:
                    score += MEDIUM_MULTIPLIER;
                    hasMediumPermission = true;
                    break;
                case RISK_HIGH:
                    score += HIGH_MULTIPLIER;
                    hasHighPermission = true;
                    break;
            }
        }

        if (hasHighPermission) {
            score += HAS_HIGH_BONUS;
        } else if (hasMediumPermission) {
            score += HAS_MEDIUM_BONUS;
        }

        return (score > MAX_SCORE) ? MAX_SCORE : score;
    }

    public static double calculateScore(Context context, ExtendedPackageInfo packageInfo) throws SQLException {
        ArrayList<PermissionDescription> permissions = packageInfo.getPermissionDescriptions();
        ArrayList<PermissionFact> facts = packageInfo.getPermissionFacts();
        ArrayList<Rule> ruleViolations = packageInfo.getViolatedRules();

        AnswersDataSource answersDataSource = new AnswersDataSource(context);

        answersDataSource.open();
        ArrayList<Answer> allAnswers = answersDataSource.getAnswersByPackageName(packageInfo.getPackageInfo().packageName);
        answersDataSource.close();

        ArrayList<Answer> relevantAnswers = getRelevantAnswers(allAnswers, facts);

        double score = 0.0;

        // ToDo IMPLEMENT SCORE FFS!

        return score;
    }

    private static ArrayList<Answer> getRelevantAnswers(ArrayList<Answer> allAnswers, ArrayList<PermissionFact> facts) {
        ArrayList<Answer> relevantAnswers = new ArrayList<>();
        Collections.sort(allAnswers);

        for (PermissionFact fact : facts) {
            for (Answer answer : allAnswers) {
                if (answer.getAnswerId() == fact.getId()) {
                    relevantAnswers.add(answer);
                    break;
                }
            }
        }

        return relevantAnswers;
    }
}
