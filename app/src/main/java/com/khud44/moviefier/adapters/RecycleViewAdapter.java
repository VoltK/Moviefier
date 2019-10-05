package com.khud44.moviefier.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.khud44.moviefier.ActorDetailsActivity;
import com.khud44.moviefier.MovieDetailsActivity;
import com.khud44.moviefier.R;
import com.khud44.moviefier.retrofit.models.movie.Cast;
import com.khud44.moviefier.retrofit.models.movie_lists.RetroMovie;

import java.util.List;

import static com.khud44.moviefier.utils.Constants.*;
import static com.khud44.moviefier.utils.Helpers.loadImage;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {

    private static final String TAG = RecycleViewAdapter.class.getName();

    private List<RetroMovie> movies;
    private List<Cast> actors;
    private List<com.khud44.moviefier.retrofit.models.actor.Cast> actorMovies;
    private Context context;
    private boolean flag;
    private int x;

    public RecycleViewAdapter(Context context, List<RetroMovie> movies) {
        this.movies = movies;
        this.context = context;
        flag = false;
    }

    public RecycleViewAdapter(Context context, List<com.khud44.moviefier.retrofit.models.actor.Cast> movies, int x) {
        this.actorMovies = movies;
        this.context = context;
        this.x = x;
    }

    public RecycleViewAdapter(Context context, List<Cast> actors, boolean flag){
        this.context = context;
        this.actors = actors;
        this.flag = flag;
        this.x = 0;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called");
        final String title;
        String imagePath;
        final int id;

        if(flag){
            Cast actor = actors.get(position);
            title = actor.getName();
            imagePath = actor.getProfilePath();
            id = actor.getId();
        }
        else{
            if (x == 0){
                RetroMovie retroMovie = movies.get(position);
                title = retroMovie.getTitle();
                imagePath = retroMovie.getPosterPath();
                id = retroMovie.getId();
            } else{
                com.khud44.moviefier.retrofit.models.actor.Cast actorMovie = actorMovies.get(position);
                title = actorMovie.getTitle();
                imagePath = actorMovie.getPosterPath();
                id = actorMovie.getId();
            }

        }

        loadImage(context, POSTER_BASE_PATH + imagePath, holder.progressBar, holder.posterView);

        holder.titleView.setText(title);

        holder.posterView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                Log.d(TAG, "onClick: clicked on image: " + title);
                // call intent for only movie details
                Intent intent = new Intent();
                if (!flag){
                    Log.d(TAG, "starting movie details activity");
                    // intent for details page
                    intent.setClass(context, MovieDetailsActivity.class);
                    // get movie id
                    // add key:value to intent with movie id
                    intent.putExtra(INTENT_MOVIE_ID, id);
                }
                else{
                    Log.d(TAG, "starting actor details activity");
                    intent.setClass(context, ActorDetailsActivity.class);
                    intent.putExtra(INTENT_ACTOR_ID, id);
                }
                context.startActivity(intent);

            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup viewGroup, int i) {
        Log.d(TAG, "onCreateViewHolder: called");
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if (!flag){
            if (x == 0){
                return movies.size();
            }
            return actorMovies.size();
        }
        else{
            return actors.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView posterView;
        TextView titleView;
        ProgressBar progressBar;

         public ViewHolder(View itemView){
             super(itemView);
             posterView = itemView.findViewById(R.id.item_image);
             titleView = itemView.findViewById(R.id.item_title);
             progressBar = itemView.findViewById(R.id.imageProgress);
         }
    }

}
