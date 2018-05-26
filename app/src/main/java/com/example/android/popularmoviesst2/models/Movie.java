package com.example.android.popularmoviesst2.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.example.android.popularmoviesst2.R;
import com.example.android.popularmoviesst2.data.MoviesContract;
import com.example.android.popularmoviesst2.utils.DateUtils;

import java.text.ParseException;

import static com.example.android.popularmoviesst2.sync.FetchReviewAsyncTask.LOG_TAG;

public class Movie implements Parcelable {
    private static final String DATE_FORMAT = "yyyy-mm-dd";
    private String mOriginalTitle;
    private String mPosterPath;
    private String mOverview;
    private Double mVoteAverage;
    private String mVotes;
    private String mReleaseDate;
    private String mId;
    private Double mPopularity;


    public Movie() {
    }

    public void setOriginalTitle(String originalTitle) {
        mOriginalTitle = originalTitle;
    }

    public void setPosterPath(String posterPath) {
        mPosterPath = posterPath;
    }


    public void setOverview(String overview) {
        if(!overview.equals("null")) {
            mOverview = overview;
        }
    }

    public void setVoteAverage(Double voteAverage) {
        mVoteAverage = voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        mVotes = voteAverage;
    }


    public void setReleaseDate(String releaseDate) {
        if(!releaseDate.equals("null")) {
            mReleaseDate = releaseDate;
        }
    }

    public void setId(String id) {
        mId= id;
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public double getPopularity() {return mPopularity;}

    public String getPosterPath() {
        final String POSTER_BASE_URL = "https://image.tmdb.org/t/p/w185";

        return POSTER_BASE_URL + mPosterPath;
    }

    public String getOverview() {
        return mOverview;
    }

    public Double getVoteAverage() {
        return mVoteAverage;
    }


    public String getReleaseDate() {
        return mReleaseDate;
    }


    public String getDetailedVoteAverage() {
        return String.valueOf(getVoteAverage()) + "/10";
    }


    public String getDateFormat() {
        return DATE_FORMAT;
    }

    public String getId(){
        return mId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mOriginalTitle);
        dest.writeString(mPosterPath);
        dest.writeString(mOverview);
        dest.writeValue(mVoteAverage);
        dest.writeString(mReleaseDate);
        dest.writeString(mId);
        dest.writeValue(mPopularity);
    }

    public Movie(Parcel in) {
        mOriginalTitle = in.readString();
        mPosterPath = in.readString();
        mOverview = in.readString();
        mVoteAverage = (Double) in.readValue(Double.class.getClassLoader());
        mReleaseDate = in.readString();
        mId = in.readString();
        mPopularity = (Double) in.readValue(Double.class.getClassLoader());
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public boolean movieIsFavorite(Context context){

        Cursor cursor=null;
        try{
            cursor = context.getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI,
                    new String[]{MoviesContract.MoviesEntry.COLUMN_ID},
                    MoviesContract.MoviesEntry.COLUMN_ID + "=?",
                    new String[]{this.mId},
                    null);
        }catch (Exception e){
            Log.e(LOG_TAG, "No data found");
            e.printStackTrace();
        }

        if(cursor!=null){
            boolean fav = cursor.getCount()>0;
            cursor.close();
            return fav;
        }
        return false;
    }

    public boolean saveAsFav(Context context){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_ID, mId);
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_TITLE, mOriginalTitle);
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_POSTER_PATH, mPosterPath);
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_OVERVIEW, mOverview);
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE, mVoteAverage);

        String releaseDate = getFormattedReleaseData(context,mReleaseDate);
        Log.d(LOG_TAG,"Release Date =" + releaseDate);

        contentValues.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, releaseDate);

        Uri uri = context.getContentResolver().insert(MoviesContract.MoviesEntry.CONTENT_URI, contentValues);

        if (uri != null) {
            Toast.makeText(context, "Movie marked as Favorite " , Toast.LENGTH_LONG).show();
            return true;
        }else {
            Toast.makeText(context, "Movie not marked as Favorite ", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private String getPosterPath(String path){
        String posterPath = path;
        String retPosterPath = null;
        for(String retPath:posterPath.split("/")){
            retPosterPath= retPath;
        }
        return retPosterPath;
    }


    public boolean deleteAsFavorite(Context context) {
        int deletedRows = context.getContentResolver().delete(MoviesContract.MoviesEntry.CONTENT_URI,
                MoviesContract.MoviesEntry.COLUMN_ID+ "=?",new String[]{this.mId});
        if (deletedRows>0){
            Toast.makeText(context, "Movie removed from favorites",Toast.LENGTH_LONG).show();
            return true;
        }else {
            Toast.makeText(context,"Movie not removed from favorites. Please, try again", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public String getFormattedReleaseData(Context context, String releaseDate) {
        String formattedDate;
        formattedDate=releaseDate;

        if(formattedDate != null) {
            try {
                formattedDate = DateUtils.getLocalizedDate(context,
                        formattedDate, getDateFormat());
            } catch (ParseException e) {
                Log.e(LOG_TAG, "Error with parsing movie release date", e);
            }
        } else {

            formattedDate = context.getResources().getString(R.string.no_release_date_found);
        }
        return formattedDate;
    }
}
