package com.example.appinterface.core

import com.example.appinterface.Api.contacto.ContactoFormularioRequestDTO
import com.example.appinterface.Api.pedidos.PedidoResponseDTO
import com.example.appinterface.Api.RolResponseDTO
import com.example.appinterface.Api.auth.LoginRequestDTO
import com.example.appinterface.Api.auth.LoginResponseDTO
import com.example.appinterface.Api.usuarios.UsuarioRequestDTO
import com.example.appinterface.Api.usuarios.UsuarioResponseDTO
import com.example.appinterface.Api.contacto.ContactoFormularioResponseDTO
import com.example.appinterface.Api.contacto.ContactoFormularioUpdateDTO
import com.example.appinterface.Api.usuarios.PageWrapperDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
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

    // --- USUARIOS CRUD ---

    @GET("usuarios")
    fun getUsuarios(): Call<PageWrapperDTO>

    @POST("usuarios")
    fun createUsuario(
        @Body nuevoUsuario: UsuarioRequestDTO
    ): Call<UsuarioResponseDTO>

    @PUT("usuarios/{id}")
    fun updateUsuario(
        @retrofit2.http.Path("id") id: Long,
        @Body usuarioActualizado: UsuarioRequestDTO
    ): Call<UsuarioResponseDTO>

    @PATCH("usuarios/{id}/activo")
    fun cambiarEstadoUsuario(
        @retrofit2.http.Path("id") id: Long,
        @retrofit2.http.Query("activo") activo: Boolean
    ): Call<Void>

    @DELETE("usuarios/{id}")
    fun deleteUsuario(
        @retrofit2.http.Path("id") id: Long
    ): Call<Void>

}