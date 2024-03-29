package com.khud44.moviefier.utils;


public class Constants {

    // youtube api key
    public static final String YOUTUBE_API_KEY = "your_youtube_api_key";
    // TheMovieDB related api urls
    // api key
    public static final String MOVIE_API_KEY = "your_themoviedb_api_key";
    // movie DB base url
    public static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/";
    // upcoming movies
    //public static final String UPCOMING_MOVIE_API = "https://api.themoviedb.org/3/movie/upcoming?api_key=%s&language=%s&page=%d&region=%s";
    // Data in format: 2019-09-14
    // placeholders for api_key, language, region, release_date.gte, release_date.lte
    //public static final String WEEK_RELEASE_MOVIE_API = "https://api.themoviedb.org/3/discover/movie?api_key=%s&language=%s&region=%s&release_date.gte=%s&release_date.lte=%s&with_release_type=2|3&page=%d";
    // top rated movies
    //public static final String TOP_RATED_MOVIE_API = "https://api.themoviedb.org/3/movie/top_rated?api_key=%s&language=%s&page=%d&region=%s";
    // base url for img storage
    public static final String POSTER_BASE_PATH = "https://image.tmdb.org/t/p/w185";

    public static final String POSTER_DETAILS_PATH = "https://image.tmdb.org/t/p/w342";

    // Intent keys
    public static final String INTENT_MOVIE_ID = "movie_id";
    public static final String INTENT_ACTOR_ID = "actor_id";
    public static final String INTENT_LIST_ACTIVITY = "list_activity";

    public static final String INTENT_STORAGE = "storage";

    public static final String LIST_ACTIVITY_UPCOMING = "upcoming";
    public static final String LIST_ACTIVITY_WEEK = "week";
    public static final String LIST_ACTIVITY_TOP = "top";
    public static final String LIST_ACTIVITY_SEARCH = "search";
    public static final String LIST_ACTIVITY_SAVED = "saved";

    public static final String MOVIE_TITLE = "movie_title";

    public static final int MAX_PAGES = 5;

}
