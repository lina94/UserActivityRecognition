package edu.aubg.useractivityrecognition;

import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

import edu.aubg.useractivityrecognition.data.ActivityContract;

public class DbTestActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int ACTIVITY_TEST_LOADER = 200;
    ActivityDbTestAdapter activityDbTestAdapter;
    private GoogleApiClient mApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db_test);

        RecyclerView activityRecyclerView = (RecyclerView) findViewById(R.id.activity_recyclerview);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        activityRecyclerView.setLayoutManager(mLayoutManager);


        activityDbTestAdapter = new ActivityDbTestAdapter();
        activityRecyclerView.setAdapter(activityDbTestAdapter);

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mApiClient.connect();
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
        getContentResolver().delete(ActivityContract.ActivityEntryTest.CONTENT_URI, null, null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri mUri = ActivityContract.ActivityEntryTest.CONTENT_URI;
        if (null != mUri) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new android.content.CursorLoader(
                    this,
                    mUri,
                    ActivityContract.ActivityEntryTest.ACTIVITY_COLUMNS,
                    null,
                    null,
                    ActivityContract.ActivityEntryTest.COLUMN_ACTIVITY_DATE + " DESC"
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        activityDbTestAdapter.setDataCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        activityDbTestAdapter.setDataCursor(null);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Intent intent = new Intent(this, ActivityRecognizedService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient, 3000, pendingIntent);

        Log.d("Connected:", "yes");

        getLoaderManager().initLoader(ACTIVITY_TEST_LOADER, null, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
