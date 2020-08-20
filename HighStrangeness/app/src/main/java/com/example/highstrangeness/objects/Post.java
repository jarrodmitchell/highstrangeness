package com.example.highstrangeness.objects;

import java.util.ArrayList;
import java.util.Date;

public class Post {

    private String id;
    private String userId;
    private String title;
    private boolean firstHand;
    private Date date;
    private double latitude;
    private double longitude;
    private String description;
    private ArrayList<String> tags;

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public boolean isFirstHand() {
        return firstHand;
    }

    public Date getDate() {
        return date;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public Post(String id, String userId, String title, boolean firstHand, Date date, double latitude, double longitude, String description, ArrayList<String> tags) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.firstHand = firstHand;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.tags = tags;
    }
}
