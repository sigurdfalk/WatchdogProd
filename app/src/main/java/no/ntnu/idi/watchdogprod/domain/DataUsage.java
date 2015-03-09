package no.ntnu.idi.watchdogprod.domain;

import java.util.ArrayList;

/**
 * Created by fredsten on 09.03.2015.
 */
public class DataUsage {
    private String packageName;
    private Long lastdown;
    private Long lastup;
    private ArrayList<Long> appDataValuesDownForeground;
    private ArrayList<Long> appDataValuesDownBackground;
    private ArrayList<Long> appDataValuesUpForeground;
    private ArrayList<Long> appDataValuesUpBackground;

    public DataUsage(String packageName) {
        this.packageName = packageName;
        this.appDataValuesDownForeground = new ArrayList<>();
        this.appDataValuesDownBackground = new ArrayList<>();
        this.appDataValuesUpForeground = new ArrayList<>();
        this.appDataValuesUpBackground = new ArrayList<>();
    }

    public Long getLastdown() {
        return lastdown;
    }

    public void setLastdown(long lastdown) {
        this.lastdown = lastdown;
    }

    public Long getLastup() {
        return lastup;
    }

    public void setLastup(long lastup) {
        this.lastup = lastup;
    }

    public void addToValuesDownForeground(Long value) {
        appDataValuesDownForeground.add(value);
    }

    public void addToValuesDownBackground(Long value) {
        appDataValuesDownBackground.add(value);
    }

    public void addToValuesUpForeground(Long value) {
        appDataValuesUpForeground.add(value);
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    public void addToValuesUpBackground(Long value) {
        appDataValuesUpBackground.add(value);
    }

    public ArrayList<Long> getAppDataValuesDownForeground() {
        return appDataValuesDownForeground;
    }

    public void setAppDataValuesDownForeground(ArrayList<Long> appDataValuesDownForeground) {
        this.appDataValuesDownForeground = appDataValuesDownForeground;
    }

    public ArrayList<Long> getAppDataValuesDownBackground() {
        return appDataValuesDownBackground;
    }

    public void setAppDataValuesDownBackground(ArrayList<Long> appDataValuesDownBackground) {
        this.appDataValuesDownBackground = appDataValuesDownBackground;
    }

    public ArrayList<Long> getAppDataValuesUpForeground() {
        return appDataValuesUpForeground;
    }

    public void setAppDataValuesUpForeground(ArrayList<Long> appDataValuesUpForeground) {
        this.appDataValuesUpForeground = appDataValuesUpForeground;
    }

    public ArrayList<Long> getAppDataValuesUpBackground() {
        return appDataValuesUpBackground;
    }

    public void setAppDataValuesUpBackground(ArrayList<Long> appDataValuesUpBackground) {
        this.appDataValuesUpBackground = appDataValuesUpBackground;
    }

    public Long getTotalDownForeground() {
        Long total = new Long(0);
        if(null != appDataValuesDownForeground && appDataValuesDownForeground.size() > 1 ) {
            //appDataValuesDownForeground.remove(0);
            for (Long data : appDataValuesDownForeground) {
                total += data;
            }
        }
        return total;
    }

    public Long getTotalUpForeground() {
        Long total = new Long(0);
        if(null != appDataValuesUpForeground && appDataValuesUpForeground.size() > 1 ) {
            //appDataValuesUpForeground.remove(0);
            for (Long data : appDataValuesUpForeground) {
                total += data;
            }
        }
        return total;
    }

    public Long getTotalDownBackground() {
        Long total = new Long(0);
        if(null != appDataValuesDownBackground && appDataValuesDownBackground.size() > 1 ) {
            //appDataValuesDownBackground.remove(0);
            for (Long data : appDataValuesDownBackground) {
                total += data;
            }
        }
        return total;
    }

    public Long getTotalUpBackground() {
        Long total = new Long(0);
        if(null != appDataValuesUpBackground && appDataValuesUpBackground.size() > 1 ) {
            //appDataValuesUpBackground.remove(0);
            for (Long data : appDataValuesUpBackground) {
                total += data;
            }
        }
        return total;
    }
}
