package com.example.popularmovies;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.popularmovies.Models.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class FragmentDetails extends Fragment {

    private View rootView;
    private TextView date, rate, desc;
    private ImageView poster;
    private Movie movie;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        date = rootView.findViewById(R.id.details_releaseDate);
        rate = rootView.findViewById(R.id.details_rate);
        desc = rootView.findViewById(R.id.details_desc);
        poster = rootView.findViewById(R.id.details_poster);

        Bundle b = getArguments();
        if(b != null){
            movie = b.getParcelable("movie");
        }

        if(movie != null){
            ((MainActivity)getActivity()).setActionBarTitle(movie.get_title());
            date.setText(movie.get_release_date());
            rate.setText(movie.get_rate() + "/10");
            desc.setText(movie.get_overview());
            Picasso.get()
                    .load(createURL(movie.get_poster()))
                    .error(R.drawable.placeholder)
                    .fit()
                    .into(poster, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d("Image","success");
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.d("Image", "Failed To Load  "+ e);
                        }
                    });
        }

        return rootView;
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
