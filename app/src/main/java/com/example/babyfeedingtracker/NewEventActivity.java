package com.example.babyfeedingtracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

public class NewEventActivity extends AppCompatActivity {

    Spinner spin;
    Long selectedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_item);

        Resources res = getResources();
        String[] eventTypes = res.getStringArray(R.array.activities);
        //Getting the instance of Spinner and applying OnItemSelectedListener on it
        spin = (Spinner) findViewById(R.id.activitySpinner);

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,R.layout.spinner_list_item, eventTypes);
        aa.setDropDownViewResource(R.layout.spinner_list_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);
        Button setValues = findViewById(R.id.date_time_set);
        setValues.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               DatePicker datePicker = (DatePicker) findViewById(R.id.date_picker);
               TimePicker timePicker = (TimePicker) findViewById(R.id.time_picker);

               Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                       datePicker.getMonth(),
                       datePicker.getDayOfMonth(),
                       timePicker.getCurrentHour(),
                       timePicker.getCurrentMinute());

               selectedTime = calendar.getTimeInMillis();
               setNewEvent();
           }});

    }

    public void setNewEvent() {
        String event = spin.getSelectedItem().toString();
        Intent intent = new Intent(NewEventActivity.this, MainActivity.class);
        intent.putExtra("eventType", event);
        intent.putExtra("dateTime", selectedTime);
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
