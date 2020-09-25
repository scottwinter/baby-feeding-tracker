package com.example.babyfeedingtracker.model;

public class ActivityItem {
    private String id;
    private String activityType;
    private Long dateTime;

    public ActivityItem(){};
    public ActivityItem(String activityType) {
        this.activityType = activityType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }
}
