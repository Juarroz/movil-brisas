package com.example.appinterface.core


import com.example.appinterface.Api.contacto.ContactoFormularioRequestDTO
import com.example.appinterface.Api.usuarios.RolResponseDTO
import com.example.appinterface.Api.auth.LoginRequestDTO
import com.example.appinterface.Api.auth.LoginResponseDTO
import com.example.appinterface.Api.usuarios.UsuarioRequestDTO
import com.example.appinterface.Api.usuarios.UsuarioResponseDTO
import com.example.appinterface.Api.contacto.ContactoFormularioResponseDTO
import com.example.appinterface.Api.contacto.ContactoFormularioUpdateDTO
import com.example.appinterface.Api.pedidos.data.HistorialDTO
import com.example.appinterface.Api.usuarios.PageWrapperDTO
import com.example.appinterface.Api.usuarios.RolUpdateBody
import com.example.appinterface.Api.personalizacion.*
import com.example.appinterface.Api.pedidos.data.PedidoDTO
import com.example.appinterface.Api.pedidos.data.PedidoRequestDTO
import com.example.appinterface.Api.usuarios.data.EmpleadoDTO
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


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

    // ==========================================
    // ENDPOINTS DE PERSONALIZACIÓN
    // ==========================================

    @GET("opciones")
    suspend fun obtenerOpciones(): Response<List<PersonalizacionOption>>


    @GET("opciones/{id}")
    suspend fun obtenerOpcionPorId(
        @Path("id") opcionId: Int
    ): Response<OpcionesResponseDTO>

    @GET("valores")
    suspend fun obtenerValores(
        @Query("opcId") opcionId: Int? = null,
        @Query("buscar") busqueda: String? = null
    ): Response<List<PersonalizacionValor>>


    @GET("valores/{id}")
    suspend fun obtenerValorPorId(
        @Path("id") valorId: Int
    ): Response<ValoresResponseDTO>


    @POST("personalizaciones")
    suspend fun crearPersonalizacion(
        @Body request: PersonalizacionRequestDTO
    ): Response<PersonalizacionCreateResponseDTO>


    @GET("personalizaciones")
    suspend fun obtenerPersonalizaciones(
        @Query("clienteId") clienteId: Int? = null,
        @Query("fechaDesde") fechaDesde: String? = null,
        @Query("fechaHasta") fechaHasta: String? = null
    ): Response<PersonalizacionesListResponseDTO>


    @GET("personalizaciones/{id}")
    suspend fun obtenerPersonalizacionPorId(
        @Path("id") personalizacionId: Int
    ): Response<PersonalizacionResponseDTO>


    @PUT("personalizaciones/{id}")
    suspend fun actualizarPersonalizacion(
        @Path("id") personalizacionId: Int,
        @Body request: PersonalizacionRequestDTO
    ): Response<PersonalizacionResponseDTO>


    @DELETE("personalizaciones/{id}")
    suspend fun eliminarPersonalizacion(
        @Path("id") personalizacionId: Int
    ): Response<PersonalizacionResponseDTO>


    @GET("personalizaciones/{perId}/detalles")
    suspend fun obtenerDetallesPersonalizacion(
        @Path("perId") personalizacionId: Int
    ): Response<PersonalizacionDetallesResponseDTO>

    @POST("personalizaciones/{perId}/detalles")
    suspend fun agregarDetallePersonalizacion(
        @Path("perId") personalizacionId: Int,
        @Body request: PersonalizacionDetalleRequestDTO
    ): Response<PersonalizacionDetalleCreateResponseDTO>


    @GET("personalizaciones/{perId}/detalles/{detId}")
    suspend fun obtenerDetallePorId(
        @Path("perId") personalizacionId: Int,
        @Path("detId") detalleId: Int
    ): Response<PersonalizacionDetallesResponseDTO>


    @PUT("personalizaciones/{perId}/detalles/{detId}")
    suspend fun actualizarDetalle(
        @Path("perId") personalizacionId: Int,
        @Path("detId") detalleId: Int,
        @Body request: PersonalizacionDetalleRequestDTO
    ): Response<PersonalizacionDetalleCreateResponseDTO>


    @DELETE("personalizaciones/{perId}/detalles/{detId}")
    suspend fun eliminarDetalle(
        @Path("perId") personalizacionId: Int,
        @Path("detId") detalleId: Int
    ): Response<PersonalizacionDetalleCreateResponseDTO>



    // ==========================================
    // ENDPOINTS DE PEDIDO
    // ==========================================


    // 1. Admin (Lista completa)
    @GET("pedidos")
    fun getPedidos(): Call<List<PedidoDTO>>

    @GET("pedidos/{id}")
    fun obtenerPedido(@Path("id") id: Int): Call<PedidoDTO>

    // 2. Diseñador (Lista asignada)
    @GET("pedidos/empleado/{usuIdEmpleado}")
    fun obtenerPedidosPorEmpleado(@Path("usuIdEmpleado") userId: Int): Call<List<PedidoDTO>>

    // 3. Cliente (Lista propia)
    @GET("pedidos/cliente/{usuIdCliente}")
    fun obtenerPedidosPorCliente(@Path("usuIdCliente") userId: Int): Call<List<PedidoDTO>>


    @GET("pedidos/{id}/historial")
    fun obtenerHistorial(@Path("id") pedidoId: Int): Call<List<HistorialDTO>>

    @PATCH("pedidos/{id}/estado")
    fun actualizarEstado(
        @Path("id") pedidoId: Int,
        @Body payload: Map<String, @JvmSuppressWildcards Any> // Usamos Map<String, Any> para el JSON
    ): Call<PedidoDTO>

    // 🔥 Asignar Diseñador (PATCH /api/pedidos/{id}/asignar)
    @PATCH("pedidos/{id}/asignar")
    fun asignarDisenador(
        @Path("id") pedidoId: Int,
        @Body payload: Map<String, Int> // Recibe {usuIdEmpleado: X}
    ): Call<PedidoDTO>

    @POST("pedidos")
    fun crearPedido(
        @Body request: PedidoRequestDTO
    ): Call<PedidoDTO>

    // Actualizar un pedido (PUT)
    @PUT("pedidos/{id}")
    fun actualizarPedido(
        @Path("id") id: Int,
        @Body request: PedidoRequestDTO
    ): Call<PedidoDTO>

    // Eliminar un pedido (DELETE)
    @DELETE("pedidos/{id}")
    fun eliminarPedido(
        @Path("id") id: Int
    ): Call<Void>

    @GET("pedidos/cliente/{usuId}")
    fun getPedidosByClienteId(
        @Path("usuId") usuId: Int
    ): Call<List<PedidoDTO>>

    @GET("pedidos/empleado/{usuId}")
    fun getPedidosByEmpleadoId(
        @Path("usuId") usuId: Int
    ): Call<List<PedidoDTO>>

    @GET("usuarios/empleados")
    fun getDisenadores(): Call<List<EmpleadoDTO>>
}