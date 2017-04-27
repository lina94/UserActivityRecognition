package edu.aubg.useractivityrecognition;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.aubg.useractivityrecognition.data.ActivityContract;

/**
 * Created by nikola on 27.04.17.
 */

public class ActivityDbTestAdapter extends RecyclerView.Adapter<ActivityDbTestAdapter.ViewHolder> {

    private Cursor dataCursor;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_db_test_row, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(dataCursor.moveToPosition(position)){
            int activityType = dataCursor.getInt(ActivityContract.ActivityEntryTest.INDEX_ACTIVITY_TYPE);
            long activityStartTime = dataCursor.getLong(ActivityContract.ActivityEntryTest.INDEX_ACTIVITY_DATE);
            int activityConfidence = dataCursor.getInt(ActivityContract.ActivityEntryTest.INDEX_ACTIVITY_CONFIDENCE);

            holder.activityType.setText(String.valueOf(activityType));
            holder.activityStartTime.setText(String.valueOf(activityStartTime));
            holder.activityConfidence.setText(String.valueOf(activityConfidence));
        }
    }

    @Override
    public int getItemCount() {
        return dataCursor == null ? 0 : dataCursor.getCount();
    }

    public void setDataCursor(Cursor dataCursor){
        Cursor oldCursor = this.dataCursor;
        this.dataCursor = dataCursor;

        if(oldCursor != null){
            oldCursor.close();
        }

        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView activityType;
        TextView activityStartTime;
        TextView activityConfidence;

        ViewHolder(View itemView) {
            super(itemView);

            activityType = (TextView) itemView.findViewById(R.id.activity_type);
            activityStartTime = (TextView) itemView.findViewById(R.id.activity_starttime);
            activityConfidence = (TextView) itemView.findViewById(R.id.activity_confidence);
        }
    }
}
