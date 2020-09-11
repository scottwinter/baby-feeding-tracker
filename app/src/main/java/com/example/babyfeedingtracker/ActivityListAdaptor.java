package com.example.babyfeedingtracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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

    class ActivityViewHolder extends RecyclerView.ViewHolder {

        TextView activityText;
        ActivityViewHolder(final View itemView) {
            super(itemView);
            activityText = itemView.findViewById(R.id.activity_item_text);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Delete Activity")
                            .setMessage("Are you sure you want to delete this Activity?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    removeAt(getAdapterPosition());
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return true;
                }
            });
        }
    }

    public void removeAt(int position) {
        mDataset.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mDataset.size());
    }
}
