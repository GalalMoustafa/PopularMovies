package com.example.popularmovies.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable {


    private String autor;
    private String content;
    private String url;
    private String id;

    public Review(String autor, String content, String url, String id) {
        this.autor = autor;
        this.content = content;
        this.url = url;
        this.id = id;
    }

    protected Review(Parcel in) {
        autor = in.readString();
        content = in.readString();
        url = in.readString();
        id = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(autor);
        dest.writeString(content);
        dest.writeString(url);
        dest.writeString(id);
    }

    public String getAutor() {
        return autor;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }

    public String getId() {
        return id;
    }
}
