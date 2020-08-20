package com.example.highstrangeness.objects;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class NewPost {
    public NewPost(String title, boolean firstHand, Date date, double latitude, double longitude, String description) {
        this.title = title;
        this.firstHand = firstHand;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
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

    private String title;
    private boolean firstHand;
    private Date date;
    private double latitude;
    private double longitude;
    private String description;

}
