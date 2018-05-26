package com.example.android.popularmoviesst2;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.popularmoviesst2.adapters.ReviewAdapter;
import com.example.android.popularmoviesst2.adapters.TrailerAdapter;
import com.example.android.popularmoviesst2.databinding.ActivityDetailBinding;
import com.example.android.popularmoviesst2.models.Movie;
import com.example.android.popularmoviesst2.models.Review;
import com.example.android.popularmoviesst2.models.Trailer;
import com.example.android.popularmoviesst2.recycler.RecyclerClickListener;
import com.example.android.popularmoviesst2.sync.FetchReviewAsyncTask;
import com.example.android.popularmoviesst2.sync.FetchTrailerAsyncTask;
import com.example.android.popularmoviesst2.utils.DateUtils;
import com.squareup.picasso.Picasso;

import java.text.ParseException;

public class MovieDetailActivity extends AppCompatActivity implements OnTrailerTaskCompleted, OnReviewTaskCompleted {

    private final String LOG_TAG = MovieDetailActivity.class.getSimpleName();
    protected RecyclerView mTrailersRV;
    ActivityDetailBinding mBinding;
    Trailer[] mTrailerList;
    Review[] mReviewList;
    ReviewAdapter reviewAdapter;
    TrailerAdapter trailerAdapter;
    TextView noTrailerView, noReviewView;
    ImageButton mFAB;
    private Movie mMovie;
    private RecyclerView mReviewsRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        mTrailersRV = mBinding.trailersRV;
        mReviewsRV = mBinding.reviewsRV;
        noTrailerView = mBinding.noTrailerView;
        noReviewView = mBinding.noReviewView;
        mFAB = mBinding.favoriteButton;


        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(getString(R.string.movie_info))) {
            mMovie = intent.getParcelableExtra(getString(R.string.movie_info));
        }

        if (mMovie.movieIsFavorite(this)) {
            mFAB.setImageResource(R.drawable.ic_favorite_on);
        } else {
            mFAB.setImageResource(R.drawable.ic_favorite_off);
        }

        mBinding.movieTitle.setText(mMovie.getOriginalTitle());

        Picasso.with(this)
                .load(mMovie.getPosterPath())

                .placeholder(R.drawable.ic_update)
                .into(mBinding.thumbnail);

        ActionBar actionBar = this.getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        String overView = mMovie.getOverview();
        if (overView == null) {
            mBinding.movieOverview.setTypeface(null, Typeface.ITALIC);
            overView = getResources().getString(R.string.no_overview_found);
        }
        mBinding.movieOverview.setText(overView);
        mBinding.movieVoteAverage.setText(mMovie.getDetailedVoteAverage());


        String releaseDate = getFormattedReleaseData(mMovie.getReleaseDate());

        mBinding.releaseDate.setText(releaseDate);

        getTrailers();

        LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mTrailersRV.setLayoutManager(trailerLayoutManager);


        mTrailersRV.addOnItemTouchListener(new RecyclerClickListener(this, new RecyclerClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String url = "https://www.youtube.com/watch?v=".concat(mTrailerList[position].getKey());
                Intent pos = new Intent(Intent.ACTION_VIEW);
                pos.setData(Uri.parse(url));
                startActivity(pos);
            }
        }));

        getReviews();
        LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(this);
        mReviewsRV.setLayoutManager(reviewLayoutManager);

        mReviewsRV.addOnItemTouchListener(new RecyclerClickListener(this, new RecyclerClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent pos = new Intent(Intent.ACTION_VIEW);
                pos.setData(Uri.parse(mReviewList[position].getUrl()));
                startActivity(pos);
            }
        }));

    }

    private void getTrailers() {
        FetchTrailerAsyncTask fetchTrailerAsyncTask = (new FetchTrailerAsyncTask());
        fetchTrailerAsyncTask.mListener = this;
        fetchTrailerAsyncTask.execute(mMovie.getId());
    }

    private void getReviews() {
        FetchReviewAsyncTask fetchReviewAsyncTask = (new FetchReviewAsyncTask());
        fetchReviewAsyncTask.mListener = this;
        fetchReviewAsyncTask.execute(mMovie.getId());
    }

    @Override
    public void onTrailerTaskCompleted(Trailer[] trailers) {
        mTrailerList = trailers;

        if (mTrailerList.length == 0) {
            mTrailersRV.setVisibility(View.INVISIBLE);
            noTrailerView.setVisibility(View.VISIBLE);
        } else {
            trailerAdapter = new TrailerAdapter(this, mTrailerList);
            mTrailersRV.setAdapter(trailerAdapter);
        }
    }

    @Override
    public void onReviewTaskCompleted(Review[] reviews) {
        mReviewList = reviews;

        if (mReviewList.length == 0) {
            noReviewView.setVisibility(View.VISIBLE);
            mReviewsRV.setVisibility(View.INVISIBLE);

        } else {
            reviewAdapter = new ReviewAdapter(this, mReviewList);
            mReviewsRV.setAdapter(reviewAdapter);
        }
    }

    public void onClickAddMovies(View view) {
        Context context = getApplicationContext();
        if (!mMovie.movieIsFavorite(context)) {
            if (mMovie.saveAsFav(context)) {
                mFAB.setImageResource(R.drawable.ic_favorite_on);
            }
        } else {
            if (mMovie.deleteAsFavorite(context)) {
                mFAB.setImageResource(R.drawable.ic_favorite_off);
            }
        }
    }

    public String getFormattedReleaseData(String releaseDate) {
        String formattedDate;
        formattedDate = releaseDate;

        if (formattedDate != null) {
            try {
                formattedDate = DateUtils.getLocalizedDate(this,
                        formattedDate, mMovie.getDateFormat());
            } catch (ParseException e) {
                Log.e(LOG_TAG, "Error with parsing movie release date", e);
            }
        } else {
            mBinding.releaseDate.setTypeface(null, Typeface.ITALIC);
            formattedDate = getResources().getString(R.string.no_release_date_found);
        }
        return formattedDate;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.share) {
            Log.d(LOG_TAG, "Checking if the key is null " + mTrailerList[0].getKey());

            Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                    .setType("text/plain")
                    .setText("https://www.youtube.com/watch?v=".concat(mTrailerList[0].getKey()))
                    .getIntent();
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            startActivity(shareIntent);
            return true;

        }
        return super.onOptionsItemSelected(item);

    }
}


