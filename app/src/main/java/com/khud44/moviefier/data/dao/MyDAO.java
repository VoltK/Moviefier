package com.khud44.moviefier.data.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.*;
import com.khud44.moviefier.data.models.GenreRoomItem;
import com.khud44.moviefier.data.models.MovieRoomItem;

import java.util.List;

@Dao
public interface MyDAO {

    @Insert
    public void insertMovie(MovieRoomItem... movies);
    @Update
    public void updateMovie(MovieRoomItem... movies);
    @Delete
    public void deleteMovie(MovieRoomItem movie);

    @Insert
    public void insertGenre(GenreRoomItem... genres);
    @Update
    public void updateGenre(GenreRoomItem... genres);
    @Delete
    public void deleteGenre(GenreRoomItem genre);

    @Query("SELECT * FROM movies ORDER BY title ASC")
    public LiveData<List<MovieRoomItem>> getMovieRoomItems();

    @Query("SELECT 1 FROM movies WHERE movie_id = :movie_id")
    public int checkMovieExist(int movie_id);

    @Query("SELECT * FROM genres")
    public List<GenreRoomItem> getGenreAll();


    @Query("SELECT * FROM genres WHERE language = :language")
    public List<GenreRoomItem> getGenreByLanguage(String language);

    @Query("SELECT COUNT(*) FROM GENRES")
    public int getGenresCount();

    @Query("DELETE FROM movies WHERE movie_id = :movie_id")
    public void deleteAtId(int movie_id);

}
