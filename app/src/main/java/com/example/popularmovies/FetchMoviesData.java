package com.example.popularmovies;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.popularmovies.Models.Movie;
import com.example.popularmovies.Utils.FetchCompletedCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class FetchMoviesData extends AsyncTask<String, Void, String> {

    private FetchCompletedCallback fetchCompletedCallback;

    final private String OWM_TITLE = "original_title";
    final private String OWM_POSTER = "poster_path";
    final private String OWM_RELEASE_DATE = "release_date";
    final private String OWM_SYNOPSIS = "overview";
    final private String OWM_RATING = "vote_average";
    final private String OWM_ID = "id";

    private HttpURLConnection urlConnection = null;
    private BufferedReader reader = null;
    private ProgressDialog progressDialog;
    private Context context;

    public FetchMoviesData(Context context){
        this.context = context;
        fetchCompletedCallback = (FetchCompletedCallback) context;
    }


    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading, please wait.");
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        String type = params[0];
        URL url;
        String MoviesJsonStr = null;
        try {
            url = new URL(createURI(type).toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            MoviesJsonStr = buffer.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("IOexception", "Error closing stream", e);
                }
            }
        }
        return MoviesJsonStr;
    }

    @Override
    protected void onPostExecute(String json) {
        ArrayList<Movie> movies = new ArrayList<>();
        if(json != null){
            JSONObject MoviesJson = null;
            try {
                MoviesJson = new JSONObject(json);
                JSONArray MoviesArray = MoviesJson.getJSONArray("results");
                for (int i = 0; i < MoviesArray.length(); i++) {
                    JSONObject Movie = MoviesArray.getJSONObject(i);
                    movies.add(new Movie(Movie.getString(OWM_ID),
                            Movie.getString(OWM_TITLE),
                            Movie.getString(OWM_POSTER),
                            Movie.getString(OWM_SYNOPSIS),
                            Movie.getString(OWM_RELEASE_DATE),
                            Movie.getString(OWM_RATING)));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            Log.d("Failed","No Response From API");
        }
        fetchCompletedCallback.onTaskComplete(movies);
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private Uri createURI(String queryType) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("api.themoviedb.org")
                .path("3/movie/")
                .appendPath(queryType)
                .appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_KEY);

        Log.d("url", builder.build().toString());
        return builder.build();
    }
}
