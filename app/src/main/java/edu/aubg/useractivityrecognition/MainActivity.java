package edu.aubg.useractivityrecognition;

import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;

import java.util.concurrent.TimeUnit;

import edu.aubg.useractivityrecognition.data.ActivityContract;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, android.app.LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ACTIVITY_LOADER = 100;
    private GoogleApiClient mApiClient;

    private ImageView actionImage;
    private TextView actionDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mApiClient.connect();

        actionImage = (ImageView) findViewById(R.id.actionImage);
        actionDescription = (TextView) findViewById(R.id.actionDescription);

        int lastActivityType = getLastActivityType();
        setImageAndDescription(lastActivityType);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mApiClient.isConnecting() && !mApiClient.isConnected()) {
            mApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Intent intent = new Intent(this, ActivityRecognizedService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient, 3000, pendingIntent);

        Log.d("Connected:", "yes");

        Intent intentDb = new Intent(this, DbTestActivity.class);
        startActivity(intentDb);

        getLoaderManager().initLoader(ACTIVITY_LOADER, null, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void setImageAndDescription(int activityType) {
        switch (activityType) {
            case DetectedActivity.IN_VEHICLE:
                actionImage.setImageResource(R.drawable.vehicle);
                actionDescription.setText("In a vehicle");
                break;
            case DetectedActivity.WALKING:
                actionImage.setImageResource(R.drawable.walking);
                actionDescription.setText("Walking");
                break;
            case DetectedActivity.RUNNING:
                actionImage.setImageResource(R.drawable.running);
                actionDescription.setText("Running");
                break;
            case DetectedActivity.STILL:
                actionImage.setImageResource(R.drawable.still);
                actionDescription.setText("Standing still");
                break;
        }
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

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri mUri = ActivityContract.ActivityEntry.CONTENT_URI_FIRST_TWO;
        if (null != mUri) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new android.content.CursorLoader(
                    this,
                    mUri,
                    ActivityContract.ActivityEntry.ACTIVITY_COLUMNS,
                    null,
                    null,
                    ActivityContract.ActivityEntry.COLUMN_ACTIVITY_DATE + " DESC"
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        if (data.getCount() > 1) {
            data.moveToPosition(0);
            long currentActivityStartTime = data.getLong(ActivityContract.ActivityEntry.INDEX_ACTIVITY_DATE);
            int currentActivityType = data.getInt(ActivityContract.ActivityEntry.INDEX_ACTIVITY_TYPE);
            setImageAndDescription(currentActivityType);

            data.moveToPosition(1);
            long lastActivityStartTime = data.getLong(ActivityContract.ActivityEntry.INDEX_ACTIVITY_DATE);
            int lastActivityType = data.getInt(ActivityContract.ActivityEntry.INDEX_ACTIVITY_TYPE);

            long deltaTime = currentActivityStartTime - lastActivityStartTime;

            long minutes = TimeUnit.MILLISECONDS.toMinutes(deltaTime);
            long seconds = TimeUnit.MILLISECONDS.toSeconds(deltaTime) - TimeUnit.MINUTES.toSeconds(minutes);

            String activityMessage = "You ";

            switch (lastActivityType) {
                case DetectedActivity.IN_VEHICLE:
                    activityMessage += "were in a vehicle for ";
                    break;
                case DetectedActivity.WALKING:
                    activityMessage += "were walking for ";
                    break;
                case DetectedActivity.RUNNING:
                    activityMessage += "were running for ";
                    break;
                case DetectedActivity.STILL:
                    activityMessage += "were standing still for ";
                    break;
            }

            Snackbar.make(findViewById(R.id.root_coordinatorlayout),
                    String.format(activityMessage + "%d minutes and %d seconds", minutes, seconds), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {

    }
}
