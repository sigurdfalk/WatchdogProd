package no.ntnu.idi.watchdogprod.recommender;

import org.apache.mahout.common.distance.EuclideanDistanceMeasure;

import no.ntnu.idi.watchdogprod.privacyProfile.Profile;

/**
 * Created by Wschive on 22/04/15.
 */
public class ResponseApp implements Comparable<ResponseApp>{
    private String packageName;
    private String[] permissions;
    private String logo;
    private String storeUrl;
    private AppVector vector;
    private double distanceFromProfile;

    public ResponseApp(String packageName, String[] permissions, String logo, String storeUrl) {
        this.packageName = packageName;
        this.permissions = permissions;
        this.logo = logo;
        this.storeUrl = storeUrl;
        this.vector = new AppVector(permissions);
        this.distanceFromProfile = calculateDistance();
    }
    public ResponseApp(){    }
    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
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

    public String getStoreUrl() {
        return storeUrl;
    }

    public void setStoreUrl(String storeUrl) {
        this.storeUrl = storeUrl;
    }
    private double calculateDistance(){
        EuclideanDistanceMeasure measure = new EuclideanDistanceMeasure();
        return measure.distance(Profile.getInstance().getProfileVector(),this.vector);
    }

    @Override
    public int compareTo(ResponseApp another) {
        if (this.distanceFromProfile > another.distanceFromProfile){
            return -1;
        }
        else if (this.distanceFromProfile < another.distanceFromProfile){
            return 1;
        }


        return 0;
    }
}
