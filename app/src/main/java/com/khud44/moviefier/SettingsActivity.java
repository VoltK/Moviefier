package com.khud44.moviefier;

import android.os.Bundle;
import android.support.v7.app.ActionBar;

public class SettingsActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        internetNeeded = false;
        setContentView(R.layout.activity_settings);

        ActionBar actionBar = this.getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle(R.string.settings);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void initAll(){}

}
