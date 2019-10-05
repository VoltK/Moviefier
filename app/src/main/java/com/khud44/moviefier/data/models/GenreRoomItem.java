package com.khud44.moviefier.data.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "genres")

public class GenreRoomItem {

    @PrimaryKey
    @NonNull
    private int genre_id;
    private String name;

    @NonNull
    public int getGenre_id() {
        return genre_id;
    }

    public String getName() {
        return name;
    }

    public void setGenre_id(@NonNull int genre_id) {
        this.genre_id = genre_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GenreRoomItem(@NonNull int genre_id, String name) {
        this.genre_id = genre_id;
        this.name = name;
    }
}
