package com.example.popularmovies.Utils;

import com.example.popularmovies.Models.Movie;

import java.util.ArrayList;

public interface FetchCompletedCallback {

    void onTaskComplete(ArrayList<Movie> movies);

}
