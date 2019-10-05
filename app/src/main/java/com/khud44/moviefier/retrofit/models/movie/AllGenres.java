package com.khud44.moviefier.retrofit.models.movie;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AllGenres {

        @SerializedName("genres")
        @Expose
        private List<Genre> genres = null;

        /**
         * No args constructor for use in serialization
         *
         */
        public AllGenres() {
        }

        /**
         *
         * @param genres
         */
        public AllGenres(List<Genre> genres) {
            super();
            this.genres = genres;
        }

        public List<Genre> getGenres() {
            return genres;
        }

        public void setGenres(List<Genre> genres) {
            this.genres = genres;
        }
 }

