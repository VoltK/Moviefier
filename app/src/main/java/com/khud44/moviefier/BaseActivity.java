package com.khud44.moviefier;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.khud44.moviefier.retrofit.GetData;
import com.khud44.moviefier.utils.MyContextWrapper;

import java.util.Map;

import static com.khud44.moviefier.utils.Constants.*;
import static com.khud44.moviefier.utils.Helpers.loadPreferences;
import static com.khud44.moviefier.utils.Helpers.showMessage;

public abstract class BaseActivity extends AppCompatActivity {

    public GetData service;
    public boolean internetNeeded;

    public String location;
    public String language;
    public int screenLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart(){
        super.onStart();
        // check if location or language is null
        location = location == null ? getString(R.string.location_us_code) : location;
        language = language == null ? getString(R.string.language_en_value) : language;
        if (internetNeeded){
            internetCheck();
        }
        else{
            initAll();
        }
    }

    private void internetCheck(){
        if (isNetworkAvailable()){
            initAll();
        }
        else{
            noInternetHandle();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void noInternetHandle(){
        setContentView(R.layout.no_connection_layout);
    }

    public void restartInit(View view){
        if(isNetworkAvailable()){
            setContentView(screenLayout);
            initAll();
        } else{
            showMessage(this, getString(R.string.reconnect_error));
        }
    }

    private void setPreferences(Context context){
        //Log.e(TAG, "INSIDE setPreferences");
        Map<String, String> preferences= loadPreferences(context);
        location = preferences.get(context.getString(R.string.settings_location_key));
        language = preferences.get(context.getString(R.string.settings_language_key));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.saved:
                showSavedMovies();
                return true;
            case R.id.settings:
                showSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        recreate();
    }

    private void showSettings(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void showSavedMovies(){
        Intent intent = new Intent(this, MovieListActivity.class);
        intent.putExtra(INTENT_LIST_ACTIVITY, LIST_ACTIVITY_SAVED);
        intent.putExtra(INTENT_STORAGE, 0);
        startActivity(intent);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        setPreferences(newBase);
        super.attachBaseContext(MyContextWrapper.wrap(newBase,language));
    }

    public String getLocation(){
        return location;
    }

    public String getLanguage(){
        return language;
    }
    // this method should be the starting point for UI part
    public abstract void initAll();

}
