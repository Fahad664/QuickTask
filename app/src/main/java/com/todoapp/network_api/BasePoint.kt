package com.todoapp.network_api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
//This is a Retrofit Instance class. It is used to send requests to an API.
object BasePoint {
    private var retrofit : Retrofit? = null
    var localhostLink = "http://localhost/API_learning/"
    private var serverLink = "https://learning-api.000webhostapp.com/todoapp/"

    fun getEvents(): Retrofit? {
        if (retrofit ==null){
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            retrofit = Retrofit.Builder().baseUrl(serverLink)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .build()
        }
        return retrofit
    }
}