package com.example.myproject;


import android.graphics.Bitmap;

public class Match {

    private final String title;
    private final String tourney;
    private final String URL;
    private final String thumbURL;
    private Bitmap thumb;
    private final String time;

    public Match(String t, String tou, String u, Bitmap tu, String tym, String thumbu) {
        title = t;
        tourney = tou;
        URL = u;
        thumb = tu;
        time = tym;
        thumbURL = thumbu;
    }

    public Bitmap getThumb() {
        return thumb;
    }

    public String getTitle() {
        return title;
    }

    public String getTourney() {
        return tourney;
    }

    public String getURL() {
        return URL;
    }

    public String getTime() {
        return time;
    }

    public String getThumbURL() {
        return thumbURL;
    }

    public void setThumb(Bitmap b) {
        thumb = b;
    }
}
