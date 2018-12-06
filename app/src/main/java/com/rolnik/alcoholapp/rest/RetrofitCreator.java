package com.rolnik.alcoholapp.rest;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RetrofitCreator {

    public static final String API_BASE_URL = "https://alco-app.herokuapp.com";

    private RetrofitCreator(){

    }


    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create());

    private static OkHttpClient getLoggingClient(){
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build();
    }

    public static <T> T createService(Class<T> classType){
        return builder.client(getLoggingClient()).build().create(classType);
    }
}
