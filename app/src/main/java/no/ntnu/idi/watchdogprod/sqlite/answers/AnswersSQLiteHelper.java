package no.ntnu.idi.watchdogprod.sqlite.answers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by sigurdhf on 16.04.2015.
 */
public class AnswersSQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE_ANSWERS = "answers";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_ANSWER_ID = "answerID";
    public static final String COLUMN_PACKAGE_NAME = "packageName";
    public static final String COLUMN_ANSWER = "answer";

    public static final String DATABASE_NAME = "answers.db";
    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_CREATE = "create table "
            + TABLE_ANSWERS + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_ANSWER_ID + " integer not null, "
            + COLUMN_DATE + " integer not null, "
            + COLUMN_PACKAGE_NAME + " text not null, "
            + COLUMN_ANSWER + " integer not null);";

    public AnswersSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(AnswersSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANSWERS);
        onCreate(db);
    }
}
