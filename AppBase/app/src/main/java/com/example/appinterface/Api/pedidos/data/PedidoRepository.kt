package com.example.appinterface.Api.pedidos.data

import com.example.appinterface.Api.pedidos.data.PedidoDTO
import com.example.appinterface.Api.pedidos.data.PedidoRequestDTO
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
                    val msg = if (response.code() == 403) "Acceso denegado. Permisos insuficientes." else "Error HTTP ${response.code()}"
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
    suspend fun actualizarPedido(id: Int, request: PedidoRequestDTO): Result<Boolean> = suspendCoroutine { continuation ->
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

    fun actualizarEstado(
        pedidoId: Int,
        nuevoEstadoId: Int,
        comentarios: String?
    ): Call<PedidoDTO> {
        // Tu API en Spring Boot requiere: {nuevoEstadoId, comentarios}
        // y el responsableId (que es fijo en 2 en tu ejemplo, o debe venir del JWT)
        val responsableId = sessionManager.getUserId() ?: 2 // Usar el ID del usuario logueado

        val payload = mutableMapOf<String, Any>()
        payload["nuevoEstadoId"] = nuevoEstadoId
        payload["comentarios"] = comentarios ?: ""
        // Si el responsableId es necesario en el body (tu Spring Boot lo ignora por ahora,
        // pero es buena práctica si no usas JWT para eso):
        // payload["responsableId"] = responsableId

        return api.actualizarEstado(pedidoId, payload)
    }

    fun asignarDisenador(
        pedidoId: Int,
        usuIdEmpleado: Int
    ): Call<PedidoDTO> {
        val payload = mapOf("usuIdEmpleado" to usuIdEmpleado)
        return api.asignarDisenador(pedidoId, payload)
    }
}