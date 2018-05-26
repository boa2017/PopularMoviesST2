package com.example.android.popularmoviesst2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by beita on 26/03/2018.
 */

public class MoviesDBHelper extends SQLiteOpenHelper {

    private static final String TAG = MoviesDBHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "movies.db";

    private static final int DATABASE_VERSION = 1;

    // Constructor
    public MoviesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MoviesContract.MoviesEntry.TABLE_NAME + " (" +
                MoviesContract.MoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MoviesContract.MoviesEntry.COLUMN_ID + " INTEGER NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_POSTER_PATH + " TEXT, " +
                MoviesContract.MoviesEntry.COLUMN_OVERVIEW + " TEXT, " +
                MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE + " TEXT, " +
                MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE + " TEXT" + "); ";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE " + MoviesContract.MoviesEntry.TABLE_NAME);


    }
}
