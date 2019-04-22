package com.example.popularmovies.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Trailer implements Parcelable {

    private String key;
    private String id;
    private String size;
    private String type;

    public Trailer(String key, String id, String size, String type) {
        this.key = key;
        this.id = id;
        this.size = size;
        this.type = type;
    }

    public static final Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>() {
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        public Trailer[] newArray(int size) {

            return new Trailer[size];
        }

    };

    public Trailer(Parcel in){
        key = in.readString();
        id = in.readString();
        size = in.readString();
        type = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(id);
        dest.writeString(type);
        dest.writeString(size);
    }

    public String getKey() {
        return key;
    }

    public String getId() {
        return id;
    }

    public String getSize() {
        return size;
    }

    public String getType() {
        return type;
    }
}
