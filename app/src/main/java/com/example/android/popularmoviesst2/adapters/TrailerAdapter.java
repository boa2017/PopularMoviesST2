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