package com.khud44.moviefier;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import com.khud44.moviefier.adapters.RecycleViewAdapter;
import com.khud44.moviefier.retrofit.models.actor.Cast;
import com.khud44.moviefier.retrofit.models.actor.RetroActorDetails;
import com.khud44.moviefier.retrofit.GetData;
import com.khud44.moviefier.retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

import static com.khud44.moviefier.utils.Constants.*;
import static com.khud44.moviefier.utils.Helpers.loadImage;

public class ActorDetailsActivity extends BaseActivity {

        private static final String TAG = ActorDetailsActivity.class.getName();

        private ScrollView scrollView;
        private int actor_id;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            screenLayout = R.layout.actor_details_layout;
            setContentView(screenLayout);
        }

        public void initAll(){
            // Get the Intent that started this activity and extract the string
            Intent intent = getIntent();
            actor_id = intent.getIntExtra(INTENT_ACTOR_ID, 0);

            scrollView = findViewById(R.id.actorScrollDetails);
            scrollView.setVisibility(View.GONE);

            Log.d(TAG, "ACTOR ID: " + actor_id);
            service = RetrofitClient.getRetrofitInstance().create(GetData.class);
            retrofitGetActorDetails();
        }

        private void retrofitGetActorDetails(){
            Call<RetroActorDetails> call = service.getActorDetails(actor_id, MOVIE_API_KEY, language, "movie_credits");
            call.enqueue(new Callback<RetroActorDetails>() {
                @Override
                public void onResponse(Call<RetroActorDetails> call, Response<RetroActorDetails> response) {
                    Log.d(TAG, "message returned: " + response.body().getName() + "\nCode:" + response.code() + "\b RAW: " + response.raw());
                    //Toast.makeText(MovieDetailsActivity.this, "Details for " + response.body().getTitle(), Toast.LENGTH_SHORT).show();
                    showDetails(response.body());
                }

                @Override
                public void onFailure(Call<RetroActorDetails> call, Throwable t) {
                    Log.d(TAG, "Failed to fetch details from API\n" + t);
                }
            });
        }

        private void showDetails(RetroActorDetails actorDetails){
            findViewById(R.id.progressActorDetails).setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);

            TextView nameView = findViewById(R.id.actorDetailsName);
            nameView.setText(actorDetails.getName());

            TextView yearView = findViewById(R.id.actorDetailsYears);
            String death = actorDetails.getDeathday();
            String years = String.format("(%s - %s)", actorDetails.getBirthday(), death != null ? death: getString(R.string.present));
            yearView.setText(years);

            TextView bioView = findViewById(R.id.actorDetailsBio);
            bioView.setText(actorDetails.getBiography());


            ImageView posterView = findViewById(R.id.actorDetailsPhoto);
            final ProgressBar progressBar = findViewById(R.id.progressActorDetailsPhoto);

            loadImage(this, POSTER_DETAILS_PATH + actorDetails.getProfilePath(), progressBar, posterView);

            setRecyclerCastView(actorDetails.getMovieCredits().getCast());

        }

    private void setRecyclerCastView(List<Cast> castList){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        // find views
        RecyclerView recyclerView = findViewById(R.id.actorDetailsMovies);
        //ProgressBar progressBar = findViewById(progressId);
        recyclerView.setLayoutManager(layoutManager);
        // create adapter with fetched movies
        RecycleViewAdapter adapter = new RecycleViewAdapter(this, castList, 1);
        // hide progress bar
        //progressBar.setVisibility(View.GONE);
        // set adapter for recyclerView
        recyclerView.setAdapter(adapter);
    }

}
