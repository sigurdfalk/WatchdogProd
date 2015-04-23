package no.ntnu.idi.watchdogprod.privacyProfile;

import android.content.Context;

import java.lang.reflect.Array;
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

        return score;
        //return (score > MAX_SCORE) ? MAX_SCORE : score;
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
        Collections.sort(relevantAnswers);

        double totalScore = 0.0;
        double totalWeight = 0.0;

        for (PermissionDescription permission : permissions) {
            double weight = getPermissionWeight(permission.getName(), relevantAnswers, facts);
            totalWeight += weight;

            switch (permission.getRisk()) {
                case RISK_LOW:
                    totalScore += 1 * weight;
                    break;
                case RISK_MEDIUM:
                    totalScore += 2 * weight;
                    break;
                case RISK_HIGH:
                    totalScore += 4 * weight;
                    break;
            }
        }

        //totalScore = totalScore / totalWeight;

        //return (totalScore > MAX_SCORE) ? MAX_SCORE : totalScore;
        return totalScore;
    }

    private static double getPermissionWeight(String permission, ArrayList<Answer> answers, ArrayList<PermissionFact> facts) {
        PermissionFact matchingFact = null;

        for (PermissionFact fact : facts) {
            if (fact.getPermissions().length == 1 && fact.getPermissions()[0].contains(permission)) {
                matchingFact = fact;
                break;
            }
        }

        if (matchingFact == null) {
            return 2.0;
        }

        for (Answer answer : answers) {
            if (answer.getAnswerId() == matchingFact.getId()) {
                switch (answer.getAnswer()) {
                    case Answer.ANSWER_HAPPY:
                        return 1.0;
                    case Answer.ANSWER_NEUTRAL:
                        return 2.0;
                    case Answer.ANSWER_SAD:
                        return 3.0;
                }
            }
        }

        return 2.0;
    }

    private static double getPermissionsScore(ArrayList<PermissionDescription> permissions) {
        double score = 0.0;


        return score;
    }

    private static double getRuleViolationsScore(ArrayList<Rule> ruleViolations) {
        double score = 0.0;


        return score;
    }

    private static double getAnswersScore(ArrayList<Answer> relevantAnswers) {
        double score = 0.0;


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
