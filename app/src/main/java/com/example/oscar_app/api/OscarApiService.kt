package com.example.oscar_app.api

import com.example.oscar_app.models.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface OscarApiService {

    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("voto")
    fun confirmarVoto(@Body request: VoteRequest): Call<Void> // Or a response model if needed

    @GET("filme.json")
    fun getFilmes(): Call<List<Filme>>

    @GET("diretor.json")
    fun getDiretores(): Call<List<Diretor>>

    companion object {
        //private const val BASE_URL = "http://10.0.2.2:8080/"
        private const val BASE_URL = "http://192.168.1.2:8080/"
        fun create(): OscarApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(OscarApiService::class.java)
        }
    }
}