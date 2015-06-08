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
import no.ntnu.idi.watchdogprod.helpers.PermissionFactHelperSingleton;
import no.ntnu.idi.watchdogprod.helpers.PermissionHelperSingleton;
import no.ntnu.idi.watchdogprod.helpers.RuleHelperSingleton;
import no.ntnu.idi.watchdogprod.sqlite.answers.AnswersDataSource;

/**
 * Created by sigurdhf on 28.04.2015.
 */
public class PrivacyScoreCalculator {
    public static final int RISK_HIGH = 3;
    public static final int RISK_MEDIUM = 2;
    public static final int RISK_LOW = 1;

    public static final double PENALTY_RISK_HIGH = 10.0;
    public static final double PENALTY_RISK_MEDIUM = 3.0;
    public static final double PENALTY_RISK_LOW = 1.0;

    public static final double PENALTY_VIOLATED_RULE = 10.0;

    public static final int MAX_SCORE = 100;

    public static final int LOW_THRESHOLD = 0;
    public static final int MEDIUM_THRESHOLD = 15;
    public static final int HIGH_THRESHOLD = 60;

    private static PrivacyScoreCalculator instance;

    private Context context;
    private HashMap<PermissionDescription, Double> permissionWeights;

    private PrivacyScoreCalculator(Context context) {
        this.context = context;
        this.permissionWeights = new HashMap<>();
        calculatePermissionWeights();
    }

    public static PrivacyScoreCalculator getInstance(Context context) {
        if (instance == null) {
            instance = new PrivacyScoreCalculator(context);
        }

        return instance;
    }

    public double calculatePrivacyScore(ExtendedPackageInfo packageInfo) {
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

        double normalizedScore = normalizeScore(score, 0.0, 1.0);

        double logistic = generalLogisticFunction(normalizedScore);

        double sigmoid = logisticFunction(score);

        System.out.println(";" + score + ";" + normalizedScore + ";" + getPermissionTypeCount(packageInfo.getPermissionDescriptions(), RISK_HIGH) + ";" + getPermissionTypeCount(packageInfo.getPermissionDescriptions(), RISK_MEDIUM) + ";" + getPermissionTypeCount(packageInfo.getPermissionDescriptions(), RISK_LOW) + ";" + violatedRules.size());

        double hypTan = hyperbolicTan(normalizedScore);
        double finalScore = sigmoid;

        return finalScore;
    }

    private double hyperbolicTan(double rawScore) {
        return 1 - (2 / (Math.pow(Math.E, 2 * rawScore) + 1));
    }

    private int getPermissionTypeCount(ArrayList<PermissionDescription> permissions, int type) {
        int count = 0;

        for (PermissionDescription permissionDescription : permissions) {
            if (permissionDescription.getRisk() == type) {
                count++;
            }
        }

        return count;
    }

    private double sigmoid(double input) {
        double output = 1 / (1 + Math.pow(Math.E, -input));

        return output;
    }

    private double logisticFunction(double input) {
        final double L = 100.0; // max output value
        final double X0 = 35.0; // curve midpoint
        final double k = 0.08; // steepness

        double output = L / (1 + Math.pow(Math.E, -k * (input - X0)));

        return output;
    }

    private double generalLogisticFunction(double input) {
        final double A = 0;
        final double K = 1;
        final double B = 2;
        final double v = 1;
        final double Q = 1;
        final double M = 3;

        double output = A + ((K - A) / (Math.pow(1 + (Q * Math.pow(Math.E, -B * (input - M))), 1 / v)));

        return output;
    }

    private double normalizeScore(double rawScore, double minRange, double maxRange) {
        double minScore = 0.0;
        double maxScore = calculateMaxScore();

        double normalizedScore = (rawScore - minScore) / (maxScore - minScore);

        return (normalizedScore * maxRange) + minRange;
    }

    private double calculateMaxScore() {
        double maxScore = 0.0;

        ArrayList<PermissionDescription> permissions = PermissionHelperSingleton.getInstance(context).getPermissionDescriptions();

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

        ArrayList<Rule> rules = RuleHelperSingleton.getInstance(context).getRules();

        maxScore += (rules.size() * PENALTY_VIOLATED_RULE);

        return maxScore;
    }

    public void calculatePermissionWeights() {
        permissionWeights = new HashMap<>();

        ArrayList<PermissionDescription> permissions = PermissionHelperSingleton.getInstance(context).getPermissionDescriptions();
        ArrayList<PermissionFact> facts = PermissionFactHelperSingleton.getInstance(context).getPermissionFacts();
        ArrayList<Answer> answers = null;
        try {
            answers = getAllAnswers();
        } catch (SQLException e) {
            Log.e(PrivacyScoreCalculator.class.getName(), "Unable to get answers from db", e);
            e.printStackTrace();
        }

        for (PermissionDescription permission : permissions) {
            double weight = getPermissionWeightSigmoid(permission, answers, facts);
            permissionWeights.put(permission, weight);
        }
    }

    private double getPermissionWeight(PermissionDescription permission, ArrayList<Answer> allAnswers, ArrayList<PermissionFact> facts) {
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
                    answerSum += 0.1;
                    break;
            }
        }

        double weight = answerSum / matchingAnswers.size();

        return weight;
    }

    private double getPermissionWeightSigmoid(PermissionDescription permission, ArrayList<Answer> allAnswers, ArrayList<PermissionFact> facts) {
        if (allAnswers == null) {
            return sigmoid(0);
        }

        PermissionFact matchingFact = getPermissionFactMatchingPermission(permission, facts);

        if (matchingFact == null) {
            return sigmoid(0);
        }

        ArrayList<Answer> matchingAnswers = getAnswersByPermissionFact(matchingFact, allAnswers);

        if (matchingAnswers.size() == 0) {
            return sigmoid(0);
        }

        int answerSum = 0;

        for (Answer answer : matchingAnswers) {
            switch (answer.getAnswer()) {
                case Answer.ANSWER_SAD:
                    answerSum += 1;
                    break;
                case Answer.ANSWER_NEUTRAL:
                    answerSum += 0;
                    break;
                case Answer.ANSWER_HAPPY:
                    answerSum -= 1;
                    break;
            }
        }

        return sigmoid((double) answerSum);
    }

    private ArrayList<Answer> getAnswersByPermissionFact(PermissionFact fact, ArrayList<Answer> allAnswers) {
        ArrayList<Answer> matchingAnswers = new ArrayList<>();

        for (Answer answer : allAnswers) {
            if (answer.getAnswerId() == fact.getId()) {
                matchingAnswers.add(answer);
            }
        }

        return matchingAnswers;
    }

    private PermissionFact getPermissionFactMatchingPermission(PermissionDescription permission, ArrayList<PermissionFact> facts) {
        for (PermissionFact fact : facts) {
            if (fact.getPermissions()[0].equals(permission.getName())) {
                return fact;
            }
        }

        return null;
    }

    private ArrayList<Answer> getAllAnswers() throws SQLException {
        AnswersDataSource answersDataSource = new AnswersDataSource(context);
        answersDataSource.open();
        ArrayList<Answer> allAnswers = answersDataSource.getAllAnswers();
        answersDataSource.close();
        return allAnswers;
    }
}
