package com.example.highstrangeness.objects;

import android.net.Uri;

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

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public ArrayList<Uri> getImageUris() {
        return imageUris;
    }

    public void setImageUris(ArrayList<Uri> imageUris) {
        this.imageUris = imageUris;
    }

    public ArrayList<Uri> getAudioUris() {
        return audioUris;
    }

    public void setAudioUris(ArrayList<Uri> audioUris) {
        this.audioUris = audioUris;
    }

    public ArrayList<Uri> getVideoUris() {
        return videoUris;
    }

    public void setVideoUris(ArrayList<Uri> videoUris) {
        this.videoUris = videoUris;
    }

    private String title;
    private boolean firstHand;
    private Date date;
    private double latitude;
    private double longitude;
    private String description;
    String tags;
    ArrayList<Uri> imageUris;
    ArrayList<Uri> audioUris;
    ArrayList<Uri> videoUris;

}
