package com.example.popularmovies.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.popularmovies.Models.Review;
import com.example.popularmovies.R;
import com.example.popularmovies.Utils.reviewsClickListener;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.myViewHolder> {

    private View rootview;
    private Context context;
    private ArrayList<Review> reviews;
    private reviewsClickListener reviewsClickListener;

    public ReviewAdapter(Context context, ArrayList<Review> reviews, com.example.popularmovies.Utils.reviewsClickListener reviewsClickListener) {
        this.context = context;
        this.reviews = reviews;
        this.reviewsClickListener = reviewsClickListener;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater Inflater = LayoutInflater.from(context);
        rootview = Inflater.inflate(R.layout.review_item, viewGroup, false);
        return new myViewHolder(rootview);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder myViewHolder, int i) {
        myViewHolder.reviewName.setText("Review " + (i+1) + ", Author: " + reviews.get(i).getAutor());
        myViewHolder.reviewName.requestLayout();
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView reviewName;

        private myViewHolder(View v) {
            super(v);
            reviewName = v.findViewById(R.id.review_name);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            reviewsClickListener.onItemClickListener(clickedPosition, reviews.get(clickedPosition));
        }
    }
}
