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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

import edu.aubg.useractivityrecognition.data.ActivityContract;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, android.app.LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ACTIVITY_LOADER = 100;
    private GoogleApiClient mApiClient;

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

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Intent intent = new Intent(this, ActivityRecognizedService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient, 3000, pendingIntent);

        Log.d("Connected:", "yes");

        getLoaderManager().initLoader(ACTIVITY_LOADER, null, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri mUri = ActivityContract.ActivityEntry.CONTENT_URI;
        if (null != mUri) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new android.content.CursorLoader(
                    this,
                    mUri,
                    ActivityContract.ActivityEntry.ACTIVITY_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
//        if(data.getCount() > 1){
//            data.moveToPosition(0);
//            long currentActivityStartTime = data.getLong(ActivityContract.ActivityEntry.INDEX_ACTIVITY_DATE);
//            data.moveToPosition(1);
//            long lastActivityStartTime = data.getLong(ActivityContract.ActivityEntry.INDEX_ACTIVITY_DATE);
//
//            long deltaTime = currentActivityStartTime - lastActivityStartTime;
//
//            long minutes = TimeUnit.MILLISECONDS.toMinutes(deltaTime);
//            long seconds = TimeUnit.MILLISECONDS.toSeconds(deltaTime) - TimeUnit.MINUTES.toSeconds(minutes);
//
//            Snackbar.make(findViewById(R.id.root_coordinatorlayout),
//                    String.format("You have ran for %d minutes and %d seconds", minutes, seconds), 5);
//        }
        Log.d("Loader finished:", "yes");

        if (null != data && data.moveToNext()) {
            Snackbar.make(findViewById(R.id.root_coordinatorlayout),
                    String.format("Added" + data.getLong(ActivityContract.ActivityEntry.INDEX_ACTIVITY_TYPE)), 5);
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {

    }
}
