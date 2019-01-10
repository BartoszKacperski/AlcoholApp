package com.rolnik.alcoholapp.modules;

import android.support.annotation.NonNull;
import android.util.Log;

import com.rolnik.alcoholapp.MyApplication;
import com.rolnik.alcoholapp.sharedpreferenceservices.CookieSharedPreferencesService;

import java.io.IOException;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Module
public class ApiModule {
    private static final String API_BASE_URL = "https://alco-app.herokuapp.com/";

    public ApiModule() {

    }

    @Provides
    @Singleton
    public RxJava2CallAdapterFactory provideRxJava2CallAdapterFactory(){
        return RxJava2CallAdapterFactory.create();
    }

    @Provides
    @Singleton
    public JacksonConverterFactory provideJacksonConverterFactory(){
        return JacksonConverterFactory.create();
    }

    @Provides
    @Singleton
    public Interceptor provideAuthInterceptor(){
        return new Interceptor() {
            private CookieSharedPreferencesService cookieSharedPreferencesService = new CookieSharedPreferencesService(MyApplication.getAppContext());

            @NonNull
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder builder = original.newBuilder();

                String cookie = cookieSharedPreferencesService.getCookie();

                builder.addHeader("Cookie", cookie);
                Log.i("Adding cookie", "Cookie = " + cookie);

                return chain.proceed(builder.build());
            }
        };
    }

    @Named("without_cookie")
    @Provides
    @Singleton
    public OkHttpClient provideClientWithoutAuth(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.addInterceptor(getLoggingInterceptor());

        return builder.build();
    }

    @Named("with_cookie")
    @Provides
    @Singleton
    public OkHttpClient provideClient(Interceptor interceptor){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.addInterceptor(getLoggingInterceptor());
        builder.addInterceptor(interceptor);

        return builder.build();
    }

    @Named("with_cookie")
    @Provides
    @Singleton
    public Retrofit provideRetrofitWithCookie(JacksonConverterFactory jacksonConverterFactory, RxJava2CallAdapterFactory rxJava2CallAdapterFactory, @Named("with_cookie") OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .addConverterFactory(jacksonConverterFactory).
                addCallAdapterFactory(rxJava2CallAdapterFactory)
                .baseUrl(API_BASE_URL)
                .client(okHttpClient)
                .build();
    }

    @Named("without_cookie")
    @Provides
    @Singleton
    public Retrofit provideRetrofitWithoutCookie(JacksonConverterFactory jacksonConverterFactory, RxJava2CallAdapterFactory rxJava2CallAdapterFactory, @Named("without_cookie")OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .addConverterFactory(jacksonConverterFactory).
                        addCallAdapterFactory(rxJava2CallAdapterFactory)
                .baseUrl(API_BASE_URL)
                .client(okHttpClient)
                .build();
    }

    protected Interceptor getLoggingInterceptor(){
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return httpLoggingInterceptor;
    }
}
