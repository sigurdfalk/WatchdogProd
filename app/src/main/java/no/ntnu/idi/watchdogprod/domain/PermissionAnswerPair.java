package no.ntnu.idi.watchdogprod.domain;

/**
 * Created by fredsten on 27.04.2015.
 */
public class PermissionAnswerPair {
    private String permissionDesignation;
    private int answer;

    public PermissionAnswerPair(String permissionDesignation, int answer) {
        this.permissionDesignation = permissionDesignation;
        this.answer = answer;
    }

    public String getPermissionDesignation() {
        return permissionDesignation;
    }

    public int getAnswer() {
        return answer;
    }
}
