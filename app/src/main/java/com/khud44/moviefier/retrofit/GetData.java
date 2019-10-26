package com.khud44.moviefier.retrofit;

import com.khud44.moviefier.retrofit.models.actor.RetroActorDetails;
import com.khud44.moviefier.retrofit.models.movie.AllGenres;
import com.khud44.moviefier.retrofit.models.movie.RetroMovieDetails;
import com.khud44.moviefier.retrofit.models.movie_lists.RetroUpcomingResults;
import com.khud44.moviefier.retrofit.models.movie_lists.RetroMovieResults;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetData {

    @GET("movie/upcoming")
    Call<RetroUpcomingResults> getUpcomingMovies(@Query("api_key") String apiKey,
                                                 @Query("language") String language,
                                                 @Query("region") String region,
                                                 @Query("page") int page);

    @GET("discover/movie")
    Call<RetroMovieResults> getWeekReleases(@Query("api_key") String apiKey,
                                            @Query("language") String language,
                                            @Query("region") String region,
                                            @Query("release_date.gte") String start,
                                            @Query("release_date.lte") String end,
                                            @Query("with_release_type") String release_types,
                                            @Query("page") int page);

    @GET("movie/top_rated")
    Call<RetroMovieResults> getTopRated(@Query("api_key") String apiKey,
                                        @Query("language") String language,
                                        @Query("region") String region,
                                        @Query("page") int page);

    @GET("movie/{movie_id}")
    Call<RetroMovieDetails> getMovieDetails(@Path("movie_id") int movie_id,
                                            @Query("api_key") String apiKey,
                                            @Query("language") String language,
                                            @Query("append_to_response") String additional);

    @GET("person/{person_id}")
    Call<RetroActorDetails> getActorDetails(@Path("person_id") int person_id,
                                            @Query("api_key") String apiKey,
                                            @Query("language") String language,
                                            @Query("append_to_response") String additional);

    @GET("genre/movie/list")
    Call<AllGenres> getAllGenres(@Query("api_key") String apiKey, @Query("language") String lang);

    @GET("search/movie")
    Call<RetroMovieResults> getSearchedMovies(@Query("api_key") String apiKey,
                                              @Query("query") String movieTitle,
                                              @Query("language") String language,
                                              @Query("page") int page);

}
