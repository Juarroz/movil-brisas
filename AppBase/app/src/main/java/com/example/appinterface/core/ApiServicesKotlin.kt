package com.example.appinterface.core

import com.example.appinterface.Api.contacto.ContactoFormularioRequestDTO
import com.example.appinterface.Api.pedidos.PedidoResponseDTO
import com.example.appinterface.Api.usuarios.RolResponseDTO
import com.example.appinterface.Api.auth.LoginRequestDTO
import com.example.appinterface.Api.auth.LoginResponseDTO
import com.example.appinterface.Api.usuarios.UsuarioRequestDTO
import com.example.appinterface.Api.usuarios.UsuarioResponseDTO
import com.example.appinterface.Api.contacto.ContactoFormularioResponseDTO
import com.example.appinterface.Api.contacto.ContactoFormularioUpdateDTO
import com.example.appinterface.Api.usuarios.PageWrapperDTO
import com.example.appinterface.Api.usuarios.RolUpdateBody
import com.example.appinterface.Api.personalizacion.*
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

    /**
     * Obtiene todas las categorías de personalización (opciones)
     * GET /api/opciones
     *
     * Ejemplo de respuesta:
     * {
     *   "success": true,
     *   "data": [
     *     {"id": 1, "nombre": "Forma de la gema"},
     *     {"id": 2, "nombre": "Gema central"}
     *   ]
     * }
     */
    @GET("opciones")
    suspend fun obtenerOpciones(): Response<List<PersonalizacionOption>>

    /**
     * Obtiene una opción específica por ID
     * GET /api/opciones/{id}
     */
    @GET("opciones/{id}")
    suspend fun obtenerOpcionPorId(
        @Path("id") opcionId: Int
    ): Response<OpcionesResponseDTO>

    /**
     * Obtiene todos los valores de personalización
     * GET /api/valores
     *
     * Query params opcionales:
     * - opcId: filtra por categoría
     * - buscar: búsqueda por nombre
     *
     * Ejemplo de respuesta:
     * {
     *   "success": true,
     *   "data": [
     *     {
     *       "id": 1,
     *       "nombre": "Redonda",
     *       "imagen": "redonda.png",
     *       "opcId": 1,
     *       "opcionNombre": "Forma de la gema"
     *     }
     *   ]
     * }
     */
    @GET("valores")
    suspend fun obtenerValores(
        @Query("opcId") opcionId: Int? = null,
        @Query("buscar") busqueda: String? = null
    ): Response<List<PersonalizacionValor>>

    /**
     * Obtiene un valor específico por ID
     * GET /api/valores/{id}
     */
    @GET("valores/{id}")
    suspend fun obtenerValorPorId(
        @Path("id") valorId: Int
    ): Response<ValoresResponseDTO>

    /**
     * Crea una nueva personalización (encabezado + detalles)
     * POST /api/personalizaciones
     *
     * Body:
     * {
     *   "usu_id_cliente": 7,
     *   "valores": [1, 4, 7, 12, 15]
     * }
     *
     * Respuesta:
     * {
     *   "success": true,
     *   "data": {
     *     "id": 123,
     *     "per_fecha": "2024-11-20",
     *     "usu_id_cliente": 7
     *   }
     * }
     */
    @POST("personalizaciones")
    suspend fun crearPersonalizacion(
        @Body request: PersonalizacionRequestDTO
    ): Response<PersonalizacionCreateResponseDTO>

    /**
     * Obtiene todas las personalizaciones
     * GET /api/personalizaciones
     *
     * Query params opcionales:
     * - clienteId: filtra por usuario
     * - fechaDesde: formato YYYY-MM-DD
     * - fechaHasta: formato YYYY-MM-DD
     */
    @GET("personalizaciones")
    suspend fun obtenerPersonalizaciones(
        @Query("clienteId") clienteId: Int? = null,
        @Query("fechaDesde") fechaDesde: String? = null,
        @Query("fechaHasta") fechaHasta: String? = null
    ): Response<PersonalizacionesListResponseDTO>

    /**
     * Obtiene una personalización específica (encabezado)
     * GET /api/personalizaciones/{id}
     */
    @GET("personalizaciones/{id}")
    suspend fun obtenerPersonalizacionPorId(
        @Path("id") personalizacionId: Int
    ): Response<PersonalizacionResponseDTO>

    /**
     * Actualiza una personalización
     * PUT /api/personalizaciones/{id}
     */
    @PUT("personalizaciones/{id}")
    suspend fun actualizarPersonalizacion(
        @Path("id") personalizacionId: Int,
        @Body request: PersonalizacionRequestDTO
    ): Response<PersonalizacionResponseDTO>

    /**
     * Elimina una personalización
     * DELETE /api/personalizaciones/{id}
     */
    @DELETE("personalizaciones/{id}")
    suspend fun eliminarPersonalizacion(
        @Path("id") personalizacionId: Int
    ): Response<PersonalizacionResponseDTO>

    /**
     * Obtiene los detalles completos de una personalización
     * GET /api/personalizaciones/{perId}/detalles
     *
     * Respuesta:
     * {
     *   "success": true,
     *   "data": [
     *     {
     *       "det_id": 1,
     *       "val_id": 1,
     *       "val_nombre": "Redonda",
     *       "opc_id": 1,
     *       "opc_nombre": "Forma de la gema"
     *     }
     *   ]
     * }
     */
    @GET("personalizaciones/{perId}/detalles")
    suspend fun obtenerDetallesPersonalizacion(
        @Path("perId") personalizacionId: Int
    ): Response<PersonalizacionDetallesResponseDTO>

    /**
     * Agrega un detalle individual a una personalización existente
     * POST /api/personalizaciones/{perId}/detalles
     *
     * Body:
     * {
     *   "val_id": 5
     * }
     */
    @POST("personalizaciones/{perId}/detalles")
    suspend fun agregarDetallePersonalizacion(
        @Path("perId") personalizacionId: Int,
        @Body request: PersonalizacionDetalleRequestDTO
    ): Response<PersonalizacionDetalleCreateResponseDTO>

    /**
     * Obtiene un detalle específico
     * GET /api/personalizaciones/{perId}/detalles/{detId}
     */
    @GET("personalizaciones/{perId}/detalles/{detId}")
    suspend fun obtenerDetallePorId(
        @Path("perId") personalizacionId: Int,
        @Path("detId") detalleId: Int
    ): Response<PersonalizacionDetallesResponseDTO>

    /**
     * Actualiza un detalle específico
     * PUT /api/personalizaciones/{perId}/detalles/{detId}
     */
    @PUT("personalizaciones/{perId}/detalles/{detId}")
    suspend fun actualizarDetalle(
        @Path("perId") personalizacionId: Int,
        @Path("detId") detalleId: Int,
        @Body request: PersonalizacionDetalleRequestDTO
    ): Response<PersonalizacionDetalleCreateResponseDTO>

    /**
     * Elimina un detalle específico
     * DELETE /api/personalizaciones/{perId}/detalles/{detId}
     */
    @DELETE("personalizaciones/{perId}/detalles/{detId}")
    suspend fun eliminarDetalle(
        @Path("perId") personalizacionId: Int,
        @Path("detId") detalleId: Int
    ): Response<PersonalizacionDetalleCreateResponseDTO>



}