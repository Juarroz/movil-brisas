package com.example.appinterface.core

import com.example.appinterface.Api.contacto.ContactoFormularioRequestDTO
import com.example.appinterface.Api.usuarios.RolResponseDTO
import com.example.appinterface.Api.auth.LoginRequestDTO
import com.example.appinterface.Api.auth.LoginResponseDTO
import com.example.appinterface.Api.usuarios.UsuarioRequestDTO
import com.example.appinterface.Api.usuarios.UsuarioResponseDTO
import com.example.appinterface.Api.contacto.ContactoFormularioResponseDTO
import com.example.appinterface.Api.contacto.ContactoFormularioUpdateDTO
import com.example.appinterface.Api.usuarios.PageWrapperDTO
import com.example.appinterface.Api.usuarios.RolUpdateBody
import com.example.appinterface.core.model.Pedido
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
    fun getPedidos(): Call<List<Pedido>>

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

    @PATCH("usuarios/{id}/rol")
    fun cambiarRolUsuario(
        @retrofit2.http.Path("id") id: Long,
        @Body rolUpdate: RolUpdateBody // Ahora se envía en el cuerpo, no en Query
    ): Call<UsuarioResponseDTO>

    @DELETE("usuarios/{id}")
    fun deleteUsuario(
        @retrofit2.http.Path("id") id: Long
    ): Call<Void>

    // ... tus otros endpoints ...

    // Actualizar un pedido (PUT)
    @PUT("pedidos/{id}")
    fun actualizarPedido(
        @Path("id") id: Int,
        @Body request: com.example.appinterface.core.model.PedidoRequest
    ): Call<com.example.appinterface.core.model.Pedido>

    // Eliminar un pedido (DELETE)
    @DELETE("pedidos/{id}")
    fun eliminarPedido(
        @Path("id") id: Int
    ): Call<Void>

    // Crear un nuevo pedido (POST)
    @POST("pedidos")
    fun crearPedido(
        @Body request: com.example.appinterface.core.model.PedidoRequest
    ): Call<com.example.appinterface.core.model.Pedido>

}