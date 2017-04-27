package edu.aubg.useractivityrecognition.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class ActivityProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ActivityDbHelper mOpenHelper;

    static final int ACTIVITIES = 100;
    static final int FIRST_TWO_ACTIVITIES = 101;
    static final int TEST_ACTIVITIES = 102;


    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ActivityContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, ActivityContract.PATH_ACTIVITY, ACTIVITIES);
        matcher.addURI(authority, ActivityContract.PATH_ACTIVITY + '/' + ActivityContract.PATH_FIRST_TWO, FIRST_TWO_ACTIVITIES);
        matcher.addURI(authority, ActivityContract.PATH_ACTIVITY_TEST, TEST_ACTIVITIES);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new ActivityDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case ACTIVITIES:
                return ActivityContract.ActivityEntry.CONTENT_TYPE;
            case FIRST_TWO_ACTIVITIES:
                return ActivityContract.ActivityEntry.CONTENT_TYPE;
            case TEST_ACTIVITIES:
                return ActivityContract.ActivityEntryTest.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case ACTIVITIES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ActivityContract.ActivityEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder,
                        null
                );
                break;
            }
            case FIRST_TWO_ACTIVITIES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ActivityContract.ActivityEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder,
                        "2"
                );
                break;
            }
            case TEST_ACTIVITIES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ActivityContract.ActivityEntryTest.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {

            case ACTIVITIES: {
                long _id = db.insert(ActivityContract.ActivityEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = ActivityContract.ActivityEntry.buildActivityUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TEST_ACTIVITIES: {
                long _id = db.insert(ActivityContract.ActivityEntryTest.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = ActivityContract.ActivityEntryTest.buildActivityUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if (null == selection) selection = "1";
        switch (match) {
            case ACTIVITIES:
                rowsDeleted = db.delete(
                        ActivityContract.ActivityEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TEST_ACTIVITIES:
                rowsDeleted = db.delete(
                        ActivityContract.ActivityEntryTest.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }


    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case ACTIVITIES:
                rowsUpdated = db.update(ActivityContract.ActivityEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ACTIVITIES:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ActivityContract.ActivityEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
