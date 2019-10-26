package com.khud44.moviefier;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.khud44.moviefier.adapters.RecycleViewAdapter;
import com.khud44.moviefier.data.DbViewModel;
import com.khud44.moviefier.data.models.MovieRoomItem;
import com.khud44.moviefier.retrofit.models.movie.*;
import com.khud44.moviefier.retrofit.GetData;
import com.khud44.moviefier.retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import static com.khud44.moviefier.utils.Constants.*;
import static com.khud44.moviefier.utils.Helpers.*;

public class MovieDetailsActivity extends BaseActivity implements YouTubePlayer.OnInitializedListener {

    private static final String TAG = MovieDetailsActivity.class.getName();

    private ScrollView scrollView;
    private DbViewModel dbViewModel;
    private String videoKey;
    //private String language;
    private FloatingActionButton fab;
    private boolean added;

    //private GetData service;
    private int movie_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        screenLayout = R.layout.movie_details_layout;
        internetNeeded = true;
        setContentView(screenLayout);
    }

    public void initAll(){
        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        movie_id = intent.getIntExtra(INTENT_MOVIE_ID, 0);

        scrollView = findViewById(R.id.scrollDetails);
        scrollView.setVisibility(View.GONE);
        // TODO finish db operations
        dbViewModel = ViewModelProviders.of(this).get(DbViewModel.class);

        Log.d(TAG, "MOVIE ID: " + movie_id);

        service = RetrofitClient.getRetrofitInstance().create(GetData.class);

        retrofitGetMovieDetails();
    }

    private void retrofitGetMovieDetails(){

        Call<RetroMovieDetails> call = service.getMovieDetails(movie_id, MOVIE_API_KEY, language,"credits,videos");
        call.enqueue(new Callback<RetroMovieDetails>() {
            @Override
            public void onResponse(Call<RetroMovieDetails> call, Response<RetroMovieDetails> response) {
                try {
                    Log.d(TAG, "message returned: " + response.body().getTitle() + "\nCode:" + response.code() + "\b RAW: " + response.raw());
                    showDetails(response.body());
                }
                catch (NullPointerException e){
                    Log.d(TAG, "Error in recycler view");

                }//Toast.makeText(MovieDetailsActivity.this, "Details for " + response.body().getTitle(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<RetroMovieDetails> call, Throwable t) {
                Log.d(TAG, "Failed to fetch details from API\n" + t);
            }
        });
    }

    private void showCast(Credits credits){
        setDirector(credits.getCrew());
        setRecyclerCastView(credits.getCast());
    }

    private void setDirector(List<Crew> crewList){
        TextView directorView = findViewById(R.id.directorDetails);
        ArrayList<String> directors = new ArrayList<>();

        for (Crew crew: crewList){
            if(crew.getJob().equals("Director")){
                directors.add(crew.getName());
            }
        }

        directorView.setText(TextUtils.join(",", directors));
    }

    private void setRecyclerCastView(List<Cast> castList){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        // find views
        RecyclerView recyclerView = findViewById(R.id.recyclerCastView);
        //ProgressBar progressBar = findViewById(progressId);
        recyclerView.setLayoutManager(layoutManager);
        // create adapter with fetched movies
        RecycleViewAdapter adapter = new RecycleViewAdapter(this, castList, true);
        // hide progress bar
        //progressBar.setVisibility(View.GONE);
        // set adapter for recyclerView
        recyclerView.setAdapter(adapter);
    }

    private void showDetails(RetroMovieDetails movieDetails){

        findViewById(R.id.progressDetailsMain).setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);

        handleFloatingButton(movieDetails);

        // title view
        TextView titleView = findViewById(R.id.titleDetails);
        titleView.setText(movieDetails.getTitle());

        // genres view
        TextView genreView = findViewById(R.id.genreDetails);

        ArrayList<String> genres = new ArrayList<>();

        for(Genre genre: movieDetails.getGenres()){
            genres.add(genre.getName());
        }

        genreView.setText(TextUtils.join("/", genres));

        // rating view
        TextView ratingView = findViewById(R.id.ratingDetails);
        double rating = movieDetails.getVoteAverage();
        ratingView.setText(rating > 0 ? String.valueOf(rating): "?");
        GradientDrawable ratingCircle = (GradientDrawable) ratingView.getBackground();
        ratingCircle.setColor(getRatingColor(this, rating));

        // budget view
        TextView budgetView = findViewById(R.id.budgetDetails);
        NumberFormat format = NumberFormat.getCurrencyInstance();
        int budget = movieDetails.getBudget();
        budgetView.setText( budget > 0 ? format.format(budget) : "?");

        // release date view
        TextView releasedView = findViewById(R.id.releasedDetails);
        releasedView.setText(movieDetails.getReleaseDate());

        // overview view
        TextView overviewView = findViewById(R.id.overviewDetails);
        overviewView.setText(movieDetails.getOverview());

        ImageView posterView = findViewById(R.id.posterDetails);
        final ProgressBar progressBar = findViewById(R.id.progressPosterDetails);

        loadImage(this, POSTER_DETAILS_PATH + movieDetails.getPosterPath(), progressBar, posterView);


        showCast(movieDetails.getCredits());

        final List<Result> videos = movieDetails.getVideos().getResults();

        if ( videos != null && !videos.isEmpty()){
            //initYouTube(videos.get(0).getKey());
            videoKey = videos.get(0).getKey();
            YouTubePlayerSupportFragment youtubePlayerFragment =
                    (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.youtubeTrailer);
            youtubePlayerFragment.initialize(YOUTUBE_API_KEY, this);
        }
        else{
            showMessage(MovieDetailsActivity.this, getString(R.string.trailer_error));
        }

    }

    private void handleFloatingButton(final RetroMovieDetails movieDetails){
        // button to add and delete movie from database
        fab = findViewById(R.id.fab);
        fab.show();
        added = dbViewModel.checkMovieExist(movie_id) == 1;
        changeFloatingIcon();
        // hack the bug in support library v7:28.0.0, which doesn't center icon by default
        fab.setScaleType(ImageView.ScaleType.CENTER);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // add / delete functionality
                if (added) {
                    // delete
                    dbViewModel.deleteMovie(movie_id);
                    added = false;
                }
                else{
                    // add
                    dbViewModel.insertMovie(new MovieRoomItem(movieDetails.getId(),
                                                            movieDetails.getTitle(),
                                                            movieDetails.getGenresIds(),
                                                            movieDetails.getReleaseDate(),
                            movieDetails.getPosterPath(),
                            movieDetails.getVoteAverage()));
                    added = true;
                }
                changeFloatingIcon();
            }
        });
    }

    private void changeFloatingIcon(){
        if (added){
            fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.rating3)));
            fab.setImageResource(R.drawable.ic_delete_floating_white);
        }else {
            fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.rating10)));
            fab.setImageResource(R.drawable.ic_add_floating_white);
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                        boolean wasRestored) {
        if (!wasRestored) {
            player.cueVideo(videoKey);
        }
    }

    @Override
    public void onInitializationFailure (YouTubePlayer.Provider provider, YouTubeInitializationResult error) {
            showMessage(MovieDetailsActivity.this,getString(R.string.trailer_error));
    }

}
