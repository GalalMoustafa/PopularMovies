package com.example.popularmovies.Models;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;


@Entity(tableName = "Movie")
public class Movie implements Parcelable {

    @NonNull
    @PrimaryKey
    private String _id;
    private String _title;
    private String _poster;
    private String _overview;
    private String _release_date;
    private String _rate;


    public Movie(String id, String title, String poster, String overview, String release_date, String rate) {
        this._id = id;
        this._title = title;
        this._poster = poster;
        this._overview = overview;
        this._release_date = release_date;
        this._rate = rate;
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {

            return new Movie[size];
        }

    };

    public Movie(Parcel in) {
        _id = in.readString();
        _title = in.readString();
        _poster = in.readString();
        _overview = in.readString();
        _release_date = in.readString();
        _rate = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(_title);
        dest.writeString(_poster);
        dest.writeString(_overview);
        dest.writeString(_release_date);
        dest.writeString(_rate);
    }


    public String get_id() {
        return _id;
    }

    public String get_title() {
        return _title;
    }

    public String get_poster() {
        return _poster;
    }

    public String get_overview() {
        return _overview;
    }

    public String get_release_date() {
        return _release_date;
    }

    public String get_rate() {
        return _rate;
    }

}
