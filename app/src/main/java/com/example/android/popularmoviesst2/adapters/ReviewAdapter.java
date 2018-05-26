package com.example.android.popularmoviesst2.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmoviesst2.R;
import com.example.android.popularmoviesst2.models.Review;

/**
 * Created by beita on 26/03/2018.
 */

public class ReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mContext;
    private final Review[] mReview;

    public ReviewAdapter(Context context, Review[] review) {
        mContext = context;
        mReview = review;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_view, parent, false);
        viewHolder = new ItHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ItHolder) holder).authorTV.setText(mReview[position].getAuthor());
        ((ItHolder) holder).contentTV.setText(mReview[position].getContent());
    }

    @Override
    public int getItemCount() {
        return mReview.length;
    }

    public static class ItHolder extends RecyclerView.ViewHolder {
        TextView authorTV, contentTV;

        public ItHolder(View itemView) {
            super(itemView);

            authorTV = (TextView) itemView.findViewById(R.id.author);
            contentTV = (TextView) itemView.findViewById(R.id.review);
        }
    }
}