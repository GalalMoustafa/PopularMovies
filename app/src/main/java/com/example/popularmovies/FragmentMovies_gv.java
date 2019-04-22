package com.example.popularmovies;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.popularmovies.Adapters.PosterAdapter;
import com.example.popularmovies.Models.Movie;
import com.example.popularmovies.Utils.GridItemClickListener;

import java.util.ArrayList;


public class FragmentMovies_gv extends Fragment implements GridItemClickListener {

    private View rootView;
    RecyclerView recyclerView;
    PosterAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_movies_gv, container, false);
        getMovies();
        return rootView;
    }

    private void getMovies() {
        if (checkType().equals(getResources().getString(R.string.favourites))) {
            setRecyclerView(((MainActivity) getActivity()).getMoviesDB());
        } else {
            setRecyclerView(((MainActivity) getActivity()).getMovies());
        }
    }


    private void setRecyclerView(ArrayList<Movie> movies) {
        recyclerView = rootView.findViewById(R.id.poster_rv);
        int spanCount;
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && !((MainActivity) getActivity()).mTwoPane) {
            spanCount = 4;
        } else {
            spanCount = 2;
        }
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), spanCount));
        recyclerView.setHasFixedSize(true);
        adapter = new PosterAdapter(getActivity(), movies, this);
        recyclerView.setAdapter(adapter);
    }

    private String checkType() {
        MainActivity activity = (MainActivity) getActivity();
        String type = activity.type;
        return type;
    }

    public void UpdateAdapter() {
        if (checkType().equals(getResources().getString(R.string.favourites))) {
            adapter.setMovies(((MainActivity) getActivity()).getMoviesDB());
        } else {
            adapter.setMovies(((MainActivity) getActivity()).getMovies());
        }
    }


    private void openDetails(Fragment fragment, int i, Movie movie) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putParcelable("movie", movie);
        fragment.setArguments(bundle);
        if (((MainActivity) getActivity()).mTwoPane) {
            fragmentTransaction.add(R.id.details_container, fragment);
        } else {
            fragmentTransaction.add(R.id.container, fragment);
        }
        fragmentTransaction.addToBackStack(getResources().getString(R.string.detailsFragment));
        fragmentTransaction.commit();
    }

    @Override
    public void onItemClickListener(int clickIndex, Movie movie) {
        openDetails(new FragmentDetails(), clickIndex, movie);
    }


}
