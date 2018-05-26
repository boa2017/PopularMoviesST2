package com.example.android.popularmoviesst2.sync;

import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmoviesst2.MovieActivity;
import com.example.android.popularmoviesst2.models.Movie;
import com.example.android.popularmoviesst2.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchMovieAsyncTask extends AsyncTask<String, Void, Movie[]> {
    public final String LOG_TAG = FetchMovieAsyncTask.class.getSimpleName();
    public MovieActivity mListener = null;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Movie[] doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String moviesJson = null;

        if (params.length == 0) {
            return null;
        }

        try {
            URL url = NetworkUtils.buildMoviesUrl(params);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();

            StringBuffer buffer = new StringBuffer();

            if (inputStream == null) {
                return null;
            }

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = reader.readLine()) != null) {

                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }

            moviesJson = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            return getMovieDataFromJson(moviesJson);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Movie[] movies) {
        if (movies != null) {
            mListener.onTaskCompleted(movies);

        }
    }

    private Movie[] getMovieDataFromJson(String movieJSONData) throws JSONException {

        final String MOVIE_ID = "id";
        final String MOVIE_TITLE = "title";
        final String MOVIE_POPULARITY = "popularity";
        final String MOVIE_OVERVIEW = "overview";
        final String MOVIE_RELEASE_DATE = "release_date";
        final String MOVIE_OUTPUT = "results";
        final String MOVIE_VOTE_COUNT = "vote_count";
        final String MOVIE_VOTE_AVERAGE = "vote_average";
        final String MOVIE_POSTER_PATH = "poster_path";
        final String MOVIE_BACKDROP_PATH = "backdrop_path";

        JSONObject movieJSON = new JSONObject(movieJSONData);

        JSONArray movieArray = movieJSON.getJSONArray(MOVIE_OUTPUT);
        Movie[] movies = new Movie[movieArray.length()];

        for (int pos = 0; pos < movieArray.length(); pos++) {
            movies[pos] = new Movie();
            JSONObject movieObject = movieArray.getJSONObject(pos);

            movies[pos].setId(movieObject.getString(MOVIE_ID));
            movies[pos].setVoteAverage(movieObject.getDouble(MOVIE_VOTE_AVERAGE));
            movies[pos].setOriginalTitle(movieObject.getString(MOVIE_TITLE));
            movies[pos].setPosterPath(movieObject.getString(MOVIE_POSTER_PATH));
            movies[pos].setOverview(movieObject.getString(MOVIE_OVERVIEW));
            movies[pos].setReleaseDate(movieObject.getString(MOVIE_RELEASE_DATE));

        }
        return movies;
    }
}
