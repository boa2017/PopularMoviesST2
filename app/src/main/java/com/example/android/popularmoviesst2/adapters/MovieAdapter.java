package com.example.android.popularmoviesst2.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.android.popularmoviesst2.R;
import com.example.android.popularmoviesst2.models.Movie;
import com.squareup.picasso.Picasso;

/**
 * Created by beita on 10/03/2018.
 */

public class MovieAdapter extends BaseAdapter {
    //Member Variables
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();
    private final Movie[] mMovies;
    private Context mContext;

    public MovieAdapter(Context context, Movie[] movies) {
        mContext = context;
        mMovies = movies;
    }

    @Override
    public int getCount() {

        if (mMovies == null || mMovies.length == 0) {
            return -1;
        }

        return mMovies.length;
    }

    @Override
    public Movie getItem(int position) {
        if (mMovies == null || mMovies.length == 0) {
            return null;
        }

        return mMovies[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setAdjustViewBounds(true);
        } else {
            imageView = (ImageView) convertView;
        }

        Picasso.with(mContext)
                .load(mMovies[position].getPosterPath())
                .resize(185, 278)
                .error(R.drawable.ic_error_outline_black)
                .placeholder(R.drawable.ic_update)
                .into(imageView);

        return imageView;
    }
}