package no.ntnu.idi.watchdogprod.sqlite.applicationupdates;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by sigurdhf on 10.03.2015.
 */
public class ApplicationUpdatesSQLiteHelper extends SQLiteOpenHelper {
    static final String TABLE_APPLICATION_UPDATES = "applicationUpdates";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PACKAGE_NAME = "packageName";
    public static final String COLUMN_PERMISSIONS = "permissions";
    public static final String COLUMN_VERSION_CODE = "versionCode";
    public static final String COLUMN_LAST_UPDATE_TIME = "lastUpdateTime";

    public static final String DATABASE_NAME = "applicationupdates.db";
    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_CREATE = "create table "
            + TABLE_APPLICATION_UPDATES + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_PACKAGE_NAME + " text not null, "
            + COLUMN_PERMISSIONS + " text not null, "
            + COLUMN_VERSION_CODE + " integer not null, "
            + COLUMN_LAST_UPDATE_TIME + " integer not null);";

    public ApplicationUpdatesSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(ApplicationUpdatesSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPLICATION_UPDATES);
        onCreate(db);
    }
}
