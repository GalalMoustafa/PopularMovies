package com.example.popularmovies.Utils;

import android.arch.lifecycle.LiveData;

import com.example.popularmovies.Models.Movie;

import java.util.ArrayList;
import java.util.List;

public interface FetchCompletedCallback {

    void onTaskComplete(List<Movie> movies);

}
