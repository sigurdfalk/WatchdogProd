package no.ntnu.idi.watchdogprod.recommender;

/**
 * Created by Wschive on 20/04/15.
 */
public enum Permissions {
    ACCESS_COARSE_LOCATION(0);

    private final int index;
    Permissions(int index) {
        this.index = index;
    }
    int index(){return index;}
}
