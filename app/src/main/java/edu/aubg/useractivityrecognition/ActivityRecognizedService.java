package edu.aubg.useractivityrecognition;

import android.app.IntentService;
import android.content.Intent;

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
    }
}