package com.khud44.moviefier.data.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "movies")
public class MovieRoomItem {
    @PrimaryKey
    @NonNull
    private Integer movie_id;
    private String title;
    private List<Integer> genre;
    private String releaseDate;
    private String posterPath;
    private double rating;

    public MovieRoomItem(int movie_id, String title, List<Integer> genre, String releaseDate, String posterPath, double rating) {
        this.movie_id = movie_id;
        this.title = title;
        this.genre = genre;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.rating = rating;
    }

    @NonNull
    public Integer getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(@NonNull Integer movie_id) {
        this.movie_id = movie_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Integer> getGenre() {
        return genre;
    }

    public void setGenre(List<Integer> genre) {
        this.genre = genre;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
