package com.example.android.popularmoviesst2.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable {
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
    private String mId;
    private String mAuthor;
    private String mContent;
    private String mUrl;

    public Review() {
    }

    protected Review(Parcel in) {
        mId = in.readString();
        mAuthor = in.readString();
        mContent = in.readString();
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int pos) {
        parcel.writeString(mId);
        parcel.writeString(mAuthor);
        parcel.writeString(mContent);
    }


}
