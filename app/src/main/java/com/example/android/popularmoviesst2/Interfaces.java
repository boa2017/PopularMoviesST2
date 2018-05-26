package com.example.android.popularmoviesst2;

import com.example.android.popularmoviesst2.models.Movie;
import com.example.android.popularmoviesst2.models.Review;
import com.example.android.popularmoviesst2.models.Trailer;

interface OnFetchTaskCompleted {
    public void onTaskCompleted(Movie[] movies);

}

interface OnTrailerTaskCompleted {
    public void onTrailerTaskCompleted(Trailer[] trailers);
}

interface OnReviewTaskCompleted {
    public void onReviewTaskCompleted(Review[] reviews);
}
