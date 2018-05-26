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
