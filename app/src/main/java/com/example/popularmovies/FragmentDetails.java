package com.example.popularmovies;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.popularmovies.Adapters.ReviewAdapter;
import com.example.popularmovies.Adapters.TrailerAdapter;
import com.example.popularmovies.Models.Movie;
import com.example.popularmovies.Models.Review;
import com.example.popularmovies.Models.Trailer;
import com.example.popularmovies.Utils.AppExecutors;
import com.example.popularmovies.Utils.reviewsClickListener;
import com.example.popularmovies.Utils.trailersClickListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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

import Database.AppDatabase;

public class FragmentDetails extends Fragment implements trailersClickListener, reviewsClickListener {

    private View rootView;
    private TextView date, desc, trailersHeader, reviewsHeader;
    private ImageView poster, fav_img;
    private Movie movie;
    private AppDatabase mDb;
    private RatingBar rate;
    private boolean found = false;
    private int fav;
    private RecyclerView trailersList, reviewsList;
    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;
    public ArrayList<Trailer> trailers;
    public ArrayList<Review> reviews;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mDb = AppDatabase.getInstance(getContext().getApplicationContext());
        date = rootView.findViewById(R.id.details_releaseDate);
        rate = rootView.findViewById(R.id.details_rate);
        desc = rootView.findViewById(R.id.details_desc);
        poster = rootView.findViewById(R.id.details_poster);
        fav_img = rootView.findViewById(R.id.details_fav_img);
        fav_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SaveClicked", "clicked");
                ChangeStatus();
            }
        });

        trailers = new ArrayList<>();
        reviews = new ArrayList<>();
        trailersList = rootView.findViewById(R.id.trailersList);
        reviewsList = rootView.findViewById(R.id.reviewsList);
        trailersHeader = rootView.findViewById(R.id.trailerTitle);
        reviewsHeader = rootView.findViewById(R.id.reviewsTitle);


        if (savedInstanceState != null) {
            trailers = savedInstanceState.getParcelableArrayList("trailers");
            reviews = savedInstanceState.getParcelableArrayList("reviews");
            if (trailers.size() == 0) {
                trailersHeader.setText(getContext().getResources().getString(R.string.notrailers));
            }
            if (reviews.size() == 0) {
                reviewsHeader.setText(getContext().getResources().getString(R.string.noreviews));
            }
            movie = savedInstanceState.getParcelable("movie");
        } else {
            Bundle b = getArguments();
            if (b != null) {
                movie = b.getParcelable("movie");
            }
            new FetchTrailers().execute(movie.get_id());
            new FetchReviews().execute(movie.get_id());
        }


        if (movie != null) {
            CheckInDB();
            ((MainActivity) getActivity()).setActionBarTitle(movie.get_title());
            date.setText(movie.get_release_date());
            rate.setIsIndicator(true);
            rate.setRating(Float.parseFloat(movie.get_rate()) / 2.0f);
            desc.setText(movie.get_overview());
            Picasso.get()
                    .load(createURL(movie.get_poster()))
                    .error(R.drawable.placeholder)
                    .fit()
                    .into(poster, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d("Image", "success");
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.d("Image", "Failed To Load  " + e);
                        }
                    });
        }

        trailersList.setLayoutManager(new LinearLayoutManager(getContext()));
        trailersList.setHasFixedSize(true);
        trailerAdapter = new TrailerAdapter(getContext(), trailers, this);
        trailersList.setAdapter(trailerAdapter);

        reviewsList.setLayoutManager(new LinearLayoutManager(getContext()));
        reviewsList.setHasFixedSize(true);
        reviewAdapter = new ReviewAdapter(getContext(), reviews, this);
        reviewsList.setAdapter(reviewAdapter);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("trailers", trailers);
        outState.putParcelableArrayList("reviews", reviews);
        outState.putParcelable("movie", movie);
    }

    private void saveSortPref(String id, int fav) {
        SharedPreferences sp = getActivity().getSharedPreferences("fav_prefs", MainActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(id, fav);
        editor.apply();
    }

    private void readSortPref(String id) {
        SharedPreferences sp = getActivity().getSharedPreferences("fav_prefs", MainActivity.MODE_PRIVATE);
        fav = sp.getInt(id, 0);
    }

    private void CheckInDB() {
        readSortPref(movie.get_id());
        if (fav == 0) {
            found = false;
            fav_img.setImageResource(R.drawable.favoff);
        } else {
            found = true;
            fav_img.setImageResource(R.drawable.favon);
        }
    }

    private void ChangeStatus() {
        if (!found) {
            found = true;
            fav_img.setImageResource(R.drawable.favon);
            saveSortPref(movie.get_id(), 1);
            SaveMovie();
        } else {
            found = false;
            fav_img.setImageResource(R.drawable.favoff);
            saveSortPref(movie.get_id(), 0);
            DeleteMovie();
        }
    }

    private void SaveMovie() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.movieDao().InsertMovie(movie);
                Log.d("Movie", "Added");
            }
        });
    }

    private void DeleteMovie() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.movieDao().deleteMovie(movie);
                Log.d("Movie", "Deleted");
            }
        });
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

    @Override
    public void OnItemClickListener(int index, Trailer trailer) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailer.getKey()));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()));
        try {
            getActivity().startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            getActivity().startActivity(webIntent);
        }
    }

    @Override
    public void onItemClickListener(int index, Review review) {
        Dialog reviewDialog = new Dialog(getActivity(), R.style.DialogTheme);
        reviewDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        reviewDialog.setContentView(R.layout.dialog_review_layout);
        reviewDialog.setCancelable(true);
        TextView tv_review = reviewDialog.findViewById(R.id.dialog_text);
        tv_review.setText(review.getContent());
        reviewDialog.show();
    }


    public class FetchTrailers extends AsyncTask<String, Void, String> {


        private HttpURLConnection urlConnection = null;
        private BufferedReader reader = null;

        @Override
        protected String doInBackground(String... params) {
            String id = params[0];
            URL url;
            String trailersJson = null;
            try {
                url = new URL(createURI(id).toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

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
                trailersJson = buffer.toString();

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
            return trailersJson;
        }

        @Override
        protected void onPostExecute(String json) {
            if (json != null) {
                JSONObject TrailersJson = null;
                try {
                    TrailersJson = new JSONObject(json);
                    JSONArray trailersArray = TrailersJson.getJSONArray("results");
                    for (int i = 0; i < trailersArray.length(); i++) {
                        JSONObject trailer = trailersArray.getJSONObject(i);
                        trailers.add(new Trailer(trailer.getString("key"),
                                trailer.getString("id"),
                                trailer.getString("size"),
                                trailer.getString("type")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (trailers.size() == 0) {
                trailersHeader.setText(getContext().getResources().getString(R.string.notrailers));
            } else {
                trailerAdapter.notifyDataSetChanged();
            }
            super.onPostExecute(json);
        }

        private Uri createURI(String id) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("api.themoviedb.org")
                    .path("3/movie")
                    .appendPath(id)
                    .appendPath("videos")
                    .appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_KEY);

            Log.d("urlTrailer", builder.build().toString());
            return builder.build();
        }
    }


    public class FetchReviews extends AsyncTask<String, Void, String> {


        private HttpURLConnection urlConnection = null;
        private BufferedReader reader = null;

        @Override
        protected String doInBackground(String... params) {
            String id = params[0];
            URL url;
            String reviewsJson = null;
            try {
                url = new URL(createURI(id).toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

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
                reviewsJson = buffer.toString();

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
            return reviewsJson;
        }

        @Override
        protected void onPostExecute(String json) {
            if (json != null) {
                JSONObject ReviewsJson = null;
                try {
                    ReviewsJson = new JSONObject(json);
                    JSONArray reviewsArray = ReviewsJson.getJSONArray("results");
                    for (int i = 0; i < reviewsArray.length(); i++) {
                        JSONObject review = reviewsArray.getJSONObject(i);
                        reviews.add(new Review(review.getString("author"),
                                review.getString("content"),
                                review.getString("url"),
                                review.getString("id")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (reviews.size() == 0) {
                reviewsHeader.setText(getContext().getResources().getString(R.string.noreviews));
            } else {
                reviewAdapter.notifyDataSetChanged();
            }
            super.onPostExecute(json);
        }

        private Uri createURI(String id) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("api.themoviedb.org")
                    .path("3/movie")
                    .appendPath(id)
                    .appendPath("reviews")
                    .appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_KEY);

            Log.d("urlReview", builder.build().toString());
            return builder.build();
        }
    }
}
