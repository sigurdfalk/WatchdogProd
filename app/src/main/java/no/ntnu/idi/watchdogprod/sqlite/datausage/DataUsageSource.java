package no.ntnu.idi.watchdogprod.sqlite.datausage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import no.ntnu.idi.watchdogprod.domain.DataLog;

/**
 * Created by fredsten on 09.03.2015.
 */
public class DataUsageSource {
    private SQLiteDatabase db;
    private SQLiteOpenHelperDataUsage dbHelper;
    private String[] allColumns = {SQLiteOpenHelperDataUsage.COLUMN_ID_DATA, SQLiteOpenHelperDataUsage.COLUMN_TIMESTAMP_DATA, SQLiteOpenHelperDataUsage.COLUMN_INFO_DATA,
            SQLiteOpenHelperDataUsage.COLUMN_AMOUNT_DOWNFOREGROUND, SQLiteOpenHelperDataUsage.COLUMN_AMOUNT_DOWNBACKGROUND, SQLiteOpenHelperDataUsage.COLUMN_AMOUNT_UPFOREGROUND, SQLiteOpenHelperDataUsage.COLUMN_AMOUNT_UPBACKGROUND};
    public DataUsageSource(Context context) {
        this.dbHelper = new SQLiteOpenHelperDataUsage(context);
    }

    public void open() throws SQLException {
        this.db = dbHelper.getWritableDatabase();
    }

    public DataLog insert(DataLog datalog) {
        ContentValues values = new ContentValues();
        values.put(SQLiteOpenHelperDataUsage.COLUMN_TIMESTAMP_DATA, new SimpleDateFormat("dd-M-yyyy hh:mm:ss").format(new Date()));
        values.put(SQLiteOpenHelperDataUsage.COLUMN_INFO_DATA, datalog.getDataInfo());
        values.put(SQLiteOpenHelperDataUsage.COLUMN_AMOUNT_DOWNFOREGROUND, datalog.getAmountDownForeground());
        values.put(SQLiteOpenHelperDataUsage.COLUMN_AMOUNT_DOWNBACKGROUND, datalog.getAmountDownBackground());
        values.put(SQLiteOpenHelperDataUsage.COLUMN_AMOUNT_UPFOREGROUND, datalog.getAmountUpForeground());
        values.put(SQLiteOpenHelperDataUsage.COLUMN_AMOUNT_UPBACKGROUND, datalog.getAmountUpBackground());
        long insertId = db.insert(SQLiteOpenHelperDataUsage.TABLE_DATA, null,
                values);
        Cursor cursor = db.query(SQLiteOpenHelperDataUsage.TABLE_DATA,
                allColumns, SQLiteOpenHelperDataUsage.COLUMN_ID_DATA + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        DataLog newDataLog = cursorToDataLog(cursor);
        cursor.close();
        System.out.println("LA INN " + newDataLog.getTimestamp());
        return newDataLog;
    }

    private DataLog cursorToDataLog(Cursor cursor) {
        DataLog dataLog = new DataLog(cursor.getLong(0), cursor.getString(1), cursor.getString(2), Long.parseLong(cursor.getString(3)), Long.parseLong(cursor.getString(4)), Long.parseLong(cursor.getString(5)), Long.parseLong(cursor.getString(6)));
        return dataLog;
    }

    public void PrintDatabase() {
        Cursor cursor = db.query(SQLiteOpenHelperDataUsage.TABLE_DATA, allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            System.out.println("DATE " + cursor.getString(1) + " DATATEST " + cursor.getLong(3));
        }
    }

    public ArrayList<DataLog> getDataLogsForApp(String packageName) {
        ArrayList<DataLog> datalogs = new ArrayList<>();
        Cursor cursor = db.query(SQLiteOpenHelperDataUsage.TABLE_DATA, allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            if (cursor.getString(2).equals(packageName)) {
                datalogs.add(new DataLog(cursor.getLong(0), cursor.getString(1), cursor.getString(2), Long.parseLong(cursor.getString(3)), Long.parseLong(cursor.getString(4)), Long.parseLong(cursor.getString(5)), Long.parseLong(cursor.getString(6))));
            }
        }
        return datalogs;
    }

    public DataLog getDataTotals(String packageName) {
        ArrayList<DataLog> dataLogs = new ArrayList<>();
        Cursor cursor = db.query(SQLiteOpenHelperDataUsage.TABLE_DATA, allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            if (cursor.getString(2).equals(packageName)) {
                dataLogs.add(new DataLog(cursor.getLong(0), cursor.getString(1), cursor.getString(2), Long.parseLong(cursor.getString(3)), Long.parseLong(cursor.getString(4)), Long.parseLong(cursor.getString(5)), Long.parseLong(cursor.getString(6))));
            }
        }
        long downback = 0;
        long downfront = 0;
        long upback = 0;
        long upfront = 0;
        for (DataLog dataLog : dataLogs) {
            downback += dataLog.getAmountDownBackground();
            downfront += dataLog.getAmountDownForeground();
            upback = +dataLog.getAmountUpBackground();
            upfront = +dataLog.getAmountUpForeground();
        }
        System.out.println("DOWNBACK " + downback);
        System.out.println("DOWNBACK " + downfront);
        System.out.println("DOWNBACK " + upback);
        System.out.println("DOWNBACK " + upfront);
        return new DataLog(-1, null, null, downfront, downback, upfront, upback);
    }

    public ArrayList<DataLog> getLastWeek(String packageName) {
        ArrayList<DataLog> dataLogs = new ArrayList<>();
        Calendar aWeekAgo = Calendar.getInstance();
        aWeekAgo.add(Calendar.DAY_OF_MONTH, -7);
        DataLog dayLog = null;

        return dataLogs;
    }

    public ArrayList<DataLog> getLastDay(String packageName) throws ParseException {
        ArrayList<DataLog> hoursDataLog = new ArrayList<>();
        Calendar today = Calendar.getInstance();
        Calendar current = null;

        final SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");

        Cursor cursor = db.query(SQLiteOpenHelperDataUsage.TABLE_DATA, allColumns, null, null, null, null, null);
        cursor.moveToFirst();

        while (cursor.moveToNext()) {
            if (cursor.getString(2).equals(packageName)) {
//                current.setTime(sdf.parse(cursor.getString(1)));
//                if(current.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                    hoursDataLog.add(new DataLog(cursor.getLong(0), cursor.getString(1), cursor.getString(2), Long.parseLong(cursor.getString(3)), Long.parseLong(cursor.getString(4)), Long.parseLong(cursor.getString(5)), Long.parseLong(cursor.getString(6))));
//                }
            }
        }
        Collections.sort(hoursDataLog, new Comparator<DataLog>() {
            public int compare(DataLog o1, DataLog o2) {
                try {
                    return sdf.parse(o1.getTimestamp()).compareTo(sdf.parse(o2.getTimestamp()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return -1;
            }
        });
        return hoursDataLog;
    }

    public void close() {
        dbHelper.close();
    }
}
