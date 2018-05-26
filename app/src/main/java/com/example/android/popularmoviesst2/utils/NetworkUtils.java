package com.example.android.popularmoviesst2.utils;

import android.net.Uri;
import android.util.Log;

import com.example.android.popularmoviesst2.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by beita on 10/03/2018.
 */

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private final static String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie";
    private final static String WATCH_VIDEO_BASE_URL = "https://www.youtube.com/watch";
    private final static String MOVIE_POSTER_BASE_URL = "http://image.tmdb.org/t/p";
    private final static String MOVIE_VIDEOS = "videos";
    private final static String MOVIE_REVIEWS = "reviews";
    private final static String API_PARAM_NAME = "api_key";
    private final static String MOVIE_POSTER_SIZE = "w185";
    private final static String apiKey = BuildConfig.API_KEY;
    private final static String watchVideoKey = "v";

    public static URL buildMoviesUrl(String[] params) throws MalformedURLException {
        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(params[0])
                .appendQueryParameter(API_PARAM_NAME, BuildConfig.API_KEY)
                .build();

        URL url = null;
        url = new URL(builtUri.toString());
        return url;
    }

    public static URL buildPostersURL(String posterPath) {
        Uri builtUri = Uri.parse(MOVIE_POSTER_BASE_URL).buildUpon()
                .appendPath(MOVIE_POSTER_SIZE)
                .appendEncodedPath(posterPath).build();
        URL builtURL = null;

        try {
            builtURL = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return builtURL;
    }

    public static URL buildBackdropsURL(String backdropPath) {
        Uri builtUri = Uri.parse(MOVIE_POSTER_BASE_URL).buildUpon()
                .appendPath(MOVIE_POSTER_SIZE)
                .appendPath(backdropPath).build();
        URL builtURL = null;

        try {
            builtURL = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return builtURL;
    }

    public static URL buildWatchVideoURL(String[] params, String videoKey) throws MalformedURLException {

        Log.i(TAG, "Video Key - " + videoKey);

        Uri builtUri = Uri.parse(WATCH_VIDEO_BASE_URL)
                .buildUpon()
                .appendQueryParameter(watchVideoKey, videoKey)
                .build();

        Log.i(TAG, "Watch Video URI - " + builtUri);

        URL builtURL = null;

        try {
            builtURL = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return builtURL;
    }

    public static URL buildVideosUrl(String[] params) throws MalformedURLException {
        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(params[0]).appendPath(MOVIE_VIDEOS)
                .appendQueryParameter(API_PARAM_NAME, BuildConfig.API_KEY)
                .build();

        URL url = null;
        url = new URL(builtUri.toString());
        return url;
    }

    public static URL buildReviewsUrl(String[] params) throws MalformedURLException {
        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(params[0]).appendPath(MOVIE_REVIEWS)
                .appendQueryParameter(API_PARAM_NAME, BuildConfig.API_KEY)
                .build();

        URL url = null;
        url = new URL(builtUri.toString());
        return url;
    }

    //Method to return full result from HTTP response

    public static String getResponseFromHttpURL(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput)
                return scanner.next();
            else
                return null;
        } finally {
            urlConnection.disconnect();
        }
    }
}
