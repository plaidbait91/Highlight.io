package com.example.highlightio;


import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Match implements Parcelable {

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

    protected Match(Parcel in) {
        title = in.readString();
        tourney = in.readString();
        URL = in.readString();
        thumbURL = in.readString();
        thumb = in.readParcelable(Bitmap.class.getClassLoader());
        time = in.readString();
    }

    public static final Creator<Match> CREATOR = new Creator<Match>() {
        @Override
        public Match createFromParcel(Parcel in) {
            return new Match(in);
        }

        @Override
        public Match[] newArray(int size) {
            return new Match[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.tourney);
        dest.writeString(this.URL);
        dest.writeString(this.thumbURL);
        dest.writeString(this.time);
        dest.writeParcelable(this.thumb, flags);
    }
}
