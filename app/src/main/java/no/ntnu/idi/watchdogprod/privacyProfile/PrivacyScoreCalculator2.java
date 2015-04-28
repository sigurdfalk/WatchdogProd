package no.ntnu.idi.watchdogprod.privacyProfile;

import android.content.Context;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import no.ntnu.idi.watchdogprod.domain.Answer;
import no.ntnu.idi.watchdogprod.domain.ExtendedPackageInfo;
import no.ntnu.idi.watchdogprod.domain.PermissionDescription;
import no.ntnu.idi.watchdogprod.domain.PermissionFact;
import no.ntnu.idi.watchdogprod.domain.Rule;
import no.ntnu.idi.watchdogprod.helpers.ApplicationHelper;
import no.ntnu.idi.watchdogprod.helpers.PermissionFactHelper;
import no.ntnu.idi.watchdogprod.helpers.PermissionHelper;
import no.ntnu.idi.watchdogprod.helpers.RuleHelper;
import no.ntnu.idi.watchdogprod.sqlite.answers.AnswersDataSource;

/**
 * Created by sigurdhf on 28.04.2015.
 */
public class PrivacyScoreCalculator2 {
    public static final int RISK_HIGH = 3;
    public static final int RISK_MEDIUM = 2;
    public static final int RISK_LOW = 1;

    public static final double PENALTY_RISK_HIGH = 5.0;
    public static final double PENALTY_RISK_MEDIUM = 3.0;
    public static final double PENALTY_RISK_LOW = 1.0;

    public static final double PENALTY_VIOLATED_RULE = 10.0;

    private static HashMap<PermissionDescription, Double> permissionWeights;

    public static double calculatePrivacyScore(Context context, ExtendedPackageInfo packageInfo) {
        if (permissionWeights == null || permissionWeights.size() == 0) {
            calculatePermissionWeights(context);
        }

        double score = 0.0;

        for (PermissionDescription permission : packageInfo.getPermissionDescriptions()) {
            double weight = permissionWeights.get(permission);
            double penalty = 0.0;

            switch (permission.getRisk()) {
                case RISK_LOW:
                    penalty = PENALTY_RISK_LOW;
                    break;
                case RISK_MEDIUM:
                    penalty = PENALTY_RISK_MEDIUM;
                    break;
                case RISK_HIGH:
                    penalty = PENALTY_RISK_HIGH;
                    break;
            }

            score += (penalty * weight);
        }

        ArrayList<Rule> violatedRules = packageInfo.getViolatedRules();

        score += violatedRules.size() * PENALTY_VIOLATED_RULE;

        return hyperbolicTan(normalizeScore(context, score, 0.0, 3.0)) * 100.0;
    }

    private static double hyperbolicTan(double rawScore) {
        return 1 - (2 / (Math.pow(Math.E, 2 * rawScore) + 1));
    }

    private static double normalizeScore(Context context, double rawScore, double minRange, double maxRange) {
        double minScore = 0.0;
        double maxScore = calculateMaxScore(context);

        double normalizedScore = (rawScore - minScore) / (maxScore - minScore);

        return (normalizedScore * maxRange) + minRange;
    }

    private static double calculateMaxScore(Context context) {
        double maxScore = 0.0;

        ArrayList<PermissionDescription> permissions = PermissionHelper.getAllPermissionDescriptions(context);

        for (PermissionDescription permission : permissions) {
            switch (permission.getRisk()) {
                case RISK_LOW:
                    maxScore += PENALTY_RISK_LOW;
                    break;
                case RISK_MEDIUM:
                    maxScore += PENALTY_RISK_MEDIUM;
                    break;
                case RISK_HIGH:
                    maxScore += PENALTY_RISK_HIGH;
                    break;
            }
        }

        ArrayList<Rule> rules = RuleHelper.getAllRules(context);

        maxScore += (rules.size() * PENALTY_VIOLATED_RULE);

        return maxScore;
    }

    public static void calculatePermissionWeights(Context context) {
        permissionWeights = new HashMap<>();

        ArrayList<PermissionDescription> permissions = PermissionHelper.getAllPermissionDescriptions(context);
        ArrayList<PermissionFact> facts = PermissionFactHelper.getAllPermissionFacts(context);
        ArrayList<Answer> answers = null;
        try {
            answers = getAllAnswers(context);
        } catch (SQLException e) {
            Log.e(PrivacyScoreCalculator2.class.getName(), "Unable to get answers from db", e);
            e.printStackTrace();
        }

        for (PermissionDescription permission : permissions) {
            double weight = getPermissionWeight(permission, answers, facts);
            permissionWeights.put(permission, weight);
        }
    }

    private static double getPermissionWeight(PermissionDescription permission, ArrayList<Answer> allAnswers, ArrayList<PermissionFact> facts) {
        if (allAnswers == null) {
            return 0.5;
        }

        PermissionFact matchingFact = getPermissionFactMatchingPermission(permission, facts);

        if (matchingFact == null) {
            return 0.5;
        }

        ArrayList<Answer> matchingAnswers = getAnswersByPermissionFact(matchingFact, allAnswers);

        if (matchingAnswers.size() == 0) {
            return 0.5;
        }

        double answerSum = 0.0;

        for (Answer answer : matchingAnswers) {
            switch (answer.getAnswer()) {
                case Answer.ANSWER_SAD:
                    answerSum += 1.0;
                    break;
                case Answer.ANSWER_NEUTRAL:
                    answerSum += 0.5;
                    break;
                case Answer.ANSWER_HAPPY:
                    answerSum += 0.0;
                    break;
            }
        }

        double weight = answerSum / matchingAnswers.size();

        System.out.println(weight);

        return weight;
    }

    private static ArrayList<Answer> getAnswersByPermissionFact(PermissionFact fact, ArrayList<Answer> allAnswers) {
        ArrayList<Answer> matchingAnswers = new ArrayList<>();

        for (Answer answer : allAnswers) {
            if (answer.getAnswerId() == fact.getId()) {
                matchingAnswers.add(answer);
            }
        }

        return matchingAnswers;
    }

    private static PermissionFact getPermissionFactMatchingPermission(PermissionDescription permission, ArrayList<PermissionFact> facts) {
        for (PermissionFact fact : facts) {
            if (fact.getPermissions()[0].equals(permission.getName())) {
                return fact;
            }
        }

        return null;
    }

    private static ArrayList<Answer> getAllAnswers(Context context) throws SQLException {
        AnswersDataSource answersDataSource = new AnswersDataSource(context);
        answersDataSource.open();
        ArrayList<Answer> allAnswers = answersDataSource.getAllAnswers();
        answersDataSource.close();
        return allAnswers;
    }
}
