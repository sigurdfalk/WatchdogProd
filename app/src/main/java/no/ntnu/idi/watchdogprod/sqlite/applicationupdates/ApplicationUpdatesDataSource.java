package no.ntnu.idi.watchdogprod.sqlite.applicationupdates;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import no.ntnu.idi.watchdogprod.domain.AppInfo;

/**
 * Created by sigurdhf on 10.03.2015.
 */
public class ApplicationUpdatesDataSource {
    private SQLiteDatabase db;
    private ApplicationUpdatesSQLiteHelper dbHelper;
    private String[] allColumns = {ApplicationUpdatesSQLiteHelper.COLUMN_ID,
        ApplicationUpdatesSQLiteHelper.COLUMN_PACKAGE_NAME,
        ApplicationUpdatesSQLiteHelper.COLUMN_PERMISSIONS,
        ApplicationUpdatesSQLiteHelper.COLUMN_VERSION_CODE,
        ApplicationUpdatesSQLiteHelper.COLUMN_LAST_UPDATE_TIME};

    public ApplicationUpdatesDataSource(Context context) {
        dbHelper = new ApplicationUpdatesSQLiteHelper(context);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public AppInfo insertApplicationUpdate(PackageInfo packageInfo) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ApplicationUpdatesSQLiteHelper.COLUMN_PACKAGE_NAME, packageInfo.packageName);
        contentValues.put(ApplicationUpdatesSQLiteHelper.COLUMN_PERMISSIONS, getPermissionsString(packageInfo.requestedPermissions != null ? packageInfo.requestedPermissions : new String[]{}));
        contentValues.put(ApplicationUpdatesSQLiteHelper.COLUMN_VERSION_CODE, packageInfo.versionCode);
        contentValues.put(ApplicationUpdatesSQLiteHelper.COLUMN_LAST_UPDATE_TIME, packageInfo.lastUpdateTime);

        long insertId = db.insert(ApplicationUpdatesSQLiteHelper.TABLE_APPLICATION_UPDATES, null, contentValues);
        Cursor cursor = db.query(ApplicationUpdatesSQLiteHelper.TABLE_APPLICATION_UPDATES,
                allColumns, ApplicationUpdatesSQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        AppInfo newAppInfo = cursorToAppInfo(cursor);
        cursor.close();

        return newAppInfo;
    }

    public ArrayList<AppInfo> getAllApplicationUpdates() {

        Cursor cursor = db.query(ApplicationUpdatesSQLiteHelper.TABLE_APPLICATION_UPDATES,
                allColumns, null, null, null, null, null);

        return cursorToAppInfoList(cursor);
    }

    public ArrayList<AppInfo> getApplicationUpdatesByPackageName(String packageName) {

        Cursor cursor = db.query(ApplicationUpdatesSQLiteHelper.TABLE_APPLICATION_UPDATES,
                allColumns, ApplicationUpdatesSQLiteHelper.COLUMN_PACKAGE_NAME + "=?",
                new String[]{packageName}, null, null, null);

        return cursorToAppInfoList(cursor);
    }

    private String getPermissionsString(String[] permissions) {
        StringBuilder permStr = new StringBuilder();

        for (int i = 0; i < permissions.length; i++) {
            permStr.append(permissions[i]);

            if (i < (permissions.length - 1)) {
                permStr.append(":");
            }
        }

        return permStr.toString();
    }

    private String[] getPermissionsArray(String permissionsString) {
        return permissionsString.split(":");
    }

    private ArrayList<AppInfo> cursorToAppInfoList(Cursor cursor) {
        ArrayList<AppInfo> applicationUpdates = new ArrayList<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            AppInfo appInfo = cursorToAppInfo(cursor);
            applicationUpdates.add(appInfo);
            cursor.moveToNext();
        }
        cursor.close();

        return applicationUpdates;
    }

    private AppInfo cursorToAppInfo(Cursor cursor) {
        return new AppInfo(cursor.getLong(0), cursor.getString(1), getPermissionsArray(cursor.getString(2)), cursor.getInt(3), cursor.getLong(4));
    }
}
