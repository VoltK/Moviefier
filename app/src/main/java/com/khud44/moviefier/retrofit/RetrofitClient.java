package com.khud44.moviefier.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.khud44.moviefier.utils.Constants.MOVIE_BASE_URL;

public class RetrofitClient {

    private static Retrofit retrofit;

    //Define the base URL//

    //private static final String LOCATION_API = "http://ip-api.com/";

    //Create the Retrofit instance//
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(MOVIE_BASE_URL)
                    //Add the converter//
                    .addConverterFactory(GsonConverterFactory.create())
                    //Build the Retrofit instance//
                    .build();
        }
        return retrofit;
    }

}
