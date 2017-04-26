package edu.aubg.useractivityrecognition;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.Date;
import java.util.List;

import edu.aubg.useractivityrecognition.data.ActivityContract;

public class ActivityRecognizedService extends IntentService {

    private Integer currentActivityType = null;

    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
    }

    public ActivityRecognizedService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        currentActivityType = getLastActivityType();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities(result.getProbableActivities());
        }
    }

    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {
        for (DetectedActivity activity : probableActivities) {
            Log.d("Got activity:", "" + activity.getType() + " with confidence: " + activity.getConfidence());
            if (relevantActivity(activity) && activity.getConfidence() > 66) {
                if (currentActivityType == null || activity.getType() != currentActivityType) {
                    currentActivityType = activity.getType();
                    saveActivity(activity);
                }
            }
        }
    }

    private void saveActivity(DetectedActivity activity) {
        ContentValues cv = new ContentValues();
        cv.put(ActivityContract.ActivityEntry.COLUMN_ACTIVITY_TYPE, activity.getType());
        cv.put(ActivityContract.ActivityEntry.COLUMN_ACTIVITY_DATE, new Date().getTime());

        getContentResolver().insert(ActivityContract.ActivityEntry.CONTENT_URI, cv);
    }

    private int getLastActivityType() {
        Uri mUri = ActivityContract.ActivityEntry.CONTENT_URI_FIRST_TWO;

        Cursor c = getContentResolver().query(
                mUri,
                ActivityContract.ActivityEntry.ACTIVITY_COLUMNS,
                null,
                null,
                ActivityContract.ActivityEntry.COLUMN_ACTIVITY_DATE + " DESC"
        );

        if (c.moveToFirst()) {
            return c.getInt(ActivityContract.ActivityEntry.INDEX_ACTIVITY_TYPE);
        } else return -1;
    }


    private boolean relevantActivity(DetectedActivity activity) {
        switch (activity.getType()) {
            case DetectedActivity.IN_VEHICLE:
                return true;
            case DetectedActivity.WALKING:
                return true;
            case DetectedActivity.RUNNING:
                return true;
            case DetectedActivity.STILL:
                return true;
            default:
                return false;
        }
    }
}