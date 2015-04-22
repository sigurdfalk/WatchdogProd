package no.ntnu.idi.watchdogprod.domain;

import org.apache.mahout.math.RandomAccessSparseVector;

import java.util.Arrays;
import java.util.Date;

import no.ntnu.idi.watchdogprod.recommender.AppVector;

/**
 * Created by sigurdhf on 10.03.2015.
 */
public class AppInfo implements Comparable<AppInfo> {
    private long id;
    private String packageName;
    private String[] permissions;
    private int versionCode;
    private long lastUpdateTime;
    private AppVector vector;

    public AppInfo(long id, String packageName, String[] permissions, int versionCode, long lastUpdateTime) {
        this.id = id;
        this.packageName = packageName;
        this.permissions = permissions;
        this.versionCode = versionCode;
        this.lastUpdateTime = lastUpdateTime;
        makeVector();
    }

    public AppInfo(String packageName, String[] permissions, int versionCode, long lastUpdateTime) {
        this(-1, packageName, permissions, versionCode, lastUpdateTime);
    }




    public long getId() {
        return id;
    }

    public String getPackageName() {
        return packageName;
    }

    public String[] getPermissions() {
        return permissions;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "id=" + id +
                ", packageName='" + packageName + '\'' +
                ", permissions=" + Arrays.toString(permissions) +
                ", versionCode=" + versionCode +
                ", lastUpdateTime=" + lastUpdateTime +
                '}';
    }

    @Override
    public int compareTo(AppInfo another) {
        Date thisDate = new Date(this.getLastUpdateTime());
        Date anotherDate = new Date(another.getLastUpdateTime());

        if (thisDate.before(anotherDate)) {
            return 1;
        } else if (thisDate.after(anotherDate)) {
            return -1;
        }

        return 0;
    }
    private void makeVector(){

    }
}
