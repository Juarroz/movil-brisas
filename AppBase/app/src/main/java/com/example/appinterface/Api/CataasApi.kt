package com.example.appinterface.Api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

interface CataasApi {
    @Headers("Accept: application/json")
    @GET("cat?json=true")
    fun getRandomCat(): Call<CatResponseDTO>
}