package no.ntnu.idi.watchdogprod.sqlite.answers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

import no.ntnu.idi.watchdogprod.sqlite.applicationupdates.ApplicationUpdatesSQLiteHelper;

/**
 * Created by sigurdhf on 16.04.2015.
 */
public class AnswersDataSource {
    private SQLiteDatabase db;
    private AnswersSQLiteHelper dbHelper;
    private String[] allColumns = {AnswersSQLiteHelper.COLUMN_ID,
            AnswersSQLiteHelper.COLUMN_ANSWER_ID,
            AnswersSQLiteHelper.COLUMN_DATE,
            AnswersSQLiteHelper.COLUMN_PACKAGE_NAME,
            AnswersSQLiteHelper.COLUMN_ANSWER};

    public AnswersDataSource(Context context) {
        dbHelper = new AnswersSQLiteHelper(context);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Answer insertAnswer(int answerID, long date, String packageName, int answer) {
        ContentValues cv = new ContentValues();
        cv.put(AnswersSQLiteHelper.COLUMN_ANSWER_ID, answerID);
        cv.put(AnswersSQLiteHelper.COLUMN_DATE, date);
        cv.put(AnswersSQLiteHelper.COLUMN_PACKAGE_NAME, packageName);
        cv.put(AnswersSQLiteHelper.COLUMN_ANSWER, answer);

        long insertId = db.insert(AnswersSQLiteHelper.TABLE_ANSWERS, null, cv);
        Cursor cursor = db.query(AnswersSQLiteHelper.TABLE_ANSWERS,
                allColumns, AnswersSQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Answer newAnswer = cursorToAnswer(cursor);
        return newAnswer;
    }

    private Answer cursorToAnswer(Cursor cursor) {
        return new Answer(cursor.getLong(0), cursor.getInt(1), cursor.getLong(2), cursor.getString(3), cursor.getInt(4));
    }

}
