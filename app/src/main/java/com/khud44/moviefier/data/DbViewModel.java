package com.khud44.moviefier.data;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import com.khud44.moviefier.data.models.GenreRoomItem;
import com.khud44.moviefier.data.models.MovieRoomItem;

import java.util.List;

public class DbViewModel extends AndroidViewModel {

    private DbRepository mRepository;
    private LiveData<List<MovieRoomItem>> mAllMovies;
    private List<GenreRoomItem> mAllGenres;

    public DbViewModel(Application application) {
        super(application);
        mRepository = new DbRepository(application);
        mAllMovies = mRepository.getAllMovies();
        mAllGenres = mRepository.getAllGenres();
    }

    public LiveData<List<MovieRoomItem>> getAllWords() { return mAllMovies; }
    public List<GenreRoomItem> getAllGenres() {
        return mAllGenres;
    }

    public void insertMovie(MovieRoomItem movieRoomItem) { mRepository.insertMovie(movieRoomItem); }

    public void insertGenre(GenreRoomItem genreRoomItem) { mRepository.insertGenre(genreRoomItem); }

    public void deleteMovie(int movie_id){ mRepository.deleteMovie(movie_id);}

    public int checkMovieExist(int movie_id) { return mRepository.checkMovieExist(movie_id); }

}
