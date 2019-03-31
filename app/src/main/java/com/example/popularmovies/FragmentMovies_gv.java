package com.example.popularmovies;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.popularmovies.Adapters.PosterAdapter;
import com.example.popularmovies.Models.Movie;
import com.example.popularmovies.Utils.GridItemClickListener;

import java.util.ArrayList;


public class FragmentMovies_gv extends Fragment implements GridItemClickListener {

    private ArrayList<Movie> movies;
    private View rootView;
    RecyclerView recyclerView;
    PosterAdapter adapter;
    private Toast mToast;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_movies_gv, container, false);
        ((MainActivity)getActivity()).setActionBarTitle(getString(R.string.app_name));
        checkExtras();
        movies = new ArrayList<>();
        return rootView;
    }

    private void checkExtras(){
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            movies = bundle.getParcelableArrayList("movies");
            setRecyclerView();
        }
    }

    private void setRecyclerView(){
        recyclerView = rootView.findViewById(R.id.poster_rv);
        int spanCount;
        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            spanCount = 4;
        }else {
            spanCount = 2;
        }
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), spanCount));
        recyclerView.setHasFixedSize(true);
        adapter = new PosterAdapter(getActivity(), movies, this);
        recyclerView.setAdapter(adapter);
    }

    private void openDetails(Fragment fragment, int i, Movie movie){
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putParcelable("movie", movie);
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onItemClickListener(int clickIndex, Movie movie) {
        if (mToast != null) {
            mToast.cancel();
        }
        String toastMessage = "Item #" + clickIndex + " clicked.";
        mToast = Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_LONG);
        mToast.show();
        openDetails(new FragmentDetails(), clickIndex, movie);
    }
}
