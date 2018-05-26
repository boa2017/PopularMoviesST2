package com.example.android.popularmoviesst2.sync;

import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmoviesst2.MovieDetailActivity;
import com.example.android.popularmoviesst2.models.Review;
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

public class FetchReviewAsyncTask extends AsyncTask<String, Void, Review[]> {
    public final static String LOG_TAG = FetchReviewAsyncTask.class.getSimpleName();
    public MovieDetailActivity mListener;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Review[] doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String reviewsJsonString = null;

        if (params.length == 0) {
            return null;
        }

        try {
            URL url = NetworkUtils.buildReviewsUrl(params);

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

            reviewsJsonString = buffer.toString();
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
            return getReviewDataFromJson(reviewsJsonString);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    private Review[] getReviewDataFromJson(String reviewsJsonString) throws JSONException {
        final String RESULTS = "results";
        final String ID = "id";
        final String AUTHOR = "author";
        final String CONTENT = "content";
        final String URL = "url";

        JSONObject reviewsJson = new JSONObject(reviewsJsonString);
        JSONArray resultsArray = reviewsJson.getJSONArray(RESULTS);

        Review[] reviews = new Review[resultsArray.length()];

        for (int pos = 0; pos < resultsArray.length(); pos++) {
            reviews[pos] = new Review();

            JSONObject reviewInfo = resultsArray.getJSONObject(pos);
            reviews[pos].setId(reviewInfo.getString(ID));
            reviews[pos].setAuthor(reviewInfo.getString(AUTHOR));
            reviews[pos].setContent(reviewInfo.getString(CONTENT));
            reviews[pos].setUrl(reviewInfo.getString(URL));

        }
        Log.d(LOG_TAG, "Data Added successfully");
        return reviews;

    }

    @Override
    protected void onPostExecute(Review[] reviews) {

        if (reviews != null) {
            mListener.onReviewTaskCompleted(reviews);
        }
    }
}