package com.example.highstrangeness.objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;

public class Post implements Parcelable {

    protected Post(Parcel in) {
        id = in.readString();
        userId = in.readString();
        username = in.readString();
        title = in.readString();
        firstHand = in.readByte() != 0;
        date = new Date(in.readLong());
        latitude = in.readDouble();
        longitude = in.readDouble();
        description = in.readString();
        tags = in.createStringArrayList();
        contentTypes = in.createStringArrayList();
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(userId);
        parcel.writeString(username);
        parcel.writeString(title);
        parcel.writeByte((byte) (firstHand ? 1 : 0));
        parcel.writeLong(date.getTime());
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeString(description);
        parcel.writeStringList(tags);
        parcel.writeStringList(contentTypes);
    }

    private String id;
    private String userId;
    private String username;
    private String title;
    private boolean firstHand;
    private Date date;
    private double latitude;
    private double longitude;
    private String description;
    private ArrayList<String> tags;
    private ArrayList<String> contentTypes;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ArrayList<String> getContentTypes() {
        return contentTypes;
    }

    public void setContentTypes(ArrayList<String> contentTypes) {
        this.contentTypes = contentTypes;
    }

    public Post(String id, String userId, String title, boolean firstHand, Date date, double latitude, double longitude, String description, ArrayList<String> tags, ArrayList<String> contentTypes) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.firstHand = firstHand;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.tags = tags;
        this.contentTypes = contentTypes;
    }
}
