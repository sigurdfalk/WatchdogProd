package no.ntnu.idi.watchdogprod.sqlite.profile;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by fredsten on 20.03.2015.
 */
public class ProfileSQLiteOpenHelper extends SQLiteOpenHelper {

    public static final String TABLE_PROFILE_EVENT = "profileevent";
    public static final String COLUMN_ID_PROFILE = "_id";
    public static final String COLUMN_TIMESTAMP_PROFILE = "timestamp";
    public static final String COLUMN_EVENT = "event";
    public static final String COLUMN_VALUE = "value";
    public static final String COLUMN_APP_PACKAGE = "package";

    public static final String TABLE_PROFILE_USERQUESTIONS = "userquestions";
    public static final String COLUMN_QUESTION = "question";
    public static final String COLUMN_ANSWER = "answer";

    private static final String DATABASE_NAME_PROFILE = "profile.db";
    private static final int DATABASE_VERSION_PROFILE = 1;

    private static final String CREATE_TABLE_EVENTS = "create table "
            + TABLE_PROFILE_EVENT + "(" + COLUMN_ID_PROFILE
            + " integer primary key autoincrement, " + COLUMN_TIMESTAMP_PROFILE
            + " text not null, " + COLUMN_EVENT + " text not null, " + COLUMN_VALUE + " text not null, " + COLUMN_APP_PACKAGE + " text not null);";

    private static final String CREATE_TABLE_USERQUESTIONS = "create table "
            + TABLE_PROFILE_USERQUESTIONS + "(" + COLUMN_ID_PROFILE
            + " integer primary key autoincrement, " + COLUMN_TIMESTAMP_PROFILE
            + " text not null, " + COLUMN_QUESTION + " text not null, " + COLUMN_ANSWER + " text not null);";

    public ProfileSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME_PROFILE, null, DATABASE_VERSION_PROFILE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_EVENTS);
        db.execSQL(CREATE_TABLE_USERQUESTIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(ProfileSQLiteOpenHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE_EVENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE_USERQUESTIONS);
        onCreate(db);
    }
}
