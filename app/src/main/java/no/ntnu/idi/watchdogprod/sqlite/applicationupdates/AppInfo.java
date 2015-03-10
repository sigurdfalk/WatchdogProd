package no.ntnu.idi.watchdogprod.sqlite.applicationupdates;

import java.util.Arrays;

/**
 * Created by sigurdhf on 10.03.2015.
 */
public class AppInfo {
    private long id;
    private String packageName;
    private String[] permissions;
    private int versionCode;
    private long lastUpdateTime;

    public AppInfo(long id, String packageName, String[] permissions, int versionCode, long lastUpdateTime) {
        this.id = id;
        this.packageName = packageName;
        this.permissions = permissions;
        this.versionCode = versionCode;
        this.lastUpdateTime = lastUpdateTime;
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
}
