package com.example.babyfeedingtracker;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.babyfeedingtracker.model.ActivityItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity
        implements DeleteActivityDialog.DeleteActivityDialogListener, OnItemDeleteListener {

    TextView feedingNumber;
    TextView diaperNumber;
    TextView latestFeedingTime;
    TextView latestDiaperTime;
    ArrayList<ActivityItem> dataList;
    ArrayList<ActivityItem> todaysFeedings;
    ArrayList<ActivityItem> todaysDiapers;
    RecyclerView recyclerView;
    ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        feedingNumber = findViewById(R.id.feedingCount);
        diaperNumber = findViewById(R.id.diaperCount);
        latestFeedingTime = findViewById(R.id.feedingDateTime);
        latestDiaperTime = findViewById(R.id.diaperDateTime);
        spinner = findViewById(R.id.progressSpinner);
        spinner.setVisibility(View.VISIBLE);



        String eventType = getIntent().getStringExtra("eventType");
        long dateTime = getIntent().getLongExtra("dateTime", 0L);
        if(dateTime > 0 && eventType != null) {
            ActivityItem activityItem = new ActivityItem(eventType);
            activityItem.setDateTime(dateTime);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("activities")
                    .add(activityItem)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
//                            Log.d("DATA", "DocumentSnapshot added with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("DATA", "Error adding document", e);
                        }
                    });
        }

        recyclerView = findViewById(R.id.recentActivityList);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        ActivityListAdaptor adaptor = new ActivityListAdaptor(populateData());
        adaptor.setOnItemDeleteListener(this);
        recyclerView.setAdapter(adaptor);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        FloatingActionButton fab;
        fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewEventActivity.class);
//                intent.putExtra("eventId", -1);
                startActivityForResult(intent, 1);
            }
        });



    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        String eventType = getIntent().getStringExtra("eventType");
//        long dateTime = getIntent().getLongExtra("dateTime", 0L);
//        if(dateTime > 0 && eventType != null) {
//            ActivityItem activityItem = new ActivityItem(eventType);
//            activityItem.setDateTime(dateTime);
//            addItemToTodaysLists(activityItem);
//
//            FirebaseFirestore db = FirebaseFirestore.getInstance();
//            db.collection("activities")
//                    .add(activityItem)
//                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                        @Override
//                        public void onSuccess(DocumentReference documentReference) {
//                            Log.d("DATA", "DocumentSnapshot added with ID: " + documentReference.getId());
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.w("DATA", "Error adding document", e);
//                        }
//                    });
//
//
//            dataList.add(activityItem);
//            refreshData();
//
//        }
//    }

    private void refreshData() {
        ActivityListAdaptor mAdapter = new ActivityListAdaptor(dataList);
        mAdapter.setOnItemDeleteListener(this);
        mAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(mAdapter);
    }

    private ArrayList<ActivityItem> populateData() {
        dataList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("activities")
                .orderBy("dateTime", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ActivityItem item = new ActivityItem();
                                item.setId(document.getId());
                                if(document.getData().get("activityType") != null) {
                                    item.setActivityType(document.getData().get("activityType").toString());
                                }
                                if(document.getData().get("dateTime") != null) {
                                    item.setDateTime(Long.valueOf(document.getData().get("dateTime").toString()));
                                }
                                dataList.add(item);
                            }
                        } else {
                            Log.d("ERROR", "Error getting documents: ", task.getException());
                        }
                        addItemToTodaysLists();
                        refreshData();
                        spinner.setVisibility(View.GONE);
                    }
                });

        return dataList;
    }

    private void addItemToTodaysLists() {
        todaysFeedings = new ArrayList<>();
        todaysDiapers = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        long todayInMillis = c.getTimeInMillis();
        long latestFeeding = todayInMillis;
        long latestDiaper = todayInMillis;

        for(ActivityItem item : dataList){
            if(item.getActivityType().equalsIgnoreCase("feeding")){
                if(item.getDateTime() >= todayInMillis) {
                    if(item.getDateTime() > latestFeeding) {
                        latestFeeding = item.getDateTime();
                    }
                    todaysFeedings.add(item);
                }
            }

            if(item.getActivityType().equalsIgnoreCase("diaper")) {
                if(item.getDateTime() >= todayInMillis) {
                    if(item.getDateTime() > latestDiaper) {
                        latestDiaper = item.getDateTime();
                    }
                    todaysDiapers.add(item);
                }
            }
        }

        java.text.DateFormat dateFormat = new SimpleDateFormat("MM/dd hh:mm a");
        Date feedingDate = new Date(latestFeeding);
        latestFeedingTime.setText(dateFormat.format(feedingDate));
        Date diaperDate = new Date(latestDiaper);
        latestDiaperTime.setText(dateFormat.format(diaperDate));
        feedingNumber.setText(String.valueOf(todaysFeedings.size()));
        diaperNumber.setText(String.valueOf(todaysDiapers.size()));
    }

    @Override
    public void deleteActivity(ActivityItem activityItem) {
        dataList.remove(activityItem);
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onItemDelete() {
        populateData();
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            final Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            long millis = calendar.getTimeInMillis();

        }
    }
}


