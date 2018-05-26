/*
 * PROJECT LICENSE
 *
 * This project was submitted by Beatriz Ovejero as part of the Android Developer
 * Nanodegree at Udacity.
 *
 * As part of Udacity Honor code, your submissions must be your own work, hence
 * submitting this project as yours will cause you to break the Udacity Honor Code
 * and the suspension of your account.
 *
 * As author of the project, I allow you to check it as a reference, but if you submit it
 * as your own project, it's your own responsibility if you get expelled.
 *
 * Copyright (c) 2018 Beatriz Ovejero
 *
 * Besides the above notice, the following license applies and this license notice must be
 * included in all works derived from this project.
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
