package com.khud44.moviefier.data;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import com.khud44.moviefier.data.dao.MyDAO;
import com.khud44.moviefier.data.models.GenreRoomItem;
import com.khud44.moviefier.data.models.MovieRoomItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DbRepository {

    private MyDAO myDAO;
    private LiveData<List<MovieRoomItem>> mAllMovies;
    //private List<GenreRoomItem> mAllGenres;

    DbRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        myDAO = db.getMovieDAO();
        mAllMovies = myDAO.getMovieRoomItems();
        // TEST
        //mAllGenres = myDAO.getGenreRoomItems();
    }

    LiveData<List<MovieRoomItem>> getAllMovies() {
        return mAllMovies;
    }

    public void insertMovie (MovieRoomItem movieRoomItem) {
        new insertMovieAsyncTask(myDAO).execute(movieRoomItem);
    }

    public void insertGenre(GenreRoomItem genreRoomItem){
        new insertGenreAsyncTask(myDAO).execute(genreRoomItem);
    }

    // delete from DB /* TEST */
    public void deleteMovie(int movie_id) { new deleteMovieAsyncTask(myDAO).execute(movie_id); }

    public int checkMovieExist(int movie_id){
        try {
            return new getCheckMovieAsyncTask(myDAO).execute(movie_id).get();
        } catch (ExecutionException | InterruptedException e){
            return 0;
        }
    }

    // TEST count
//    public int getGenresCount(){
//
//    }

    // TEST
    public List<GenreRoomItem> getAllGenres(String language) {
        try {
            return new getGenresAsyncTask(myDAO).execute(language).get();
        } catch (ExecutionException | InterruptedException e){
            return new ArrayList<>();
        }
    }

//    private static class getGenreByLanguageAsyncTask extends  AsyncTask<String, Void, List<GenreRoomItem>>{
//        private MyDAO mAsyncTaskDao;
//
//        getGenreByLanguageAsyncTask(MyDAO dao) {
//            mAsyncTaskDao = dao;
//        }
//
//        @Override
//        protected List<GenreRoomItem> doInBackground(String... language) {
//            return mAsyncTaskDao.getGenreByLanguage(language[0]);
//        }
//    }

    private static class getCheckMovieAsyncTask extends AsyncTask<Integer, Void, Integer>{

        private MyDAO mAsyncTaskDao;

        getCheckMovieAsyncTask(MyDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Integer doInBackground(Integer... movie_id){
            return mAsyncTaskDao.checkMovieExist(movie_id[0]);
        }

    }

    private static class getGenresAsyncTask extends AsyncTask<String, Void, List<GenreRoomItem>> {

        private MyDAO mAsyncTaskDao;

        getGenresAsyncTask(MyDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected List<GenreRoomItem> doInBackground(String... url) {
            if (url != null){
                return mAsyncTaskDao.getGenreByLanguage(url[0]);
            }
            return mAsyncTaskDao.getGenreAll();
        }
    }



    private static class insertMovieAsyncTask extends AsyncTask<MovieRoomItem, Void, Void> {

        private MyDAO mAsyncTaskDao;

        insertMovieAsyncTask(MyDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final MovieRoomItem... params) {
            mAsyncTaskDao.insertMovie(params[0]);
            return null;
        }
    }

    private static class insertGenreAsyncTask extends AsyncTask<GenreRoomItem, Void, Void> {

        private MyDAO mAsyncTaskDao;

        insertGenreAsyncTask(MyDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final GenreRoomItem... params) {
            mAsyncTaskDao.insertGenre(params[0]);
            return null;
        }
    }

    private static class deleteMovieAsyncTask extends AsyncTask<Integer, Void, Void> {

        private MyDAO mAsyncTaskDao;

        deleteMovieAsyncTask(MyDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Integer... params) {
            mAsyncTaskDao.deleteAtId(params[0]);
            return null;
        }
    }

}
