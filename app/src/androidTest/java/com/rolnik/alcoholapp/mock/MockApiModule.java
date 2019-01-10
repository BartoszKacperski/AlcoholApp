package com.rolnik.alcoholapp.mock;

import com.rolnik.alcoholapp.modules.ApiModule;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Module(includes = ApiModule.class)
public class MockApiModule extends ApiModule {
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

        return builder.build();
    }

    @Named("with_cookie")
    @Provides
    @Singleton
    public Retrofit provideRetrofitWithCookie(JacksonConverterFactory jacksonConverterFactory, RxJava2CallAdapterFactory rxJava2CallAdapterFactory, @Named("with_cookie") OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .addConverterFactory(jacksonConverterFactory).
                        addCallAdapterFactory(rxJava2CallAdapterFactory)
                .baseUrl("http://localhost:8080/")
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
                .baseUrl("http://localhost:8080/")
                .client(okHttpClient)
                .build();
    }
}
