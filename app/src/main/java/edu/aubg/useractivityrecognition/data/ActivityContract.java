package edu.aubg.useractivityrecognition.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by nikola on 25.04.17.
 */

public class ActivityContract {

    public static final String CONTENT_AUTHORITY = "edu.aubg.useractivityrecognition.activityprovider";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_ACTIVITY = "activity";
    public static final String PATH_FIRST_TWO = "first_two";

    public static final String PATH_ACTIVITY_TEST = "activitytest";

    private ActivityContract() {}

    public static class ActivityEntry implements BaseColumns {
        public static final String TABLE_NAME = "activity";

        public static final String COLUMN_ACTIVITY_TYPE = "activity_type";
        public static final String COLUMN_ACTIVITY_DATE = "activity_date";

        public static final int INDEX_ACTIVITY_ID = 0;
        public static final int INDEX_ACTIVITY_TYPE = 1;
        public static final int INDEX_ACTIVITY_DATE = 2;

        public static final String[] ACTIVITY_COLUMNS = {
                ActivityContract.ActivityEntry._ID,
                ActivityContract.ActivityEntry.COLUMN_ACTIVITY_TYPE,
                ActivityContract.ActivityEntry.COLUMN_ACTIVITY_DATE
        };

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ACTIVITY).build();

        public static final Uri CONTENT_URI_FIRST_TWO =
                CONTENT_URI.buildUpon().appendPath(PATH_FIRST_TWO).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ACTIVITY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ACTIVITY;

        public static Uri buildActivityUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static class ActivityEntryTest implements BaseColumns {
        public static final String TABLE_NAME = "activitytest";

        public static final String COLUMN_ACTIVITY_TYPE = "activity_type";
        public static final String COLUMN_ACTIVITY_DATE = "activity_date";
        public static final String COLUMN_ACTIVITY_CONFIDENCE = "activity_confidence";


        public static final int INDEX_ACTIVITY_ID = 0;
        public static final int INDEX_ACTIVITY_TYPE = 1;
        public static final int INDEX_ACTIVITY_DATE = 2;
        public static final int INDEX_ACTIVITY_CONFIDENCE = 3;


        public static final String[] ACTIVITY_COLUMNS = {
                ActivityContract.ActivityEntryTest._ID,
                ActivityContract.ActivityEntryTest.COLUMN_ACTIVITY_TYPE,
                ActivityContract.ActivityEntryTest.COLUMN_ACTIVITY_DATE,
                ActivityContract.ActivityEntryTest.COLUMN_ACTIVITY_CONFIDENCE,

        };

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ACTIVITY_TEST).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ACTIVITY_TEST;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ACTIVITY_TEST;

        public static Uri buildActivityUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}
