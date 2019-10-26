package com.khud44.moviefier.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.khud44.moviefier.R;

import java.util.HashMap;
import java.util.Map;

public class Helpers {

    public static void showMessage(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static int getRatingColor(Context context, double rating){
        int ratingResId;
        switch ((int) Math.floor(rating)){
            case 0:
            case 1:
                ratingResId = R.color.rating1;
                break;
            case 2:
                ratingResId = R.color.rating2;
                break;
            case 3:
                ratingResId = R.color.rating3;
                break;
            case 4:
                ratingResId = R.color.rating4;
                break;
            case 5:
                ratingResId = R.color.rating5;
                break;
            case 6:
                ratingResId = R.color.rating6;
                break;
            case 7:
                ratingResId = R.color.rating7;
                break;
            case 8:
                ratingResId = R.color.rating8;
                break;
            case 9:
                ratingResId = R.color.rating9;
                break;
            default:
                ratingResId = R.color.rating10;
                break;
        }
        return ContextCompat.getColor(context, ratingResId);
    }

    // listener to hide progress bar
    private static RequestListener<Bitmap> getRequestListener(final ProgressBar progressBar){
        return new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                return false;
            }
        };
    }

    // request option for image (round corners etc)
    private static RequestOptions requestOptions = new RequestOptions().transform(
            new CenterCrop(),
            // round corners
            new RoundedCorners(16))
            // image in case there is no image available
            .error(R.drawable.no_image2);

    public static void loadImage(Context context, String fullPath, ProgressBar progressBar, ImageView imageView){
        // getApplicationContext() instead of context to fix the bug in Glide when activity isFinishing() and app crashes
        Glide.with(context.getApplicationContext()).asBitmap()
                .load(fullPath)
                .apply(requestOptions)
                .listener(getRequestListener(progressBar))
                .into(imageView);
    }

    public static Map<String, String> loadPreferences(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Map<String, String> prefInfo = new HashMap<>();
        // keys
        String locationKey = context.getString(R.string.settings_location_key);
        String languageKey = context.getString(R.string.settings_language_key);
        // values
        String locationValue = sharedPreferences.getString(locationKey, context.getString(R.string.location_us_code));
        String languageValue = sharedPreferences.getString(languageKey, context.getString(R.string.language_en_value));

        prefInfo.put(locationKey, locationValue);
        prefInfo.put(languageKey, languageValue);

        return prefInfo;
    }

}
