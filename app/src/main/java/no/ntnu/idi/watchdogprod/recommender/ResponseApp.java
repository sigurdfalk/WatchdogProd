package no.ntnu.idi.watchdogprod.recommender;

import org.apache.mahout.common.distance.EuclideanDistanceMeasure;

import java.io.Serializable;
import java.util.ArrayList;

import no.ntnu.idi.watchdogprod.domain.Rule;
import no.ntnu.idi.watchdogprod.privacyProfile.Profile;

/**
 * Created by Wschive on 22/04/15.
 */
public class ResponseApp implements Comparable<ResponseApp>{
    private String name;
    private String packageName;
    private String[] permissions;
    private String logo;
    private String infoLine;
    private AppVector vector;
    private double privacyScore;
    private double distanceFromProfile;

    public ArrayList<Rule> getViolatedRules() {
        return violatedRules;
    }

    public void setViolatedRules(ArrayList<Rule> violatedRules) {
        this.violatedRules = violatedRules;
    }

    private ArrayList<Rule> violatedRules;

    public String getInfoLine() {
        return infoLine;
    }

    public void setInfoLine(String infoLine) {
        this.infoLine = infoLine;
    }

    public double getPrivacyScore() {
        return privacyScore;
    }

    public void setPrivacyScore(double privacyScore) {
        this.privacyScore = privacyScore;
    }

    public ResponseApp(String name, String[] permissions, String logo, String infoLine, String packageName) {
        this.name = name;
        this.infoLine = infoLine;
        this.permissions = permissions;
        this.logo = logo;
        this.vector = new AppVector(permissions);
        this.distanceFromProfile = calculateDistance();
        this.packageName = packageName;
    }
    public ResponseApp(){    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getPermissions() {
        return permissions;
    }

    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    private double calculateDistance(){
        EuclideanDistanceMeasure measure = new EuclideanDistanceMeasure();
        double distance = measure.distance(Profile.getInstance().getProfileVector(),this.vector);
        return distance;
    }

    @Override
    public int compareTo(ResponseApp another) {
        if (this.distanceFromProfile > another.distanceFromProfile){
            return 1;
        }
        else if (this.distanceFromProfile < another.distanceFromProfile){
            return -1;
        }


        return 0;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    public void calculate(){
        this.vector = new AppVector(this.permissions);
        this.distanceFromProfile = calculateDistance();

    }

}
