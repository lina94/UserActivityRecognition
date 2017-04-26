package edu.aubg.useractivityrecognition;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.Date;
import java.util.List;

import edu.aubg.useractivityrecognition.data.ActivityContract;

/**
 * Created by nikola on 25.04.17.
 */

public class ActivityRecognizedService extends IntentService {

    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
    }

    public ActivityRecognizedService(String name) {
        super(name);

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
            Log.d("Got activity:", "" + activity.getType());
            if (activity.getConfidence() > 5) {

                ContentValues cv = new ContentValues();
                cv.put(ActivityContract.ActivityEntry.COLUMN_ACTIVITY_TYPE, activity.getType());
                cv.put(ActivityContract.ActivityEntry.COLUMN_ACTIVITY_DATE, new Date().getTime());

                getContentResolver().insert(ActivityContract.ActivityEntry.CONTENT_URI, cv);
            }
        }
    }
}