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

import com.example.android.popularmoviesst2.MovieDetailActivity;
import com.example.android.popularmoviesst2.models.Trailer;
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

public class FetchTrailerAsyncTask extends AsyncTask<String, Void, Trailer[]> {

    public final String LOG_TAG = FetchTrailerAsyncTask.class.getSimpleName();
    public MovieDetailActivity mListener;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Trailer[] doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String trailersJsonString = null;

        if (params.length == 0) {
            return null;
        }

        try {
            URL url = NetworkUtils.buildVideosUrl(params);

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

            trailersJsonString = buffer.toString();
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
            return getTrailerDataFromJson(trailersJsonString);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    private Trailer[] getTrailerDataFromJson(String trailersJsonString) throws JSONException {
        final String RESULTS = "results";
        final String ID = "id";
        final String KEY = "key";

        JSONObject trailersJSON = new JSONObject(trailersJsonString);
        JSONArray resultsArray = trailersJSON.getJSONArray(RESULTS);

        Trailer[] trailers = new Trailer[resultsArray.length()];

        for (int pos = 0; pos < resultsArray.length(); pos++) {
            trailers[pos] = new Trailer();
            JSONObject trailerInfo = resultsArray.getJSONObject(pos);

            trailers[pos].setId(trailerInfo.getString(ID));
            trailers[pos].setKey(trailerInfo.getString(KEY));
        }

        return trailers;
    }


    @Override
    protected void onPostExecute(Trailer[] trailers) {
        if (trailers != null) {
            mListener.onTrailerTaskCompleted(trailers);
        }
    }
}
