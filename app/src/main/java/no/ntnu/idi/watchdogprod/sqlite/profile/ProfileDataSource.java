package no.ntnu.idi.watchdogprod.sqlite.profile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by fredsten on 20.03.2015.
 */
public class ProfileDataSource {
    private SQLiteDatabase db;
    private SQLiteOpenHelperProfile dbHelper;
    private String[] allColumns = {SQLiteOpenHelperProfile.COLUMN_ID_PROFILE, SQLiteOpenHelperProfile.COLUMN_TIMESTAMP_PROFILE, SQLiteOpenHelperProfile.COLUMN_EVENT,
            SQLiteOpenHelperProfile.COLUMN_VALUE};

    public ProfileDataSource(Context context) {
        this.dbHelper = new SQLiteOpenHelperProfile(context);
    }

    public void open() throws SQLException {
        this.db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insert(String event, String value) {
        ContentValues values = new ContentValues();
        values.put(SQLiteOpenHelperProfile.COLUMN_TIMESTAMP_PROFILE, new SimpleDateFormat("dd-M-yyyy hh:mm:ss").format(new Date()));
        values.put(SQLiteOpenHelperProfile.COLUMN_EVENT, event);
        values.put(SQLiteOpenHelperProfile.COLUMN_VALUE, value);
        return db.insert(SQLiteOpenHelperProfile.TABLE_PROFILE, null,
                values);
    }
}
