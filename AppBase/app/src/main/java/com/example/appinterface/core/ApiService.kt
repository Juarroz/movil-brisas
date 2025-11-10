package com.example.appinterface.core

import com.example.appinterface.Api.DataResponse
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("breed/hound/images")
    fun getHoundImages(): Call<DataResponse>

}