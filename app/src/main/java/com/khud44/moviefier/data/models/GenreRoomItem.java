package com.khud44.moviefier.data.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "genres")

public class GenreRoomItem {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    @NonNull
    private int genre_id;
    private String name;
    private String language;

    @NonNull
    public int getGenre_id() {
        return genre_id;
    }

    public String getName() {
        return name;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getLanguage() { return language; }

    public void setGenre_id(@NonNull int genre_id) {
        this.genre_id = genre_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLanguage(String language) { this.language = language; }

    public GenreRoomItem(@NonNull int genre_id, String name, String language) {
        this.genre_id = genre_id;
        this.name = name;
        this.language = language;
    }
}
