package com.example.babyfeedingtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.babyfeedingtracker.model.ActivityItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements DeleteActivityDialog.DeleteActivityDialogListener {

    ArrayList<ActivityItem> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        RecyclerView recyclerView = findViewById(R.id.recentActivityList);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(new ActivityListAdaptor(populateData()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
    }


    private ArrayList<ActivityItem> populateData() {
        dataList = new ArrayList<>();
        for(int i = 0; i <= 20; i++) {
            ActivityItem item = new ActivityItem("Feeding");
            dataList.add(item);
        }

        return dataList;
    }

//    @Override
//    public void addNewActivity(String todoItem) {
//        dataList.add(new ActivityItem(todoItem));
//    }

    @Override
    public void deleteActivity(ActivityItem activityItem) {
        dataList.remove(activityItem);
    }
}
