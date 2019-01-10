package com.rolnik.alcoholapp.testutils;

import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RetrofitTestCreator {

    private static String URL = "test/";

    private RetrofitTestCreator(){

    }

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .addConverterFactory(JacksonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create());

    public static <T> T createTestRest(Class<T> classType, MockWebServer mockWebServer){
        return builder.baseUrl(mockWebServer.url(URL)).build().create(classType);
    }

}

