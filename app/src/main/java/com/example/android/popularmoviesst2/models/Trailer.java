package com.example.android.popularmoviesst2.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Trailer implements Parcelable {
    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };
    private String mId;
    private String mKey;

    public Trailer() {

    }

    private Trailer(Parcel in) {
        mId = in.readString();
        mKey = in.readString();
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int pos) {
        parcel.writeString(mId);
        parcel.writeString(mKey);
    }


}