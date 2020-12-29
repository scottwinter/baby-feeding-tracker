package com.example.babyfeedingtracker;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

import androidx.appcompat.app.AppCompatActivity;

public class EditEventActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner activitySpinner;
    Spinner activitySubTypeSpinner;
    Long selectedTime;
    String[] diaperSubTypes;
    ArrayAdapter feedingArrayAdapter;
    String[] feedingSubTypes;
    int subtypeIndexSelection;
    String itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_item);

        Resources res = getResources();
        String[] eventTypes = res.getStringArray(R.array.activities);
        feedingSubTypes = res.getStringArray(R.array.feedingSubTypes);
        diaperSubTypes = res.getStringArray(R.array.diaperSubTypes);
        //Getting the instance of Spinner and applying OnItemSelectedListener on it
        activitySpinner = findViewById(R.id.activitySpinner);
        activitySubTypeSpinner = findViewById(R.id.activitySubTypeSpinner);

        ArrayAdapter aa = new ArrayAdapter(this,R.layout.spinner_list_item, eventTypes);
        aa.setDropDownViewResource(R.layout.spinner_list_item);
        activitySpinner.setAdapter(aa);
        activitySpinner.setOnItemSelectedListener(this);

        feedingArrayAdapter = new ArrayAdapter(this,R.layout.spinner_list_item, feedingSubTypes);
        feedingArrayAdapter.setDropDownViewResource(R.layout.spinner_list_item);
        activitySubTypeSpinner.setAdapter(feedingArrayAdapter);

        final DatePicker datePicker = (DatePicker) findViewById(R.id.date_picker);
        final TimePicker timePicker = (TimePicker) findViewById(R.id.time_picker);

        Button setValues = findViewById(R.id.date_time_set);
        setValues.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                       datePicker.getMonth(),
                       datePicker.getDayOfMonth(),
                       timePicker.getHour(),
                       timePicker.getMinute());

               selectedTime = calendar.getTimeInMillis();
               setNewEvent();
           }});

        // Set values based on the item selected to edit
        String eventType = getIntent().getStringExtra("eventType");
        String activitySubType = getIntent().getStringExtra("activitySubType");
        long intentDateTime = getIntent().getLongExtra("itemDateTime", -1L);
        itemId = getIntent().getStringExtra("id");

        int index = 0;
        if(eventType.equalsIgnoreCase("diaper")){
            index = 1;
        }
        activitySpinner.setSelection(index);
        subtypeIndexSelection = 0;
        if(index == 0) {
            if(activitySubType.equalsIgnoreCase("bottle")){
                subtypeIndexSelection = 1;
            }
        } else {
            if(activitySubType.equalsIgnoreCase("wet")){
                subtypeIndexSelection = 1;
            }
        }

        Calendar itemDateTime = Calendar.getInstance();
        itemDateTime.setTimeInMillis(intentDateTime);
        datePicker.updateDate(itemDateTime.get(Calendar.YEAR),
                itemDateTime.get(Calendar.MONTH), itemDateTime.get(Calendar.DAY_OF_MONTH));

        timePicker.setHour(itemDateTime.get(Calendar.HOUR_OF_DAY));
        timePicker.setMinute(itemDateTime.get(Calendar.MINUTE));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        Log.d("SpinnerSet", "Inside onItemSelected with position: " + pos);
        if(pos == 1) {
            feedingArrayAdapter = new ArrayAdapter(this,R.layout.spinner_list_item, diaperSubTypes);
            feedingArrayAdapter.setDropDownViewResource(R.layout.spinner_list_item);
            activitySubTypeSpinner.setAdapter(feedingArrayAdapter);
            feedingArrayAdapter.notifyDataSetChanged();
        } else if(pos == 0) {
            feedingArrayAdapter = new ArrayAdapter(this,R.layout.spinner_list_item, feedingSubTypes);
            feedingArrayAdapter.setDropDownViewResource(R.layout.spinner_list_item);
            activitySubTypeSpinner.setAdapter(feedingArrayAdapter);
            activitySubTypeSpinner.setSelection(subtypeIndexSelection);
            feedingArrayAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void setNewEvent() {
        String event = activitySpinner.getSelectedItem().toString();
        String activitySubType = activitySubTypeSpinner.getSelectedItem().toString();
        Intent intent = new Intent(EditEventActivity.this, MainActivity.class);
        intent.putExtra("eventType", event);
        intent.putExtra("activitySubType", activitySubType);
        intent.putExtra("dateTime", selectedTime);
        intent.putExtra("id", itemId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }



//    public void showTimePickerDialog() {
//        DialogFragment newFragment = new NewEventActivity.TimePickerFragment();
//        newFragment.show(getSupportFragmentManager(), "timePicker");
//    }
//
//    public class TimePickerFragment extends DialogFragment
//            implements TimePickerDialog.OnTimeSetListener {
//
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            // Use the current time as the default values for the picker
//            final Calendar c = Calendar.getInstance();
//            int hour = c.get(Calendar.HOUR_OF_DAY);
//            int minute = c.get(Calendar.MINUTE);
//
//            // Create a new instance of TimePickerDialog and return it
//            return new TimePickerDialog(getActivity(), this, hour, minute,
//                    DateFormat.is24HourFormat(getActivity()));
//        }
//
//        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//            final Calendar calendar = Calendar.getInstance();
//            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
//            calendar.set(Calendar.MINUTE, minute);
//            calendar.set(Calendar.SECOND, 0);
//            calendar.set(Calendar.MILLISECOND, 0);
//
//            long millis = calendar.getTimeInMillis();
//            selectedTime = calendar;
//        }
//    }
}