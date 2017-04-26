package edu.aubg.useractivityrecognition.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by nikola on 25.04.17.
 */

public class ActivityDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 7;
    public static final String DATABASE_NAME = "activities.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ActivityContract.ActivityEntry.TABLE_NAME + " (" +
                    ActivityContract.ActivityEntry._ID + " INTEGER PRIMARY KEY," +
                    ActivityContract.ActivityEntry.COLUMN_ACTIVITY_TYPE + " INTEGER NOT NULL," +
                    ActivityContract.ActivityEntry.COLUMN_ACTIVITY_DATE + " INTEGER NOT NULL)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ActivityContract.ActivityEntry.TABLE_NAME;


    public ActivityDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
