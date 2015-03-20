package no.ntnu.idi.watchdogprod;

import java.util.ArrayList;

/**
 * Created by sigurdhf on 20.03.2015.
 */
public class PrivacyScoreCalculator {

    public static double calculateScore(ArrayList<PermissionDescription> permissions) {
        int score = 0;

        for (PermissionDescription permission : permissions) {
            switch (permission.getRisk()) {
                case 1:
                    score += permission.getRisk();
                    break;
                case 2:
                    score += permission.getRisk() * 5;
                    break;
                case 3:
                    score += permission.getRisk() * 10;
                    break;
            }
        }

        return score;
    }
}
