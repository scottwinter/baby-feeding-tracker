package com.example.babyfeedingtracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.babyfeedingtracker.model.ActivityItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ActivityListAdaptor extends RecyclerView.Adapter<ActivityListAdaptor.ActivityViewHolder> {
    private static final String USERS_COLLECTION = "users";
    private static final String ACTIVITIES_COLLECTION = "activities";

    private ArrayList<ActivityItem> mDataset;
    private OnItemDeleteListener onItemDeleteListener;

    ActivityListAdaptor(ArrayList<ActivityItem> myDataset) {
        mDataset = myDataset;
    }

    public void setOnItemDeleteListener(OnItemDeleteListener listener) {
        this.onItemDeleteListener = listener;
    }

    @Override
    public ActivityListAdaptor.ActivityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ActivityViewHolder(LayoutInflater.from(
                parent.getContext()).inflate(R.layout.activity_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ActivityViewHolder holder, int position) {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd hh:mm a");
        Date date = new Date(mDataset.get(position).getDateTime());
        holder.activityText.setText(mDataset.get(position).getActivityType());
        holder.activitySubType.setText(mDataset.get(position).getActivitySubType());
        holder.activityTime.setText(dateFormat.format(date));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    class ActivityViewHolder extends RecyclerView.ViewHolder {

        TextView activityText;
        TextView activityTime;
        TextView activitySubType;
        ActivityViewHolder(final View itemView) {
            super(itemView);
            activityText = itemView.findViewById(R.id.activity_item_text);
            activitySubType = itemView.findViewById(R.id.activity_subtype_text);
            activityTime = itemView.findViewById(R.id.activity_time);

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
        String itemId = mDataset.get(position).getId();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            db.collection(USERS_COLLECTION).document(user.getUid()).collection(ACTIVITIES_COLLECTION)
                    .document(itemId)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("DELETE", "DocumentSnapshot successfully deleted!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("DELETE", "Error deleting document", e);
                        }
                    });
            mDataset.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mDataset.size());
        }

        if(onItemDeleteListener != null) {
            onItemDeleteListener.onItemDelete();
        }
    }
}
