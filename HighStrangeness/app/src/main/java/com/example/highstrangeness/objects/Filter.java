package com.example.highstrangeness.objects;

import java.util.Date;

public class Filter {

    public static Filter filter = null;

    String tag;
    Date startDate;
    Date endDate;
    boolean hasImages;
    boolean hasAudio;
    boolean hasVideo;

    public Filter(String tag, Date startDate, Date endDate, boolean hasImages, boolean hasAudio, boolean hasVideo) {
        this.tag = tag;
        this.startDate = startDate;
        this.endDate = endDate;
        this.hasImages = hasImages;
        this.hasAudio = hasAudio;
        this.hasVideo = hasVideo;
    }

    public String getTag() {
        return tag;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public boolean isHasImages() {
        return hasImages;
    }

    public boolean isHasAudio() {
        return hasAudio;
    }

    public boolean isHasVideo() {
        return hasVideo;
    }
}
