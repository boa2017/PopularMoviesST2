package com.example.android.popularmoviesst2.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmoviesst2.R;
import com.example.android.popularmoviesst2.models.Trailer;
import com.squareup.picasso.Picasso;

/**
 * Created by beita on 26/03/2018.
 */

public class TrailerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Trailer[] mTrailer;
    private final Context mContext;

    public TrailerAdapter(Context context, Trailer[] data) {
        mContext = context;
        mTrailer = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View view;

        view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.trailer_view, parent, false
        );
        viewHolder = new ItHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String id = mTrailer[position].getKey();
        String thumbnailURL = "https://img.youtube.com/vi/".concat(id).concat("/default.jpg");
        Picasso.with(mContext)
                .load(thumbnailURL)
                .placeholder(R.drawable.ic_video)
                .into(((ItHolder) holder)
                        .imageView);
    }

    @Override
    public int getItemCount() {
        return mTrailer.length;
    }

    public static class ItHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ItHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.trailer);
        }
    }
}