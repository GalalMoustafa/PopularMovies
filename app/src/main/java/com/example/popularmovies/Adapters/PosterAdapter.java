package com.example.popularmovies.Adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.popularmovies.MainActivity;
import com.example.popularmovies.Models.Movie;
import com.example.popularmovies.R;
import com.example.popularmovies.Utils.GridItemClickListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.myViewHolder> {

    private Context context;
    private ArrayList<Movie> movies;
    private Point size;
    private GridItemClickListener gridItemClickListener;

    public PosterAdapter(Context context, ArrayList<Movie> movies, GridItemClickListener gridItemClickListener) {
        this.context = context;
        this.movies = movies;
        this.gridItemClickListener = gridItemClickListener;
        getDisplayWidth();
    }


    private void getDisplayWidth(){
        Display display = ((MainActivity)context).getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater Inflater = LayoutInflater.from(context);
        View view = Inflater.inflate(R.layout.grid_item, viewGroup, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder myViewHolder, int i) {
        Picasso.get()
                .load(createURL(movies.get(i).get_poster()))
                .error(R.drawable.placeholder)
                .fit()
                .into(myViewHolder.img, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d("Image","success");
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d("Image", "Failed To Load  "+ e);
                    }
                });
        if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            myViewHolder.img.getLayoutParams().width = size.x/4;
        }else {
            myViewHolder.img.getLayoutParams().width = size.x/2;
        }
        myViewHolder.img.requestLayout();
    }

    @Override
    public int getItemCount() {
        if (movies == null){
            return 0;
        }else {
            return movies.size();
        }
    }

    public void setMovies(ArrayList<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    public class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView img;

        private myViewHolder(View v) {
            super(v);
            img = v.findViewById(R.id.poster_img);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            gridItemClickListener.onItemClickListener(clickedPosition, movies.get(clickedPosition));
        }
    }

    private Uri createURL(String link) {
        StringBuilder s = new StringBuilder();
        s.append("/t/p");
        s.append("/w185");
        s.append(link);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("image.tmdb.org")
                .path(s.toString());
        Log.d("linkk", builder.build().toString());
        return builder.build();
    }


}
