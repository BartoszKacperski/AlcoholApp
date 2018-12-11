package com.rolnik.alcoholapp.restUtils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.rolnik.alcoholapp.MyApplication;
import com.rolnik.alcoholapp.utils.CookieService;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RetrofitCreator {

    private static final String API_BASE_URL = "https://alco-app.herokuapp.com";

    private RetrofitCreator(){

    }


    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create());

    private static Interceptor getLoggingInterceptor(){
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return httpLoggingInterceptor;
    }

    private static Interceptor getAuthenticationInterceptor() {
        final CookieService cookieService = new CookieService(MyApplication.getAppContext());

        return new Interceptor() {
            @NonNull
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder builder = original.newBuilder();

                String cookie = cookieService.getCookie();

                builder.addHeader("Cookie", cookie);
                Log.i("Adding cookie", "Cookie = " + cookie);

                return chain.proceed(builder.build());
            }
        };
    }

    private static OkHttpClient getClientWithCookieService(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.addInterceptor(getLoggingInterceptor());
        builder.addInterceptor(getAuthenticationInterceptor());

        return builder.build();
    }

    private static OkHttpClient getClientWithoutCookieService(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.addInterceptor(getLoggingInterceptor());

        return builder.build();
    }

    public static <T> T createServiceWithCookieService(Class<T> classType){
        return builder.client(getClientWithCookieService()).build().create(classType);
    }

    public static <T> T createServiceWithoutCookieService(Class<T> classType){
        return builder.client(getClientWithoutCookieService()).build().create(classType);
    }
}
