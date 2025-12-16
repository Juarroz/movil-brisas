package com.example.appinterface.Api.pedidos.data

import android.util.Log
import com.example.appinterface.Api.pedidos.data.PedidoDTO
import com.example.appinterface.Api.pedidos.data.PedidoRequestDTO
import com.example.appinterface.Api.usuarios.data.EmpleadoDTO
import com.example.appinterface.core.ApiServicesKotlin
import com.example.appinterface.core.data.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class PedidoRepository(
    private val api: ApiServicesKotlin,
    private val sessionManager: SessionManager // Aseguramos que SessionManager esté aquí
) {

    // 🔥 Mantendremos solo esta función para cargar la lista, usando callbacks para simplificar
    fun getPedidosByRole(
        onSuccess: (List<PedidoDTO>) -> Unit,
        onError: (String) -> Unit
    ) {
        val userId = sessionManager.getUserId()
        val roles = sessionManager.getRoles()

        // 1. Determinar el Call basado en el Rol
        val call: Call<List<PedidoDTO>>? = when {
            // ADMIN: Ve todos los pedidos
            roles.contains("ROLE_ADMINISTRADOR") -> {
                api.getPedidos()
            }
            // DISEÑADOR: Ve solo los asignados a él
            roles.contains("ROLE_DISEÑADOR") && userId != null -> {
                api.getPedidosByEmpleadoId(userId)
            }
            // USUARIO: Ve solo los que él creó
            roles.contains("ROLE_USUARIO") && userId != null -> {
                api.getPedidosByClienteId(userId)
            }
            // Usuario logueado sin rol reconocido O Anónimo
            sessionManager.isLoggedIn() -> {
                // Rol inesperado
                onError("Rol no reconocido o permisos insuficientes.")
                null
            }
            // Usuario no logueado
            else -> {
                // Si la app permite ver pedidos como anónimo, se ajusta aquí.
                // Asumimos que si no está logueado, no puede ver pedidos.
                onError("Debe iniciar sesión para ver sus pedidos.")
                null
            }
        }

        // 2. Ejecutar el Call (Retrofit)
        if (call == null) return

        call.enqueue(object : Callback<List<PedidoDTO>> {
            override fun onResponse(call: Call<List<PedidoDTO>>, response: Response<List<PedidoDTO>>) {
                if (response.isSuccessful) {
                    onSuccess(response.body() ?: emptyList())
                } else {
                    val msg =
                        if (response.code() == 403) "Acceso denegado. Permisos insuficientes." else "Error HTTP ${response.code()}"
                    onError(msg)
                }
            }

            override fun onFailure(call: Call<List<PedidoDTO>>, t: Throwable) {
                onError("Error de conexión: ${t.message}")
            }
        })
    }

    // --- MÉTODOS SUSPEND (Mantenidos) ---

    // Función para actualizar (mantenida de tu código)
    suspend fun actualizarPedido(id: Int, request: PedidoRequestDTO): Result<Boolean> =
        suspendCoroutine { continuation ->
            api.actualizarPedido(id, request).enqueue(object : Callback<PedidoDTO> {
                override fun onResponse(call: Call<PedidoDTO>, response: Response<PedidoDTO>) {
                    if (response.isSuccessful) {
                        continuation.resume(Result.success(true))
                    } else {
                        continuation.resume(Result.failure(Exception("Error al actualizar: ${response.code()}")))
                    }
                }

                override fun onFailure(call: Call<PedidoDTO>, t: Throwable) {
                    continuation.resume(Result.failure(t))
                }
            })
        }

    // Función para eliminar (mantenida de tu código)
    suspend fun eliminarPedido(id: Int): Result<Boolean> = suspendCoroutine { continuation ->
        api.eliminarPedido(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    continuation.resume(Result.success(true))
                } else {
                    continuation.resume(Result.failure(Exception("Error al eliminar: ${response.code()}")))
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                continuation.resume(Result.failure(t))
            }
        })
    }

    // Función para crear (mantenida de tu código)
    suspend fun crearPedido(request: PedidoRequestDTO): Result<Boolean> = suspendCoroutine { continuation ->
        api.crearPedido(request).enqueue(object : Callback<PedidoDTO> {
            override fun onResponse(call: Call<PedidoDTO>, response: Response<PedidoDTO>) {
                if (response.isSuccessful) {
                    continuation.resume(Result.success(true))
                } else {
                    continuation.resume(Result.failure(Exception("Error al crear: ${response.code()}")))
                }
            }

            override fun onFailure(call: Call<PedidoDTO>, t: Throwable) {
                continuation.resume(Result.failure(t))
            }
        })
    }

    /**
     * Actualiza el estado de un pedido con historial
     */
    fun actualizarEstado(
        pedidoId: Int,
        nuevoEstadoId: Int,
        comentarios: String?
    ): Call<PedidoDTO> {
        val payload = buildMap<String, Any> {
            put("nuevoEstadoId", nuevoEstadoId)
            put("comentarios", comentarios?.trim() ?: "Actualización sin comentarios")
        }

        return api.actualizarEstado(pedidoId, payload)
    }

    fun asignarDisenador(
        pedidoId: Int,
        usuIdEmpleado: Int
    ): Call<PedidoDTO> {
        val payload = mapOf("usuIdEmpleado" to usuIdEmpleado)
        return api.asignarDisenador(pedidoId, payload)
    }

    fun getDisenadores(
        onSuccess: (List<EmpleadoDTO>) -> Unit,
        onError: (String) -> Unit
    ) {
        api.getDisenadores().enqueue(object : Callback<List<EmpleadoDTO>> {
            override fun onResponse(call: Call<List<EmpleadoDTO>>, response: Response<List<EmpleadoDTO>>) {
                if (response.isSuccessful) {
                    onSuccess(response.body() ?: emptyList())
                } else {
                    onError("Error ${response.code()} al cargar diseñadores.")
                }
            }
            override fun onFailure(call: Call<List<EmpleadoDTO>>, t: Throwable) {
                onError("Fallo de red al cargar diseñadores: ${t.message}")
            }
        })
    }

    suspend fun getHistorial(pedidoId: Int): Result<List<HistorialDTO>> = suspendCoroutine { continuation ->
        api.obtenerHistorial(pedidoId).enqueue(object : Callback<List<HistorialDTO>> {
            override fun onResponse(call: Call<List<HistorialDTO>>, response: Response<List<HistorialDTO>>) {
                if (response.isSuccessful) {
                    continuation.resume(Result.success(response.body() ?: emptyList()))
                } else {
                    val msg = "Error al cargar historial: ${response.code()}"
                    continuation.resume(Result.failure(Exception(msg)))
                }
            }

            override fun onFailure(call: Call<List<HistorialDTO>>, t: Throwable) {
                continuation.resume(Result.failure(t))
            }
        })
    }

    suspend fun getPedidoById(pedidoId: Int): Result<PedidoDTO> = suspendCoroutine { continuation ->
        api.obtenerPedido(pedidoId).enqueue(object : Callback<PedidoDTO> {
            override fun onResponse(call: Call<PedidoDTO>, response: Response<PedidoDTO>) {
                if (response.isSuccessful && response.body() != null) {
                    continuation.resume(Result.success(response.body()!!))
                } else {
                    val msg = "Error HTTP ${response.code()} al cargar el pedido."
                    continuation.resume(Result.failure(Exception(msg)))
                }
            }
            override fun onFailure(call: Call<PedidoDTO>, t: Throwable) {
                continuation.resume(Result.failure(t))
            }
        })
    }
}