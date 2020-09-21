package com.example.babyfeedingtracker;

import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class NewEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_item);

        Resources res = getResources();
        String[] myBooks = res.getStringArray(R.array.activities);
        //Getting the instance of Spinner and applying OnItemSelectedListener on it
        Spinner spin = (Spinner) findViewById(R.id.activitySpinner);

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,R.layout.spinner_list_item, myBooks);
        aa.setDropDownViewResource(R.layout.spinner_list_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);

    }
}
