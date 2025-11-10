package com.example.appinterface.core

import com.example.appinterface.Api.contacto.ContactoFormularioRequestDTO
import com.example.appinterface.Api.pedidos.PedidoResponseDTO
import com.example.appinterface.Api.RolResponseDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiServicesKotlin {
    @GET("roles")
    fun getRoles(): Call<List<RolResponseDTO>>

    @POST("contactos")
    fun enviarContacto(
        @Body contacto: ContactoFormularioRequestDTO
    ): Call<Void>

    @GET("pedidos")
    fun getPedidos(): Call<List<PedidoResponseDTO>>
}