package com.example.appinterface.core

import com.example.appinterface.Api.contacto.ContactoFormularioRequestDTO
import com.example.appinterface.Api.pedidos.PedidoResponseDTO
import com.example.appinterface.Api.RolResponseDTO
import com.example.appinterface.Api.auth.LoginRequestDTO
import com.example.appinterface.Api.auth.LoginResponseDTO
import com.example.appinterface.Api.usuarios.UsuarioRequestDTO
import com.example.appinterface.Api.usuarios.UsuarioResponseDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiServicesKotlin {

    @POST("auth/login")
    fun login(@Body request: LoginRequestDTO): Call<LoginResponseDTO>


    @GET("roles")
    fun getRoles(): Call<List<RolResponseDTO>>

    @POST("contactos")
    fun enviarContacto(
        @Body contacto: ContactoFormularioRequestDTO
    ): Call<Void>

    @GET("pedidos")
    fun getPedidos(): Call<List<PedidoResponseDTO>>

    // --- USUARIOS CRUD ---

    @GET("usuarios")
    fun getUsuarios(): Call<List<UsuarioResponseDTO>>

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