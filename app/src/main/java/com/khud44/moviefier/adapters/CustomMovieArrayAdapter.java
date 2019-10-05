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
        RetroMovie movie = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

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

        viewHolder.delete.setTag(position);
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int i = (Integer) v.getTag();
                deleteMovie(i, viewHolder.add, viewHolder.delete);
            }
        });
        viewHolder.delete.setVisibility(View.INVISIBLE);

        viewHolder.add.setTag(position);
        viewHolder.add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int i = (Integer) v.getTag();
                saveMovie(i, v, viewHolder.delete);
            }
        });
        // check if move in db -> disable add button, show delete button
        if(viewModel.checkMovieExist(movie.getId()) == 1){
            checkAddButton(viewHolder.add, 0);
            viewHolder.delete.setVisibility(View.VISIBLE);
        }

        viewHolder.info.setTag(position);
        viewHolder.info.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int i = (Integer) v.getTag();
                showDetails(i);
            }
        });

        loadImage(mContext, POSTER_BASE_PATH + movie.getPosterPath(), viewHolder.progressBar, viewHolder.poster);
        // Return the completed view to render on screen
        return convertView;
    }

    private void showDetails(int i){
        int movie_id = getItem(i).getId();
        Intent intent = new Intent(mContext, MovieDetailsActivity.class);
        // get movie id
        // add key:value to intent with movie id
        intent.putExtra(INTENT_MOVIE_ID, movie_id);
        // start movie details activity
        mContext.startActivity(intent);
    }

    private void deleteMovie(int i, View addView, View deleteView){
        RetroMovie movie = getItem(i);
        viewModel.deleteMovie(movie.getId());
        // delete from arraylist
        if (dbItems){
            remove(movie);
            notifyDataSetChanged();
            lastPosition--;
        }
        checkAddButton(addView, 1);
        deleteView.setVisibility(View.INVISIBLE);
        showMessage(mContext, movie.getTitle() + " was deleted from your list");
    }

    private void saveMovie(int i, View addView, View deleteView){
        Log.e("TEST_DB_ADD", "TRYING TO ADD ITEM TO DATABASE");
        RetroMovie movie = getItem(i);
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
        checkAddButton(addView, 0);
        deleteView.setVisibility(View.VISIBLE);
        showMessage(mContext, title + " was saved to watch later");
    }

    private void checkAddButton(View v, int i){
        if (i == 0){
            v.setEnabled(false);
            v.setBackgroundResource(R.drawable.ic_checked);
        } else{
            v.setEnabled(true);
            v.setBackgroundResource(R.drawable.ic_add_circle_black_24dp);
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

//    @Override
//    public void onClick(View v) {
//        int position= (Integer) v.getTag();
//        RetroMovie movie = getItem(position);
//        String title = movie.getTitle();
//        int movie_id = movie.getId();
//
//        switch (v.getId())
//        {
//            case R.id.movie_item_add:
//                Log.e("TEST_DB_ADD", "TRYING TO ADD ITEM TO DATABASE");
//                //DbViewModel viewModel = ViewModelProviders.of(getContext()).get(DbViewModel.class);
//                MovieRoomItem movieRoomItem = new MovieRoomItem(movie_id,
//                        title,
//                        movie.getGenreIds(),
//                        movie.getReleaseDate(),
//                        movie.getPosterPath(),
//                        movie.getVoteAverage());
//                // insert new field into database
//                viewModel.insertMovie(movieRoomItem);
//                checkAddButton(v);
//                //v.delete.setVisibility(View.VISIBLE);
//                showMessage(mContext, title + " was saved to watch later");
//                break;
//
//            case R.id.movie_item_delete:
//                // delete movie from db
//                viewModel.deleteMovie(movie_id);
//                // delete from arraylist
//                if (dbItems){
//                    remove(movie);
//                    lastPosition--;
//                    notifyDataSetChanged();
//                }
//                showMessage(mContext, title + " was deleted from your list");
//                break;
//
//            case R.id.movie_item_info:
//                Intent intent = new Intent(mContext, MovieDetailsActivity.class);
//                // get movie id
//                // add key:value to intent with movie id
//                intent.putExtra(INTENT_MOVIE_ID, movie_id);
//                // start movie details activity
//                mContext.startActivity(intent);
//                break;
//        }
//    }
}
