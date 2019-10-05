package com.khud44.moviefier;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.khud44.moviefier.adapters.CustomMovieArrayAdapter;
import com.khud44.moviefier.data.DbViewModel;
import com.khud44.moviefier.data.models.GenreRoomItem;
import com.khud44.moviefier.data.models.MovieRoomItem;
import com.khud44.moviefier.retrofit.models.movie_lists.RetroMovie;
import com.khud44.moviefier.retrofit.models.movie_lists.RetroMovieResults;
import com.khud44.moviefier.retrofit.models.movie_lists.RetroUpcomingResults;
import com.khud44.moviefier.retrofit.GetData;
import com.khud44.moviefier.retrofit.RetrofitClient;
import com.khud44.moviefier.utils.ThisWeek;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

import static com.khud44.moviefier.utils.Constants.*;
import static com.khud44.moviefier.utils.Helpers.showMessage;

public class MovieListActivity extends AppCompatActivity {

    private static final String TAG = MovieListActivity.class.getName();

    private ListView listView;
    private CustomMovieArrayAdapter adapter;
    private String location;
    private String language;

    private GetData service;

    private DbViewModel dbViewModel;

    private int page;
    private int total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_list_layout);

        dbViewModel = ViewModelProviders.of(this).get(DbViewModel.class);
        List<GenreRoomItem> genreRoomItems = dbViewModel.getAllGenres();

        Intent intent = getIntent();
        location = intent.getStringExtra(INTENT_LOCATION);
        language = intent.getStringExtra(INTENT_LANGUAGE);

        listView = findViewById(R.id.movie_list);
        // init empty adapter
        adapter = new CustomMovieArrayAdapter(new ArrayList<RetroMovie>(), this, dbViewModel, genreRoomItems);
        // set adapter
        listView.setAdapter(adapter);
        // set view for empty list
        listView.setEmptyView(findViewById(R.id.emptyView));
        // hide empty view while fetching data
        listView.getEmptyView().setVisibility(View.GONE);

        TextView activityTitle = findViewById(R.id.movieCategory);
        activityTitle.setText(intent.getStringExtra(INTENT_ACTIVITY_TITLE));

        if (intent.hasExtra(INTENT_STORAGE)){
            getDataFromStorage();
        }
        else {
            getDataFromApi(intent);
        }

    }

    private void checkEmptyView(){
        // show empty view if list is empty
        if(listView.getCount() == 0) {
            listView.getEmptyView().setVisibility(View.VISIBLE);
        }
    }

    private void getDataFromStorage(){
        // items from db
        adapter.setDbItems(true);
        dbViewModel.getAllWords().observe(this, new Observer<List<MovieRoomItem>>() {
            @Override
            public void onChanged(@Nullable final List<MovieRoomItem> movieRoomItems) {
                //List<RetroMovie> movies = new ArrayList<>();
                for(MovieRoomItem item: movieRoomItems){
                    //movies.add(new RetroMovie());
                    RetroMovie retroMovie = new RetroMovie();
                    retroMovie.setId(item.getMovie_id());
                    retroMovie.setPosterPath(item.getPosterPath());
                    retroMovie.setTitle(item.getTitle());
                    retroMovie.setVoteAverage(item.getRating());
                    retroMovie.setReleaseDate(item.getReleaseDate());
                    List<Integer> genre = new ArrayList<>(item.getGenre());
                    retroMovie.setGenreIds(genre);
                    adapter.add(retroMovie);
                }
            }
        });
        checkEmptyView();
    }

    private void getDataFromApi(Intent intent){

            String api = intent.getStringExtra(INTENT_LIST_ACTIVITY);
            String movieTitle = intent.hasExtra(MOVIE_TITLE) ? intent.getStringExtra(MOVIE_TITLE) : "";

            service = RetrofitClient.getRetrofitInstance().create(GetData.class);
            String apiUrl;
            // page to start
            page = 1;
            total = 2;

            while (page < MAX_PAGES && page <= total) {


                if (api.equals(LIST_ACTIVITY_SEARCH)){
                    retrofitSearchMovies(movieTitle);
                }
                else {
                    apiUrl = getApiUrl(api, page);

                    if (api.equals(LIST_ACTIVITY_UPCOMING)) {
                        retrofitGetUpcomingMovies(apiUrl);
                    } else{
                        retrofitGetMovies(apiUrl);
                    }
                }
                Log.d(TAG, "MADE A CALL TO PAGE #" + page);
                Log.d(TAG, "TOTAL PAGES" + total);

                page++;
            }
    }

    private void retrofitGetUpcomingMovies(String apiUrl){
        Call<RetroUpcomingResults> call = service.getUpcomingMovies(apiUrl);

        call.enqueue(new Callback<RetroUpcomingResults>() {

            @Override
            public void onResponse(Call<RetroUpcomingResults> call, Response<RetroUpcomingResults> response) {
                RetroUpcomingResults results = response.body();
                successfulResponse(results.getResults(), results.getTotalPages());
            }

            @Override
            public void onFailure(Call<RetroUpcomingResults> call, Throwable t) {
                Log.e(TAG, "Error making a call; page - " + page +  "\n" + t);
                showMessage(MovieListActivity.this, "Failed to fetch upcoming movies");
            }
        });
    }

    private void retrofitGetMovies(String apiUrl){
        Call<RetroMovieResults> call = service.getMovies(apiUrl);

        call.enqueue(new Callback<RetroMovieResults>() {
            @Override
            public void onResponse(Call<RetroMovieResults> call, Response<RetroMovieResults> response) {
                RetroMovieResults results = response.body();
                successfulResponse(results.getResults(), results.getTotalPages());
            }

            @Override
            public void onFailure(Call<RetroMovieResults> call, Throwable t) {
                Log.e(TAG, "Error making a call; page - " + page +  "\n" + t);
            }
    });
    }

    private void retrofitSearchMovies(String movieTitle){
        Call<RetroMovieResults> call = service.getSearchedMovies(MOVIE_API_KEY, movieTitle, language, page);

        call.enqueue(new Callback<RetroMovieResults>() {
            @Override
            public void onResponse(Call<RetroMovieResults> call, Response<RetroMovieResults> response) {
                RetroMovieResults results = response.body();
                successfulResponse(results.getResults(), results.getTotalPages());
            }

            @Override
            public void onFailure(Call<RetroMovieResults> call, Throwable t) {
                Log.e(TAG, "Error searching for a movie " + "\n" + t);
            }
        });
    }

    private String getApiUrl(String api, int page){
        ThisWeek thisWeek = new ThisWeek();
        String result;
        switch(api){
            case LIST_ACTIVITY_UPCOMING:
                result = String.format(UPCOMING_MOVIE_API,
                        // api key
                        MOVIE_API_KEY,
                        // language
                        language,
                        // page
                        page,
                        // country (region)
                        location);
                break;
            case LIST_ACTIVITY_WEEK:
                result = String.format(WEEK_RELEASE_MOVIE_API, MOVIE_API_KEY,
                        language,
                        location,
                        thisWeek.getFirstDayOfWeek(),
                        thisWeek.getLastDayOfWeek(),
                        page);
                break;

            case LIST_ACTIVITY_TOP:
                result = String.format(TOP_RATED_MOVIE_API, MOVIE_API_KEY, language, page, location);
                break;

            default:
                result = "";
                break;
        }
        return result;
    }

    private void successfulResponse(List<RetroMovie> movies, int totalPages){
        if( page == 1){
            total = totalPages;
        }
        adapter.addAll(movies);
        checkEmptyView();
    }

}
