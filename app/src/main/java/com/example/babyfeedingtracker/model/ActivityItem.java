package com.example.babyfeedingtracker.model;

import com.example.babyfeedingtracker.ActivityType;

public class ActivityItem {
    private int id;
    private ActivityType activityType;
    private Long dateTime;

    public ActivityItem(ActivityType activityType) {
        this.activityType = activityType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }
}
