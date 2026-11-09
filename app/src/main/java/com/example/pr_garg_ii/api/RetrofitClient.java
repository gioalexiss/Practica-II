package com.example.pr_garg_ii.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Cliente Retrofit
public final class RetrofitClient {

    public static final String BASE_URL = "https://ejercicio-08-documentacion-y-endpoints.onrender.com/";

    private static LibroApiService service;

    private RetrofitClient() { }

    public static synchronized LibroApiService getService() {
        if (service == null) {
            // Timeouts amplios
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            service = retrofit.create(LibroApiService.class);
        }
        return service;
    }
}
