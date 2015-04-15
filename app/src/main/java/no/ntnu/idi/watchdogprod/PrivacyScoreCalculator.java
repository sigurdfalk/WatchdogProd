package no.ntnu.idi.watchdogprod;

import java.util.ArrayList;

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
    public static double permissionScore(PermissionDescription permission){
        int score = 0;
        switch (permission.getRisk()) {
            case RISK_LOW:
                score += LOW_MULTIPLIER;
                break;
            case RISK_MEDIUM:
                score += MEDIUM_MULTIPLIER;
                break;
            case RISK_HIGH:
                score += HIGH_MULTIPLIER;
                break;
        }
        return score;
    }
}
