package com.example.babyfeedingtracker;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

import com.example.babyfeedingtracker.model.ActivityItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity
        implements DeleteActivityDialog.DeleteActivityDialogListener {

    ArrayList<ActivityItem> dataList;
    ArrayList<ActivityItem> todaysFeedings;
    ArrayList<ActivityItem> todaysDiapers;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(dataList == null) {
            dataList = new ArrayList<>();
        }

        if(dataList == null) {
            todaysFeedings = new ArrayList<>();
        }

        if(dataList == null) {
            todaysDiapers = new ArrayList<>();
        }

        recyclerView = findViewById(R.id.recentActivityList);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(new ActivityListAdaptor(dataList));

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

    @Override
    protected void onResume() {
        super.onResume();
        String eventType = getIntent().getStringExtra("eventType");
        Long dateTime = getIntent().getLongExtra("dateTime", 0L);
        if(dateTime > 0 && eventType != null) {
            ActivityItem activityItem = new ActivityItem(eventType);
            activityItem.setDateTime(dateTime);
            addItemToTodaysLists(activityItem);
            dataList.add(activityItem);
            refreshData();

        }
    }

    private void refreshData() {
        ActivityListAdaptor mAdapter = new ActivityListAdaptor(dataList);
        mAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(mAdapter);
    }

    private ArrayList<ActivityItem> populateData() {
        dataList = new ArrayList<>();
        for(int i = 0; i <= 5; i++) {
            ActivityItem item = new ActivityItem("Feeding");
            Calendar calendar = Calendar.getInstance();
            item.setDateTime(calendar.getTimeInMillis());
            dataList.add(item);
        }

        return dataList;
    }

    private void addItemToTodaysLists(ActivityItem item) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        long todayInMillis = c.getTimeInMillis();

        if(item.getActivityType().equalsIgnoreCase("feeding")){
            if(item.getDateTime() >= todayInMillis) {
                todaysFeedings.add(item);
            }
        }

        if(item.getActivityType().equalsIgnoreCase("diaper")) {
            if(item.getDateTime() >= todayInMillis) {
                todaysDiapers.add(item);
            }
        }

        Log.i("COUNTS", "Todays feedins: " + todaysFeedings.size());
        Log.i("COUNTS", "Todays diapers: " + todaysDiapers.size());

    }

//    @Override
//    public void addNewActivity(String todoItem) {
//        dataList.add(new ActivityItem(todoItem));
//    }

    @Override
    public void deleteActivity(ActivityItem activityItem) {
        dataList.remove(activityItem);
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
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


