package com.example.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.popularmovies.Models.Movie;
import com.example.popularmovies.Utils.FetchCompletedCallback;

import java.util.ArrayList;
import java.util.List;

import Database.AppDatabase;

public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener, FetchCompletedCallback {

    private int item_selected = 0;
    public String type;
    public ArrayList<Movie> movies, moviesDB;
    private AppDatabase mDb;
    public boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        readSortPref();
        checkSelected();
        movies = new ArrayList<>();
        moviesDB = new ArrayList<>();
        mDb = AppDatabase.getInstance(getApplicationContext());


        getSupportFragmentManager().addOnBackStackChangedListener(this);
        shouldDisplayHomeUp();

        if (this.getResources().getConfiguration().smallestScreenWidthDp >= 600) {
            mTwoPane = true;
        } else {
            mTwoPane = false;
        }
        if (savedInstanceState == null) {
            loadFromDB();
            if (type.equals(getResources().getString(R.string.favourites))) {
                Log.d("type", "favourites");
                setFragment(new FragmentMovies_gv());
            } else {
                new FetchMoviesData(this).execute(type);
            }
        } else {
            movies = savedInstanceState.getParcelableArrayList("movies");
            moviesDB = savedInstanceState.getParcelableArrayList("moviesDB");
            type = savedInstanceState.getString("type");
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("movies", movies);
        outState.putParcelableArrayList("moviesDB", moviesDB);
        outState.putString("type", type);
    }

    public ArrayList<Movie> getMovies() {
        return movies;
    }

    public ArrayList<Movie> getMoviesDB() {
        return moviesDB;
    }

    private void loadFromDB() {
        LiveData<List<Movie>> moviesFromDB = mDb.movieDao().LoadAllMovies();
        Log.d("DB", "Data Loaded");
        moviesFromDB.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> moviesList) {
                moviesDB = new ArrayList<>(moviesList);

                if (type.equals(getResources().getString(R.string.favourites))) {
                    Fragment f = getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.mainFragment));
                    ((FragmentMovies_gv) f).UpdateAdapter();
                }
                Log.d("DB", "Data Changed");
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mTwoPane) {
            finish();
        } else {
            super.onBackPressed();
            if (getSupportFragmentManager().findFragmentById(R.id.container) instanceof FragmentDetails) {
                Fragment f = getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.mainFragment));
                ((FragmentMovies_gv) f).UpdateAdapter();
            }
        }
    }

    private void checkSelected() {
        if (item_selected == 0) {
            type = getResources().getString(R.string.topRated);
        } else if (item_selected == 1) {
            type = getResources().getString(R.string.mostPopular);
        } else if (item_selected == 2) {
            type = getResources().getString(R.string.favourites);
        }
    }

    private void saveSortPref(int selected) {
        SharedPreferences sp = getSharedPreferences("sort_prefs", MainActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("sortBy", selected);
        editor.apply();
    }

    private void readSortPref() {
        SharedPreferences sp = getSharedPreferences("sort_prefs", MainActivity.MODE_PRIVATE);
        item_selected = sp.getInt("sortBy", 0);
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.container, fragment, getResources().getString(R.string.mainFragment));
        getSupportFragmentManager().popBackStack();
        fragmentTransaction.commit();
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onBackStackChanged() {
        shouldDisplayHomeUp();
    }


    public void shouldDisplayHomeUp() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle(R.string.app_name);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_by:
                View v = findViewById(R.id.sort_by);
                registerForContextMenu(v);
                openContextMenu(v);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.sort_menu, menu);
        MenuItem first = menu.findItem(R.id.sort_toprated);
        MenuItem second = menu.findItem(R.id.sort_mostpopular);
        MenuItem third = menu.findItem(R.id.sort_fav);
        if (item_selected == 0) {
            first.setChecked(true);

        } else if (item_selected == 1) {
            second.setChecked(true);
        } else if (item_selected == 2) {
            third.setChecked(true);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_toprated:
                item.setChecked(true);
                item_selected = 0;
                type = getResources().getString(R.string.topRated);
                saveSortPref(0);
                new FetchMoviesData(this).execute(type);
                return true;

            case R.id.sort_mostpopular:
                item.setChecked(true);
                item_selected = 1;
                type = getResources().getString(R.string.mostPopular);
                saveSortPref(1);
                new FetchMoviesData(this).execute(type);
                return true;

            case R.id.sort_fav:
                item.setChecked(true);
                item_selected = 2;
                type = getResources().getString(R.string.favourites);
                saveSortPref(2);
                FragmentMovies_gv f = (FragmentMovies_gv) getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.mainFragment));
                if (!mTwoPane) {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStack();
                    }
                }
                f.UpdateAdapter();
                return true;

        }
        return super.onContextItemSelected(item);
    }


    @Override
    public void onTaskComplete(List<Movie> m) {
        movies = new ArrayList<>(m);

        FragmentMovies_gv f = (FragmentMovies_gv) getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.mainFragment));
        if (f == null) {
            setFragment(new FragmentMovies_gv());
        } else {
            if (!mTwoPane) {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                }
            }
            f.UpdateAdapter();
        }
    }
}
