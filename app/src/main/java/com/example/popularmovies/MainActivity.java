package com.example.popularmovies;

import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.popularmovies.Models.Movie;
import com.example.popularmovies.Utils.FetchCompletedCallback;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements FetchCompletedCallback, FragmentManager.OnBackStackChangedListener {

    private int item_selected = 0;
    private ArrayList<Movie> movies;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        readSortPref();
        movies = new ArrayList<>();
        checkSelected();
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        shouldDisplayHomeUp();
        if (savedInstanceState == null){
            new FetchMoviesData(this).execute(type);
        }
    }

    private void checkSelected(){
        if (item_selected == 0){
            type =  getResources().getString(R.string.topRated);
        }
        else if (item_selected == 1){
            type = getResources().getString(R.string.mostPopular);
        }
    }

    private void saveSortPref(int selected){
        SharedPreferences sp = getSharedPreferences("sort_prefs", MainActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("sortBy", selected);
        editor.apply();
    }

    private void readSortPref(){
        SharedPreferences sp = getSharedPreferences("sort_prefs", MainActivity.MODE_PRIVATE);
        item_selected = sp.getInt("sortBy", 0);
    }

    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("movies", movies);
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.container, fragment);
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

    public void shouldDisplayHomeUp(){
        if (getSupportFragmentManager().getBackStackEntryCount() > 0){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
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
        if (item_selected == 0){
            first.setChecked(true);

        }
        else if (item_selected == 1){
            second.setChecked(true);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
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
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onTaskComplete(ArrayList<Movie> movies) {
        this.movies = movies;
        setFragment(new FragmentMovies_gv());
    }
}
