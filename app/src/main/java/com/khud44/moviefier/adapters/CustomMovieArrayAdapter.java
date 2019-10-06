package com.khud44.moviefier.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.khud44.moviefier.MovieDetailsActivity;
import com.khud44.moviefier.R;
import com.khud44.moviefier.data.DbViewModel;
import com.khud44.moviefier.data.models.GenreRoomItem;
import com.khud44.moviefier.data.models.MovieRoomItem;
import com.khud44.moviefier.retrofit.models.movie_lists.RetroMovie;

import java.util.ArrayList;
import java.util.List;

import static com.khud44.moviefier.utils.Constants.*;
import static com.khud44.moviefier.utils.Helpers.*;

public class CustomMovieArrayAdapter extends ArrayAdapter<RetroMovie>{

    private ArrayList<RetroMovie> dataSet;
    Context mContext;
    DbViewModel viewModel;
    List<GenreRoomItem> genreRoomItemList;
    boolean dbItems;

    // View lookup cache
    private static class ViewHolder {
        TextView title;
        TextView genre;
        TextView releaseDate;
        TextView rating;
        ImageView poster;
        ProgressBar progressBar;
        Button info;
        Button add;
        Button delete;
    }

    public CustomMovieArrayAdapter(ArrayList<RetroMovie> movies, Context context, DbViewModel viewModel, List<GenreRoomItem> genreRoomItemList) {
        super(context, R.layout.list_item_layout, movies);
        this.dataSet = movies;
        this.mContext=context;
        this.viewModel = viewModel;
        this.genreRoomItemList = genreRoomItemList;
        this.dbItems = false;
    }

    public void setDbItems(boolean dbItems){
        this.dbItems = dbItems;
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final RetroMovie movie = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag

        View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_layout, parent, false);
            viewHolder.title =  convertView.findViewById(R.id.movie_item_title);
            viewHolder.genre =  convertView.findViewById(R.id.movie_item_genre);
            viewHolder.releaseDate = convertView.findViewById(R.id.movie_item_date);
            viewHolder.rating =  convertView.findViewById(R.id.movie_item_rating);
            viewHolder.poster = convertView.findViewById(R.id.movie_item_poster);
            viewHolder.progressBar = convertView.findViewById(R.id.movie_item_progress);
            viewHolder.info = convertView.findViewById(R.id.movie_item_info);
            viewHolder.add = convertView.findViewById(R.id.movie_item_add);
            viewHolder.delete = convertView.findViewById(R.id.movie_item_delete);

            result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.title.setText(movie.getTitle());

        String genre = makeGenre(movie.getGenreIds());
        viewHolder.genre.setText(genre != null ? genre: "Unknown Genre");

        viewHolder.releaseDate.setText(movie.getReleaseDate());

        double rating = movie.getVoteAverage();
        viewHolder.rating.setText(rating > 0 ? String.valueOf(rating): "?");
        GradientDrawable ratingCircle = (GradientDrawable) viewHolder.rating.getBackground();
        ratingCircle.setColor(getRatingColor(getContext(), rating));

        // check if move in db -> disable add button, show delete button
        if(viewModel.checkMovieExist(movie.getId()) == 1){
            checkAddButton(viewHolder, 0);
        } else{
            checkAddButton(viewHolder, 1);
        }

        //viewHolder.delete.setTag(position);
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deleteMovie(movie, viewHolder);
            }
        });

        //viewHolder.add.setTag(position);
        viewHolder.add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //int i = (Integer) v.getTag();
                saveMovie(movie, viewHolder);
            }
        });

        //viewHolder.info.setTag(position);
        viewHolder.info.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //int i = (Integer) v.getTag();
                showDetails(movie);
            }
        });

        loadImage(mContext, POSTER_BASE_PATH + movie.getPosterPath(), viewHolder.progressBar, viewHolder.poster);
        // Return the completed view to render on screen
        return convertView;
    }

    private void showDetails(RetroMovie movie){
        int movie_id = movie.getId();
        Intent intent = new Intent(mContext, MovieDetailsActivity.class);
        // get movie id
        // add key:value to intent with movie id
        intent.putExtra(INTENT_MOVIE_ID, movie_id);
        // start movie details activity
        mContext.startActivity(intent);
    }

    private void deleteMovie(RetroMovie movie, ViewHolder viewHolder){
        //RetroMovie movie = getItem(i);
        viewModel.deleteMovie(movie.getId());
        // delete from arraylist
        if (dbItems){
            remove(movie);
            notifyDataSetChanged();
            lastPosition--;
        }
        checkAddButton(viewHolder, 1);
        showMessage(mContext, movie.getTitle() + " was deleted from your list");
    }

    private void saveMovie(RetroMovie movie, ViewHolder viewHolder){
        Log.e("TEST_DB_ADD", "TRYING TO ADD ITEM TO DATABASE");
        //RetroMovie movie = getItem(i);
        String title = movie.getTitle();
        //DbViewModel viewModel = ViewModelProviders.of(getContext()).get(DbViewModel.class);
        MovieRoomItem movieRoomItem = new MovieRoomItem(movie.getId(),
                title,
                movie.getGenreIds(),
                movie.getReleaseDate(),
                movie.getPosterPath(),
                movie.getVoteAverage());
        // insert new field into database
        viewModel.insertMovie(movieRoomItem);
        checkAddButton(viewHolder, 0);
        //deleteView.setVisibility(View.VISIBLE);
        showMessage(mContext, title + " was saved to watch later");
    }

    private void checkAddButton(ViewHolder viewHolder, int i){
        if (i == 0){
            viewHolder.add.setEnabled(false);
            // change drawable to checked
            viewHolder.add.setBackgroundResource(R.drawable.ic_checked);
            // show delete button
            viewHolder.delete.setVisibility(View.VISIBLE);
        } else{
            viewHolder.add.setEnabled(true);
            // change drawable to add
            viewHolder.add.setBackgroundResource(R.drawable.ic_add_circle_black_24dp);
            // hide delete button
            viewHolder.delete.setVisibility(View.INVISIBLE);
        }
    }

    private String makeGenre(List<Integer> genreIds){
        ArrayList<String> genresStrings = new ArrayList<>();
        for(Integer genreId: genreIds){
            for(GenreRoomItem genreRoomItem: genreRoomItemList){
                if (genreId == genreRoomItem.getGenre_id()){
                    genresStrings.add(genreRoomItem.getName());
                }
            }
        }
        return TextUtils.join("/", genresStrings);
    }

}
