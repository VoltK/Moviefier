package com.khud44.moviefier;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.khud44.moviefier.adapters.RecycleViewAdapter;
import com.khud44.moviefier.data.DbViewModel;
import com.khud44.moviefier.data.models.GenreRoomItem;
import com.khud44.moviefier.retrofit.models.movie.AllGenres;
import com.khud44.moviefier.retrofit.models.movie.Genre;
import com.khud44.moviefier.retrofit.models.movie_lists.RetroMovie;
import com.khud44.moviefier.retrofit.models.movie_lists.RetroUpcomingResults;
import com.khud44.moviefier.retrofit.models.movie_lists.RetroMovieResults;
import com.khud44.moviefier.retrofit.GetData;
import com.khud44.moviefier.retrofit.RetrofitClient;
import com.khud44.moviefier.utils.MyContextWrapper;
import com.khud44.moviefier.utils.ThisWeek;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

import static com.khud44.moviefier.utils.Constants.*;
import static com.khud44.moviefier.utils.Helpers.showMessage;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getName();

    private TextView locationView;
    private TextInputLayout movieSearchInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        screenLayout = R.layout.activity_main;
        internetNeeded = true;
        setContentView(screenLayout);
    }

    private void saveGenres(){
        final DbViewModel dbViewModel = ViewModelProviders.of(this).get(DbViewModel.class);
        if(dbViewModel.getAllGenres(language).size() == 0){
                Call<AllGenres> call = service.getAllGenres(MOVIE_API_KEY, language);
                call.enqueue(new Callback<AllGenres>() {
                    @Override
                    public void onResponse(Call<AllGenres> call, Response<AllGenres> response) {
                        for(Genre genre: response.body().getGenres()) {
                            GenreRoomItem genreRoomItem = new GenreRoomItem(genre.getId(), genre.getName(), language);
                            dbViewModel.insertGenre(genreRoomItem);
                            Log.d(TAG, "GENRE: " + genre.getName() + " was add to db");
                        }
                        Log.d(TAG, "ALL GENRES SAVED TO DATABASE");
                    }

                    @Override
                    public void onFailure(Call<AllGenres> call, Throwable t) {
                        Log.d(TAG, "Failed to fetch week releases: " + t);
                    }
                });
        }
        else{
            Log.d(TAG, "NO GENRES TO SAVE ???");
        }
    }

    public void initAll(){
        service = RetrofitClient.getRetrofitInstance().create(GetData.class);
        saveGenres();

        locationView = findViewById(R.id.locationView);
        locationView.setText(location);

        movieSearchInput = findViewById(R.id.movie_title_search);

        retrofitGetUpcomingMovies();

        retrofitGetMovies(LIST_ACTIVITY_WEEK, R.id.recycleViewWeekRelease, R.id.progressWeek);

        retrofitGetMovies(LIST_ACTIVITY_TOP, R.id.recyclerViewTopRated, R.id.progressTop);

    }

    private void retrofitGetMovies(String type, final int recycleViewId, final int progressBarId){
        Call<RetroMovieResults> call;
        if (type.equals(LIST_ACTIVITY_WEEK)){
            ThisWeek thisWeek = ThisWeek.getInstance();
            call = service.getWeekReleases(MOVIE_API_KEY,
                    language,
                    location,
                    thisWeek.getFirstDayOfWeek(),
                    thisWeek.getLastDayOfWeek(),
                    "2|3",
                    1);
        }
        else {
            call = service.getTopRated(MOVIE_API_KEY,
                    language,
                    location,
                    1);
        }

        call.enqueue(new Callback<RetroMovieResults>() {
            @Override
            public void onResponse(Call<RetroMovieResults> call, Response<RetroMovieResults> response) {
                List<RetroMovie> movies = response.body().getResults();
                for (RetroMovie movie: movies){
                    Log.d(TAG, "This week movie title: " + movie.getTitle());
                }

                initRecyclerView(movies, recycleViewId, progressBarId);
            }

            @Override
            public void onFailure(Call<RetroMovieResults> call, Throwable t) {
                Log.d(TAG, "Failed to fetch week releases: " + t);
            }
        });
    }

    private void retrofitGetUpcomingMovies(){
        Call<RetroUpcomingResults> call = service.getUpcomingMovies(MOVIE_API_KEY, language,location, 1);

        call.enqueue(new Callback<RetroUpcomingResults>() {

            @Override
            public void onResponse(Call<RetroUpcomingResults> call, Response<RetroUpcomingResults> response) {
                initRecyclerView(response.body().getResults(), R.id.recycleViewUpcoming, R.id.progressUpcoming);
            }

            @Override
            public void onFailure(Call<RetroUpcomingResults> call, Throwable t) {
                Log.e(TAG, "Error making a call " + t);
                showMessage(MainActivity.this,getString(R.string.fetch_upcoming_error));
            }
        });

    }

    private void initRecyclerView(List<RetroMovie> movies, int viewId, int progressId){
        Log.d(TAG, "initRecyclerView: init recyclerview");
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        // find views
        RecyclerView recyclerView = findViewById(viewId);
        ProgressBar progressBar = findViewById(progressId);
        recyclerView.setLayoutManager(layoutManager);
        // create adapter with fetched movies
        RecycleViewAdapter adapter = new RecycleViewAdapter(this, movies);
        // hide progress bar
        progressBar.setVisibility(View.GONE);
        // set adapter for recyclerView
        recyclerView.setAdapter(adapter);

    }

    public void onClickViewAll(View view){

        Log.d(TAG, "showAllUpcoming: starting new activity " + MovieListActivity.class.getName());
        // intent for details page
        Intent intent = new Intent(this, MovieListActivity.class);

        switch(view.getId()){
            case R.id.viewAllUpcoming:
                // add key:value to intent
                ///intent.putExtra(INTENT_ACTIVITY_TITLE, getString(R.string.upcoming));

                intent.putExtra(INTENT_LIST_ACTIVITY, LIST_ACTIVITY_UPCOMING);
                break;
            case R.id.viewAllWeek:
                //intent.putExtra(INTENT_ACTIVITY_TITLE, getString(R.string.week_release));

                intent.putExtra(INTENT_LIST_ACTIVITY, LIST_ACTIVITY_WEEK);
                break;
            case R.id.viewAllTop:
                //intent.putExtra(INTENT_ACTIVITY_TITLE, getString(R.string.top));

                intent.putExtra(INTENT_LIST_ACTIVITY, LIST_ACTIVITY_TOP);
                break;
                default:
                    break;
        }
        startActivity(intent);
    }

    private boolean validateMovieInput(String movieTitle){
        if(movieTitle.isEmpty()){
            movieSearchInput.setError(getString(R.string.empty_field));
            return false;
        } else {
            movieSearchInput.setError(null);
            return true;
        }
    }

    public void startSearch(View v){
        String movieTitle = movieSearchInput.getEditText().getText().toString().trim();
        if(validateMovieInput(movieTitle)){
            Intent intent = new Intent(this, MovieListActivity.class);
            //intent.putExtra(INTENT_ACTIVITY_TITLE,  " \"" + movieTitle + "\"");
            intent.putExtra(INTENT_LIST_ACTIVITY, LIST_ACTIVITY_SEARCH);
            intent.putExtra(MOVIE_TITLE, movieTitle);
            startActivity(intent);
        }
    }

}