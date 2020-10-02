package com.example.babyfeedingtracker;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.babyfeedingtracker.model.ActivityItem;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity
        implements DeleteActivityDialog.DeleteActivityDialogListener, OnItemDeleteListener {

    private static final int RC_SIGN_IN = 123;
    private static final String USERS_COLLECTION = "users";
    private static final String ACTIVITIES_COLLECTION = "activities";
    private static final String NONE = "None";

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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            Log.i("AUTH", "OnCreate Logged in user: " + user.getDisplayName());
        } else {
            Log.i("AUTH", "OnCreate Logged in user: none");
        }

        String eventType = getIntent().getStringExtra("eventType");
        String activitySubType = getIntent().getStringExtra("activitySubType");
        long dateTime = getIntent().getLongExtra("dateTime", 0L);
        if (dateTime > 0 && eventType != null) {
            ActivityItem activityItem = new ActivityItem(eventType);
            activityItem.setActivitySubType(activitySubType);
            activityItem.setDateTime(dateTime);
            if(user != null) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection(USERS_COLLECTION).document(user.getUid()).collection(ACTIVITIES_COLLECTION)
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
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.login:
                startSignInIntent();
                return true;
            case R.id.logout:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void startSignInIntent() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Collections.singletonList(
                new AuthUI.IdpConfig.EmailBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    public void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.i("AUTH", "Logged in user: " + FirebaseAuth.getInstance().getCurrentUser());
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
//            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.i("AUTH", "Logged in user: " + user.getDisplayName());
                Log.i("AUTH", "Logged in user ID: " + user.getUid());

            } else {
                Log.e("AUTH", "Logged FAILED.");
            }
        }
    }

    private void refreshData() {
        ActivityListAdaptor mAdapter = new ActivityListAdaptor(dataList);
        mAdapter.setOnItemDeleteListener(this);
        mAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(mAdapter);
    }

    private ArrayList<ActivityItem> populateData() {
        dataList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            db.collection(USERS_COLLECTION).document(user.getUid()).collection(ACTIVITIES_COLLECTION)
                    .orderBy("dateTime", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    ActivityItem item = new ActivityItem();
                                    item.setId(document.getId());
                                    if (document.getData().get("activityType") != null) {
                                        item.setActivityType(document.getData().get("activityType").toString());
                                    }
                                    if (document.getData().get("activitySubType") != null) {
                                        item.setActivitySubType(document.getData().get("activitySubType").toString());
                                    }
                                    if (document.getData().get("dateTime") != null) {
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
        }

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
        long latestFeeding = 0L;
        long latestDiaper = 0L;

        for (ActivityItem item : dataList) {
            if (item.getActivityType().equalsIgnoreCase("feeding")) {
                if (item.getDateTime() > latestFeeding) {
                    latestFeeding = item.getDateTime();
                }
                if (item.getDateTime() >= todayInMillis) {
                    todaysFeedings.add(item);
                }
            }

            if (item.getActivityType().equalsIgnoreCase("diaper")) {
                if (item.getDateTime() > latestDiaper) {
                    latestDiaper = item.getDateTime();
                }
                if (item.getDateTime() >= todayInMillis) {
                    todaysDiapers.add(item);
                }
            }
        }

        java.text.DateFormat dateFormat = new SimpleDateFormat("MM/dd hh:mm a");
        Date feedingDate = new Date(latestFeeding);

        if(todaysFeedings.size() > 0) {
            latestFeedingTime.setText(dateFormat.format(feedingDate));
        } else {
            latestFeedingTime.setText(NONE);
        }
        Date diaperDate = new Date(latestDiaper);

        if (todaysDiapers.size() > 0) {
            latestDiaperTime.setText(dateFormat.format(diaperDate));
        } else {
            latestDiaperTime.setText(NONE);
        }

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


