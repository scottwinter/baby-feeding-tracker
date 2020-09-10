package com.example.babyfeedingtracker;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.babyfeedingtracker.model.ActivityItem;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class ActivityListAdaptor extends RecyclerView.Adapter<ActivityListAdaptor.ActivityViewHolder> {
    private ArrayList<ActivityItem> mDataset;

    ActivityListAdaptor(ArrayList<ActivityItem> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public ActivityListAdaptor.ActivityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ActivityViewHolder(LayoutInflater.from(
                parent.getContext()).inflate(R.layout.activity_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ActivityViewHolder holder, int position) {
        holder.activityText.setText(mDataset.get(position).getActivityType());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    static class ActivityViewHolder extends RecyclerView.ViewHolder {

        TextView activityText;
        ActivityViewHolder(final View itemView) {
            super(itemView);
            activityText = itemView.findViewById(R.id.activity_item_text);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final Dialog dialog = new Dialog(itemView.getContext());
//                    dialog.setContentView(R.layout.item);
                    dialog.setTitle("Do you really want to delete this todo?" );
                    dialog.setCancelable(true);

                    dialog.show();
                    return false;
                }
            });
        }
    }
}
