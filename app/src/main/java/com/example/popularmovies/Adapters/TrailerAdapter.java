package com.example.popularmovies.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.popularmovies.Models.Trailer;
import com.example.popularmovies.R;
import com.example.popularmovies.Utils.trailersClickListener;

import java.util.ArrayList;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.myViewHolder> {

    private View rootview;
    private Context context;
    private ArrayList<Trailer> trailers;
    private trailersClickListener trailersClickListener;

    public TrailerAdapter(Context context, ArrayList<Trailer> trailers, trailersClickListener trailersClickListener) {
        this.context = context;
        this.trailers = trailers;
        this.trailersClickListener = trailersClickListener;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater Inflater = LayoutInflater.from(context);
        rootview = Inflater.inflate(R.layout.trailer_item, viewGroup, false);
        return new myViewHolder(rootview);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder myViewHolder, int i) {
        myViewHolder.trailerName.setText("Trailer " + (i+1));
        myViewHolder.trailerName.requestLayout();
    }

    @Override
    public int getItemCount() {
        if (trailers == null){
            return 0;
        }
        else {
            return trailers.size();
        }
    }

    public class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView trailerName;

        private myViewHolder(View v) {
            super(v);
            trailerName = v.findViewById(R.id.trailer_name);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            trailersClickListener.OnItemClickListener(clickedPosition, trailers.get(clickedPosition));
        }
    }
}
