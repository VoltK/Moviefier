package com.khud44.moviefier;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.khud44.moviefier.adapters.RecycleViewAdapter;
import com.khud44.moviefier.data.DbViewModel;
import com.khud44.moviefier.data.models.GenreRoomItem;
import com.khud44.moviefier.retrofit.models.RetroLocation;
import com.khud44.moviefier.retrofit.models.movie.AllGenres;
import com.khud44.moviefier.retrofit.models.movie.Genre;
import com.khud44.moviefier.retrofit.models.movie_lists.RetroMovie;
import com.khud44.moviefier.retrofit.models.movie_lists.RetroUpcomingResults;
import com.khud44.moviefier.retrofit.models.movie_lists.RetroMovieResults;
import com.khud44.moviefier.retrofit.GetData;
import com.khud44.moviefier.retrofit.RetrofitClient;
import com.khud44.moviefier.utils.ThisWeek;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;
import java.util.Locale;

import static com.khud44.moviefier.utils.Constants.*;
import static com.khud44.moviefier.utils.Helpers.showMessage;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    // Retrofit service
    private GetData service;

    //private List<Movie> movies;
    // DEFAULT location - US
    private String location;
    private TextView locationView;
    private String language;
    private TextInputLayout movieSearchInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationView = findViewById(R.id.locationView);
        language = Locale.getDefault().toString();

        if (isNetworkAvailable()){
            initAllViews();
            saveGenres();
        }
        else{
            noInternetHandle();
        }

    }

    private void saveGenres(){
        final DbViewModel dbViewModel = ViewModelProviders.of(this).get(DbViewModel.class);
        if(dbViewModel.getAllGenres().size() == 0){
            Call<AllGenres> call = service.getAllGenres(MOVIE_API_KEY, language);
            call.enqueue(new Callback<AllGenres>() {
                @Override
                public void onResponse(Call<AllGenres> call, Response<AllGenres> response) {
                    for(Genre genre: response.body().getGenres()) {
                        GenreRoomItem genreRoomItem = new GenreRoomItem(genre.getId(), genre.getName());
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

    private void initAllViews(){

        location = DEFAULT_LOCATION;
        movieSearchInput = findViewById(R.id.movie_title_search);

        // init RetroFit client
        service = RetrofitClient.getRetrofitInstance().create(GetData.class);
        // get location from API using Retrofit client
        retrofitGetLocation();

        retrofitGetUpcomingMovies();

        ThisWeek thisWeek = new ThisWeek();
        Log.d(TAG, "Start Week: " + thisWeek.getFirstDayOfWeek() + "\nEnd: " + thisWeek.getLastDayOfWeek());

        String weekReleaseUrl = String.format(WEEK_RELEASE_MOVIE_API, MOVIE_API_KEY,
                language,
                location,
                thisWeek.getFirstDayOfWeek(),
                thisWeek.getLastDayOfWeek(),
                1);

        retrofitGetMovies(weekReleaseUrl, R.id.recycleViewWeekRelease, R.id.progressWeek);

        // api_key, language, page #, country (region)
        String topRatedUrl = String.format(TOP_RATED_MOVIE_API, MOVIE_API_KEY, language, 1, location);
        retrofitGetMovies(topRatedUrl, R.id.recyclerViewTopRated, R.id.progressTop);
    }

    private void noInternetHandle(){
        // TODO handle no Internet connection ( Create a view etc)
        // hide scrollview
        findViewById(R.id.scrollView).setVisibility(View.GONE);
        // show no conn info
        findViewById(R.id.noConnection).setVisibility(View.VISIBLE);

        Log.d(TAG, " NO INTERNET BUDDY !!! ");
    }

    public void restartInit(View view){
        if(isNetworkAvailable()){
            findViewById(R.id.noConnection).setVisibility(View.GONE);
            findViewById(R.id.scrollView).setVisibility(View.VISIBLE);
            initAllViews();
        } else{
            showMessage(MainActivity.this,"Failed to reconnect");
        }
    }

    private void retrofitGetMovies(String apiUrl, final int recycleViewId, final int progressBarId){

        Call<RetroMovieResults> call = service.getMovies(apiUrl);

        call.enqueue(new Callback<RetroMovieResults>() {
            @Override
            public void onResponse(Call<RetroMovieResults> call, Response<RetroMovieResults> response) {
                List<RetroMovie> movies = response.body().getResults();
                for (RetroMovie movie: movies){
                    Log.d(TAG, "This week movie title: " + movie.getTitle());
                }

                initRecyclerView(movies, recycleViewId, progressBarId);
                //initRecyclerView(movies, R.id.recycleViewWeekRelease);
            }

            @Override
            public void onFailure(Call<RetroMovieResults> call, Throwable t) {
                Log.d(TAG, "Failed to fetch week releases: " + t);
            }
        });
    }

    private void retrofitGetUpcomingMovies(){
        Call<RetroUpcomingResults> call = service.getUpcomingMovies(String.format(UPCOMING_MOVIE_API,
                // api key
                MOVIE_API_KEY,
                // language
                language,
                // page
                1,
                // country (region)
                location));

        call.enqueue(new Callback<RetroUpcomingResults>() {

            @Override
            public void onResponse(Call<RetroUpcomingResults> call, Response<RetroUpcomingResults> response) {
                initRecyclerView(response.body().getResults(), R.id.recycleViewUpcoming, R.id.progressUpcoming);
            }

            @Override
            public void onFailure(Call<RetroUpcomingResults> call, Throwable t) {
                Log.e(TAG, "Error making a call " + t);
                showMessage(MainActivity.this,"Failed to fetch upcoming movies");
            }
        });

    }

    private void retrofitGetLocation(){
        Call<RetroLocation> call = service.getLocation(LOCATION_API + "/json");
        call.enqueue(new Callback<RetroLocation>() {
            @Override
            public void onResponse(Call<RetroLocation> call, Response<RetroLocation> response) {
                //Log.d(TAG, "RESPONSE CODE: " + response.code() + "\n" + response.toString());
                loadLocation(response.body());
            }

            @Override
            public void onFailure(Call<RetroLocation> call, Throwable t) {
                //Log.e(TAG, "RESPONSE CODE: " + call. + "\n" + response.toString());
                Log.e(TAG, "Error making a call " + t);
                showMessage(MainActivity.this, "Unable to find your country");
            }
        });
    }

    private void loadLocation(RetroLocation retroLocation){
        location = retroLocation.getLocation();
        locationView.setText(location);
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public void onClickViewAll(View view){

        Log.d(TAG, "showAllUpcoming: starting new activity " + MovieListActivity.class.getName());
        // intent for details page
        Intent intent = new Intent(this, MovieListActivity.class);

        switch(view.getId()){
            case R.id.viewAllUpcoming:
                // add key:value to intent
                intent.putExtra(INTENT_ACTIVITY_TITLE, UPCOMING_TITLE);

                intent.putExtra(INTENT_LIST_ACTIVITY, LIST_ACTIVITY_UPCOMING);
                break;
            case R.id.viewAllWeek:
                intent.putExtra(INTENT_ACTIVITY_TITLE, WEEK_TITLE);

                intent.putExtra(INTENT_LIST_ACTIVITY, LIST_ACTIVITY_WEEK);
                break;
            case R.id.viewAllTop:
                intent.putExtra(INTENT_ACTIVITY_TITLE, TOP_TITLE);

                intent.putExtra(INTENT_LIST_ACTIVITY, LIST_ACTIVITY_TOP);
                break;
                default:
                    break;
        }
        intent.putExtra(INTENT_LOCATION, location);
        intent.putExtra(INTENT_LANGUAGE, language);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.saved:
                showSavedMovies();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSavedMovies(){
        Intent intent = new Intent(this, MovieListActivity.class);
        intent.putExtra(INTENT_ACTIVITY_TITLE, SAVED_TITLE);
        intent.putExtra(INTENT_STORAGE, 0);
        intent.putExtra(INTENT_LOCATION, location);
        intent.putExtra(INTENT_LANGUAGE, language);
        startActivity(intent);
    }

    private boolean validateMovieInput(String movieTitle){
        if(movieTitle.isEmpty()){
            movieSearchInput.setError("Field cannot be empty");
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
            intent.putExtra(INTENT_ACTIVITY_TITLE, SEARCH_ACTIVITY_TITLE + " \"" + movieTitle + "\"");
            intent.putExtra(INTENT_LIST_ACTIVITY, LIST_ACTIVITY_SEARCH);
            intent.putExtra(MOVIE_TITLE, movieTitle);
            intent.putExtra(INTENT_LOCATION, location);
            intent.putExtra(INTENT_LANGUAGE, language);
            startActivity(intent);
        }
    }

}