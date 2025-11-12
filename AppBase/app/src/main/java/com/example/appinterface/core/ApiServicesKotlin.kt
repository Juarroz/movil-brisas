package com.example.appinterface.core

import com.example.appinterface.Api.contacto.ContactoFormularioRequestDTO
import com.example.appinterface.Api.pedidos.PedidoResponseDTO
import com.example.appinterface.Api.RolResponseDTO
import com.example.appinterface.Api.auth.LoginRequestDTO
import com.example.appinterface.Api.auth.LoginResponseDTO
import com.example.appinterface.Api.contacto.ContactoFormularioResponseDTO
import com.example.appinterface.Api.contacto.ContactoFormularioUpdateDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Path

interface ApiServicesKotlin {

    @POST("auth/login")
    fun login(@Body request: LoginRequestDTO): Call<LoginResponseDTO>


    @GET("roles")
    fun getRoles(): Call<List<RolResponseDTO>>


  //CRUD DE CONTACTO

      @POST("contactos")
      fun enviarContacto(@Body contacto: ContactoFormularioRequestDTO): Call<ContactoFormularioResponseDTO>

      @GET("contactos")
      fun listarContactos(): Call<List<ContactoFormularioResponseDTO>>


    @PUT("contactos/{id}")
    fun actualizarContactoDTO(
        @Path("id") id: Int,
        @Body update: ContactoFormularioUpdateDTO
    ): Call<ContactoFormularioResponseDTO>

    @DELETE("contactos/{id}")
    fun eliminarContacto(
        @Path("id") id: Int
    ): Call<Void>


    @GET("pedidos")
    fun getPedidos(): Call<List<PedidoResponseDTO>>
}