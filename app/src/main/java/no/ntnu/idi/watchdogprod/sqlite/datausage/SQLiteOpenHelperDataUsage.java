package no.ntnu.idi.watchdogprod.sqlite.datausage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by fredsten on 09.03.2015.
 */
public class SQLiteOpenHelperDataUsage extends SQLiteOpenHelper {
    public static final String TABLE_DATA = "data";
    public static final String COLUMN_ID_DATA = "_id";
    public static final String COLUMN_TIMESTAMP_DATA = "timestamp";
    public static final String COLUMN_INFO_DATA = "data_info";
    public static final String COLUMN_AMOUNT_DOWNFOREGROUND = "amountDownForeground";
    public static final String COLUMN_AMOUNT_DOWNBACKGROUND = "amountDownBackground";
    public static final String COLUMN_AMOUNT_UPFOREGROUND = "amountUpForeground";
    public static final String COLUMN_AMOUNT_UPBACKGROUND = "amountUpBackground";

    private static final String DATABASE_NAME_DATA = "data.db";
    private static final int DATABASE_VERSION_DATA = 1;

    private static final String DATABASE_CREATE_DATA = "create table "
            + TABLE_DATA + "(" + COLUMN_ID_DATA
            + " integer primary key autoincrement, " + COLUMN_TIMESTAMP_DATA
            + " text not null, " + COLUMN_INFO_DATA + " text not null, " + COLUMN_AMOUNT_DOWNFOREGROUND + " text not null," + COLUMN_AMOUNT_DOWNBACKGROUND + " text not null, " + COLUMN_AMOUNT_UPFOREGROUND + " text not null, " + COLUMN_AMOUNT_UPBACKGROUND + " text not null);";

    public SQLiteOpenHelperDataUsage(Context context) {
        super(context, DATABASE_NAME_DATA, null, DATABASE_VERSION_DATA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_DATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLiteOpenHelperDataUsage.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA);
        onCreate(db);
    }
}
