package no.ntnu.idi.watchdogprod.sqlite.profile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import no.ntnu.idi.watchdogprod.domain.ProfileEvent;
import no.ntnu.idi.watchdogprod.privacyProfile.Profile;

/**
 * Created by fredsten on 20.03.2015.
 */
public class ProfileDataSource {
    private SQLiteDatabase db;
    private ProfileSQLiteOpenHelper dbHelper;
    private String[] allColumns = {ProfileSQLiteOpenHelper.COLUMN_ID_PROFILE, ProfileSQLiteOpenHelper.COLUMN_TIMESTAMP_PROFILE, ProfileSQLiteOpenHelper.COLUMN_EVENT,
            ProfileSQLiteOpenHelper.COLUMN_VALUE, ProfileSQLiteOpenHelper.COLUMN_APP_PACKAGE};

    public ProfileDataSource(Context context) {
        this.dbHelper = new ProfileSQLiteOpenHelper(context);
    }

    public void open() throws SQLException {
        this.db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertEvent(String packageName, String event, String value) {
        ContentValues values = new ContentValues();
        values.put(ProfileSQLiteOpenHelper.COLUMN_APP_PACKAGE, packageName);
        values.put(ProfileSQLiteOpenHelper.COLUMN_TIMESTAMP_PROFILE, new SimpleDateFormat("dd-M-yyyy hh:mm:ss").format(new Date()));
        values.put(ProfileSQLiteOpenHelper.COLUMN_EVENT, event);
        values.put(ProfileSQLiteOpenHelper.COLUMN_VALUE, value);
        return db.insert(ProfileSQLiteOpenHelper.TABLE_PROFILE_EVENT, null,
                values);
    }

    public boolean isInDatabase(String packageName, String type) {
        Cursor cursor = db.query(ProfileSQLiteOpenHelper.TABLE_PROFILE_EVENT, allColumns, "event=? AND package=?", new String[]{type, packageName}, null, null, null);
        return cursor.getCount() > 0 ? true : false;
    }

    public double[] getInstallData(String type) {
        double[] appValues;
        int counter = 0;
        Cursor cursor = db.query(ProfileSQLiteOpenHelper.TABLE_PROFILE_EVENT, allColumns, "event=?", new String[]{type}, null, null, "_id DESC");
        cursor.moveToFirst();
        if (cursor.getCount() > 10) {
            appValues = new double[10];
        } else {
            appValues = new double[cursor.getCount()];
        }

        while (!cursor.isAfterLast() && counter < 10) {
            appValues[counter++] = Double.parseDouble(cursor.getString(3));
            cursor.moveToNext();
        }

        return appValues;
    }

    public ArrayList<ProfileEvent> getInstalledApps() {
        ArrayList<ProfileEvent> profileEvents = new ArrayList<>();
        Cursor cursor = db.query(ProfileSQLiteOpenHelper.TABLE_PROFILE_EVENT, allColumns, "event=?", new String[]{Profile.INSTALLED_DANGEROUS_APP}, null, null, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                profileEvents.add(new ProfileEvent(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)));
                cursor.moveToNext();
            }
        }
        return profileEvents;
    }

    public ProfileEvent getSpecificEvent(String eventType) {
        Cursor cursor = db.query(ProfileSQLiteOpenHelper.TABLE_PROFILE_EVENT, allColumns, "event=?", new String[]{eventType}, null, null, "_id DESC");
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            return new ProfileEvent(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        } else
            return null;
    }

    public ProfileEvent getSpecificEventForApp(String eventType, String packageName) {
        ProfileEvent profileEvent = null;
        Cursor cursor = db.query(ProfileSQLiteOpenHelper.TABLE_PROFILE_EVENT, allColumns, "event=? AND package=?", new String[]{eventType, packageName}, null, null, null);
        if (cursor != null && cursor.getCount() == 1) {
            cursor.moveToFirst();
            profileEvent = new ProfileEvent(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        } else {
            System.out.println("Returned null");
        }
        return profileEvent;
    }

    public int deleteEvent(String eventType, String packageName) {
        return db.delete(ProfileSQLiteOpenHelper.TABLE_PROFILE_EVENT, "event=? AND package=?", new String[]{eventType, packageName});
    }

    public long insertUserQuestions(double q1, double q2, double q3) {
        ContentValues values = new ContentValues();
        values.put(ProfileSQLiteOpenHelper.COLUMN_TIMESTAMP_PROFILE, new SimpleDateFormat("dd-M-yyyy hh:mm:ss").format(new Date()));
        values.put(ProfileSQLiteOpenHelper.COLUMN_QUESTION, "questions here");
        values.put(ProfileSQLiteOpenHelper.COLUMN_ANSWER, q1 + "," + q2 + "," + q3);
        return db.insert(ProfileSQLiteOpenHelper.TABLE_PROFILE_USERQUESTIONS, null, values);
    }

    public double[] getUserQuestions() {
        String temp[] = null;
        Cursor cursor = db.query(ProfileSQLiteOpenHelper.TABLE_PROFILE_USERQUESTIONS, allColumns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            temp = cursor.getString(3).split(",");
            double answers[] = new double[temp.length];
            for (int i = 0; i < answers.length; i++) {
                answers[i] = Double.parseDouble(temp[i]);
            }
            return answers;
        }
        return null;
    }
}
