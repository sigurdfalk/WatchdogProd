package no.ntnu.idi.watchdogprod.domain;

import java.util.ArrayList;

/**
 * Created by fredsten on 27.04.2015.
 */
public class DisharomyApplication {
    private ExtendedPackageInfo extendedPackageInfo;
    private ArrayList<PermissionAnswerPair> questionAnswerPairs;

    public DisharomyApplication(ExtendedPackageInfo extendedPackageInfo, ArrayList<PermissionAnswerPair> questionAnswerPairs) {
        this.extendedPackageInfo = extendedPackageInfo;
        this.questionAnswerPairs = questionAnswerPairs;
    }

    public ExtendedPackageInfo getExtendedPackageInfo() {
        return extendedPackageInfo;
    }

    public ArrayList<PermissionAnswerPair> getQuestionAnswerPairs() {
        return questionAnswerPairs;
    }

    public int getCount(int answerType) {
        int count = 0;
        for(PermissionAnswerPair permissionAnswerPair : questionAnswerPairs) {
            if(permissionAnswerPair.getAnswer() == answerType) {
                count++;
            }
        }
        return count;
    }
}
