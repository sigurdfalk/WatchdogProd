package no.ntnu.idi.watchdogprod.sqlite.profile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import no.ntnu.idi.watchdogprod.privacyProfile.Profile;

/**
 * Created by fredsten on 20.03.2015.
 */
public class ProfileDataSource {
    private SQLiteDatabase db;
    private SQLiteOpenHelperProfile dbHelper;
    private String[] allColumns = {SQLiteOpenHelperProfile.COLUMN_ID_PROFILE, SQLiteOpenHelperProfile.COLUMN_TIMESTAMP_PROFILE, SQLiteOpenHelperProfile.COLUMN_EVENT,
            SQLiteOpenHelperProfile.COLUMN_VALUE, SQLiteOpenHelperProfile.COLUMN_APP_PACKAGE};

    public ProfileDataSource(Context context) {
        this.dbHelper = new SQLiteOpenHelperProfile(context);
    }

    public void open() throws SQLException {
        this.db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insert(String packageName, String event, String value) {
        ContentValues values = new ContentValues();
        values.put(SQLiteOpenHelperProfile.COLUMN_APP_PACKAGE, packageName);
        values.put(SQLiteOpenHelperProfile.COLUMN_TIMESTAMP_PROFILE, new SimpleDateFormat("dd-M-yyyy hh:mm:ss").format(new Date()));
        values.put(SQLiteOpenHelperProfile.COLUMN_EVENT, event);
        values.put(SQLiteOpenHelperProfile.COLUMN_VALUE, value);
        return db.insert(SQLiteOpenHelperProfile.TABLE_PROFILE, null,
                values);
    }

    public ArrayList getEventsForApp(String packageName) {
        ArrayList<ProfileEvent> events = new ArrayList<>();
        Cursor cursor = db.query(SQLiteOpenHelperProfile.TABLE_PROFILE, allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            if (cursor.getString(4).equals(packageName)) {
                events.add(new ProfileEvent(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)));
            }
        }
        return events;
    }


}
