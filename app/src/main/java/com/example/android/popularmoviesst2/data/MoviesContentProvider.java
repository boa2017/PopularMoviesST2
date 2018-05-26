package com.example.android.popularmoviesst2.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.android.popularmoviesst2.data.MoviesContract.MoviesEntry;

/**
 * Created by beita on 26/03/2018.
 */

public class MoviesContentProvider extends ContentProvider {

    private static final int MOVIES = 100;
    private static final int MOVIES_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private MoviesDBHelper mMoviesDBHelper;

    private static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_MOVIES + "/#", MOVIES_WITH_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mMoviesDBHelper = new MoviesDBHelper(context);
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase db = mMoviesDBHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);

        Cursor mCursor;

        switch (match) {
            case MOVIES:
                mCursor = db.query(MoviesEntry.TABLE_NAME,
                        projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);

                String mSelection = "id=?";
                String[] mSelectionArgs = new String[]{id};

                mCursor = db.query(MoviesEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null, null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri:" + uri);
        }
        mCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return mCursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {

        final SQLiteDatabase db = mMoviesDBHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        Uri returnUri;

        switch (match) {
            case MOVIES:
                long id = db.insert(MoviesContract.MoviesEntry.TABLE_NAME, null, contentValues);

                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MoviesContract.MoviesEntry.CONTENT_URI, id);
                } else {
                    throw new SQLException("Problems inserting into new row" + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;

    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mMoviesDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int deleteId;

        switch (match) {
            case MOVIES:
                deleteId = db.delete(MoviesEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;

            case MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String mSelection = "id=?";
                String[] mSelectionArgs = new String[]{id};

                deleteId = db.delete(MoviesEntry.TABLE_NAME,
                        mSelection,
                        mSelectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }
        if (deleteId > 0) {
            getContext().getContentResolver().notifyChange(uri, null);

        }
        return deleteId;
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
