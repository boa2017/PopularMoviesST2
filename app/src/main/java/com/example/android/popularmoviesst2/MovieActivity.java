package com.example.android.popularmoviesst2;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.android.popularmoviesst2.adapters.MovieAdapter;
import com.example.android.popularmoviesst2.data.MoviesContract;
import com.example.android.popularmoviesst2.databinding.ActivityMovieBinding;
import com.example.android.popularmoviesst2.models.Movie;
import com.example.android.popularmoviesst2.sync.FetchMovieAsyncTask;

/**
 * Created by beita on 10/03/2018.
 */

public class MovieActivity extends AppCompatActivity implements OnFetchTaskCompleted {

    private final String LOG_TAG = MovieActivity.class.getSimpleName();
    private final GridView.OnItemClickListener movieClickListener = new GridView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Movie movie = (Movie) adapterView.getItemAtPosition(position);

            Intent intent = new Intent(getApplicationContext(), MovieDetailActivity.class);
            intent.putExtra(getResources().getString(R.string.movie_info), movie);

            startActivity(intent);
        }
    };
    ActivityMovieBinding mBinding;
    GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie);
        mGridView = mBinding.gridView;
        mGridView.setOnItemClickListener(movieClickListener);

        Movie mMovies;

        if (savedInstanceState == null) {
            String sortMethod = getSortMethod();

            if ((sortMethod.equals(getResources().getString(R.string.pref_sort_most_popular_values)))
                    || (sortMethod.equals(getResources().getString(R.string.pref_sort_high_rated_values)))) {
                getMovieData(sortMethod);
                if (sortMethod.equals(R.string.pref_sort_most_popular_values)) {
                    setTitle(R.string.sort_popularity);
                } else {
                    setTitle(R.string.sort_top_rated);
                }
                settingUpSharedPrefs(sortMethod);
            } else {
                settingUpSharedPrefs(getResources().getString(R.string.pref_sort_user_favourite_values));
                setTitle(R.string.sort_favorites);
                loadFavoriteMovies();
            }
        } else {
            Parcelable[] parcelable = savedInstanceState.
                    getParcelableArray(getString(R.string.movie_info));
            if (parcelable != null) {
                int numMovieObjects = parcelable.length;
                Movie[] movies = new Movie[numMovieObjects];
                for (int pos = 0; pos < numMovieObjects; pos++) {
                    movies[pos] = (Movie) parcelable[pos];
                }

                mGridView.setAdapter(new MovieAdapter(this, movies));
            }
        }

    }

    private void settingUpSharedPrefs(String order) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.pref_sort_key), order);
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.order_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.popular_item_menu) {
            settingUpSharedPrefs(getResources().getString(R.string.pref_sort_most_popular_values));
            setTitle(R.string.sort_popularity);
            getMovieData(getSortMethod());
        } else if (item.getItemId() == R.id.top_rated_item_menu) {
            settingUpSharedPrefs(getResources().getString(R.string.pref_sort_high_rated_values));
            setTitle(R.string.sort_top_rated);
            getMovieData(getSortMethod());
        } else {
            settingUpSharedPrefs(getResources().getString(R.string.pref_sort_user_favourite));
            setTitle(R.string.sort_favorites);
            loadFavoriteMovies();
        }
        return super.onOptionsItemSelected(item);

    }

    private void getMovieData(String sortMethod) {
        if (isNetworkAvailable()) {
            FetchMovieAsyncTask fetchMovieAsyncTask = new FetchMovieAsyncTask();
            fetchMovieAsyncTask.mListener = this;
            fetchMovieAsyncTask.execute(sortMethod);
        } else {
            Toast.makeText(this, R.string.error_message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        int MovieObjectsCount = mGridView.getCount();
        if (MovieObjectsCount > 0) {

            Movie[] movies = new Movie[MovieObjectsCount];
            for (int pos = 0; pos < MovieObjectsCount; pos++) {
                movies[pos] = (Movie) mGridView.getItemAtPosition(pos);
            }

            outState.putParcelableArray(getString(R.string.movie_info), movies);
        }

        super.onSaveInstanceState(outState);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void onTaskCompleted(Movie[] movies) {
        mGridView.setAdapter(new MovieAdapter(getApplicationContext(), movies));
    }


    private String getSortMethod() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_most_popular_values));
    }

    protected void loadFavoriteMovies() {
        //SQLiteDatabase db = MovieDbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = this.getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to load data");
            e.printStackTrace();
        }

        int count = cursor.getCount();
        Movie[] movies = new Movie[count];
        for (int pos = 0; pos < count; pos++) {
            movies[pos] = new Movie();
            if (cursor.moveToPosition(pos)) {
                String movieId = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_ID));
                String movieTitle = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_TITLE));
                String moviePosterPath = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_POSTER_PATH));
                String movieOverview = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_OVERVIEW));
                String movieVote = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE));
                String movieReleaseDate = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE));

                movies[pos].setOriginalTitle(movieTitle);
                movies[pos].setPosterPath("/" + moviePosterPath);
                movies[pos].setOverview(movieOverview);
                movies[pos].setVoteAverage(Double.parseDouble(movieVote));
                movies[pos].setReleaseDate(movieReleaseDate);
                movies[pos].setId(movieId);
            }
        }
        mGridView.setAdapter(new MovieAdapter(this, movies));
    }

    @Override
    protected void onResume() {
        super.onResume();
        String sortMethod = getSortMethod();
        if (sortMethod.equals(getResources().getString(R.string.favorite))) {
            loadFavoriteMovies();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this);
    }

}