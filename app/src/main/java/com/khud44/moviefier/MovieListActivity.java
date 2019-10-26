package com.khud44.moviefier;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

public class MovieListActivity extends BaseActivity {

    private static final String TAG = MovieListActivity.class.getName();

    private ListView listView;
    private CustomMovieArrayAdapter adapter;

    private DbViewModel dbViewModel;
    private Intent intent;

    private int page;
    private int total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        screenLayout = R.layout.movie_list_layout;
        intent = getIntent();
        // if we use storage -> no internet check
        internetNeeded = !intent.hasExtra(INTENT_STORAGE);
        setContentView(screenLayout);
    }

    public void initAll(){
        dbViewModel = ViewModelProviders.of(this).get(DbViewModel.class);
        List<GenreRoomItem> genreRoomItems = dbViewModel.getAllGenres(language);

        //Intent intent =

        listView = findViewById(R.id.movie_list);
        // init empty adapter
        adapter = new CustomMovieArrayAdapter(new ArrayList<RetroMovie>(), this, dbViewModel, genreRoomItems);
        // set adapter
        listView.setAdapter(adapter);
        // set view for empty list
        listView.setEmptyView(findViewById(R.id.emptyView));
        // hide empty view while fetching data
        listView.getEmptyView().setVisibility(View.GONE);

        setListTitle();

        if (intent.hasExtra(INTENT_STORAGE)){
            getDataFromStorage();
        }
        else {
            getDataFromApi();
        }
    }

    private void setListTitle(){
        TextView activityTitle = findViewById(R.id.movieCategory);
        String title = "";
        switch (intent.getStringExtra(INTENT_LIST_ACTIVITY)){
            case LIST_ACTIVITY_SAVED:
                title = getString(R.string.saved_movies);
                break;
            case LIST_ACTIVITY_UPCOMING:
                title = getString(R.string.upcoming);
                break;
            case LIST_ACTIVITY_WEEK:
                title = getString(R.string.week_release);
                break;
            case LIST_ACTIVITY_TOP:
                title = getString(R.string.top);
                break;
            case LIST_ACTIVITY_SEARCH:
                title = getString(R.string.search_result) + " \"" + intent.getStringExtra(MOVIE_TITLE) + "\"";

            default:
                    break;
        }

        activityTitle.setText(title);
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
                    // don't add it again if already in adapter
                    if(!existInAdapter(item)){
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
            }
        });
        checkEmptyView();
    }

    private boolean existInAdapter(MovieRoomItem movieRoomItem){
        for (int i = 0; i < adapter.getCount(); i++){
            // if ids are equal -> movie is in adapter
            if (adapter.getItem(i).getId() == movieRoomItem.getMovie_id()){
                return true;
            }
        }
        return false;
    }

    private void getDataFromApi(){

            String api = intent.getStringExtra(INTENT_LIST_ACTIVITY);
            String movieTitle = intent.hasExtra(MOVIE_TITLE) ? intent.getStringExtra(MOVIE_TITLE) : "";

            service = RetrofitClient.getRetrofitInstance().create(GetData.class);
            // page to start
            page = 1;
            total = 2;

            while (page < MAX_PAGES && page <= total) {


                if (api.equals(LIST_ACTIVITY_SEARCH)){
                    retrofitSearchMovies(movieTitle);
                }
                else {

                    if (api.equals(LIST_ACTIVITY_UPCOMING)) {
                        retrofitGetUpcomingMovies();
                    } else{
                        retrofitGetMovies(api);
                    }
                }
                Log.d(TAG, "MADE A CALL TO PAGE #" + page);
                Log.d(TAG, "TOTAL PAGES" + total);

                page++;
            }
    }

    private void retrofitGetUpcomingMovies(){
        Call<RetroUpcomingResults> call = service.getUpcomingMovies(MOVIE_API_KEY, language,location,page);

        call.enqueue(new Callback<RetroUpcomingResults>() {

            @Override
            public void onResponse(Call<RetroUpcomingResults> call, Response<RetroUpcomingResults> response) {
                RetroUpcomingResults results = response.body();
                successfulResponse(results.getResults(), results.getTotalPages());
            }

            @Override
            public void onFailure(Call<RetroUpcomingResults> call, Throwable t) {
                Log.e(TAG, "Error making a call; page - " + page +  "\n" + t);
                showMessage(MovieListActivity.this, getString(R.string.fetch_upcoming_error));
            }
        });
    }

    private void retrofitGetMovies(String api){
        Call<RetroMovieResults> call;
        if (api.equals(LIST_ACTIVITY_WEEK)){
            ThisWeek thisWeek = ThisWeek.getInstance();
            call = service.getWeekReleases(MOVIE_API_KEY,
                    language,
                    location,
                    thisWeek.getFirstDayOfWeek(),
                    thisWeek.getLastDayOfWeek(),
                    "2|3",
                    page);
        }
        else {
            call = service.getTopRated(MOVIE_API_KEY,
                    language,
                    location,
                    page);
        }

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

    private void successfulResponse(List<RetroMovie> movies, int totalPages){
        if( page == 1){
            total = totalPages;
        }
        adapter.addAll(movies);
        checkEmptyView();
    }

}
