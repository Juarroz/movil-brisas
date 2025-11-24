package com.example.appinterface.Api.auth

import com.example.appinterface.Api.usuarios.UsuarioRequestDTO
import com.example.appinterface.Api.usuarios.UsuarioResponseDTO
import com.example.appinterface.core.ApiServicesKotlin
import com.example.appinterface.Api.pedidos.data.data.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * AuthRepository - Repositorio que centraliza todas las operaciones de autenticación
 *
 * Responsabilidades:
 * - Realizar login y guardar sesión automáticamente
 * - Manejar errores de autenticación
 * - Callbacks tipados para UI
 */
class AuthRepository(
    private val api: ApiServicesKotlin,
    private val sessionManager: SessionManager
) {


    fun login(
        username: String,
        password: String,
        onSuccess: (LoginResponseDTO) -> Unit,
        onError: (String) -> Unit
    ) {
        val request = LoginRequestDTO(username, password)

        // Debug: Ver qué se está enviando
        android.util.Log.d("AuthRepository", "Login attempt: $username")

        api.login(request).enqueue(object : Callback<LoginResponseDTO> {
            override fun onResponse(
                call: Call<LoginResponseDTO>,
                response: Response<LoginResponseDTO>
            ) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {

                        sessionManager.saveSession(
                            username = loginResponse.userName,  // Nombre completo para mostrar
                            token = loginResponse.token,
                            userId = 0,
                            roles = loginResponse.roles  // ← SIN MODIFICAR: ["ROLE_ADMINISTRADOR"]
                        )
                        onSuccess(loginResponse)
                    } else {
                        onError("Respuesta vacía del servidor")
                    }
                } else {
                    // Manejar errores HTTP específicos
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("AuthRepository", "Error ${response.code()}: $errorBody")

                    val errorMsg = when (response.code()) {
                        401 -> "Usuario o contraseña incorrectos"
                        403 -> "Acceso denegado"
                        404 -> "Servicio no encontrado"
                        500 -> "Error en el servidor. Intenta más tarde"
                        else -> "Error: ${response.code()} - ${response.message()}"
                    }
                    onError(errorMsg)
                }
            }

            override fun onFailure(call: Call<LoginResponseDTO>, t: Throwable) {
                // Manejar errores de conexión
                val errorMsg = when {
                    t.message?.contains("Unable to resolve host") == true ->
                        "No se puede conectar al servidor. Verifica tu conexión"
                    t.message?.contains("timeout") == true ->
                        "Tiempo de espera agotado. Intenta nuevamente"
                    else ->
                        "Error de conexión: ${t.message}"
                }
                onError(errorMsg)
            }
        })
    }

    fun register(
        registerRequest: UsuarioRequestDTO,
        onSuccess: (UsuarioResponseDTO) -> Unit,
        onError: (String) -> Unit
    ) {
        val call = api.createUsuario(registerRequest)

        call.enqueue(object : Callback<UsuarioResponseDTO> {
            override fun onResponse(
                call: Call<UsuarioResponseDTO>,
                response: Response<UsuarioResponseDTO>
            ) {
                if (response.isSuccessful) {
                    val usuarioResponse = response.body()
                    if (usuarioResponse != null) {
                        onSuccess(usuarioResponse)
                    } else {
                        onError("Respuesta vacía del servidor")
                    }
                } else {
                    val errorMsg = when (response.code()) {
                        400 -> {
                            // Extraer mensaje específico del backend si existe
                            try {
                                val errorBody = response.errorBody()?.string()
                                if (errorBody?.contains("correo", ignoreCase = true) == true) {
                                    "Este correo ya está registrado"
                                } else if (errorBody?.contains("password", ignoreCase = true) == true) {
                                    "La contraseña no cumple los requisitos"
                                } else {
                                    "Datos inválidos. Verifica la información"
                                }
                            } catch (e: Exception) {
                                "Datos inválidos. Verifica la información"
                            }
                        }
                        409 -> "Este correo ya está registrado"
                        500 -> "Error en el servidor. Intenta más tarde"
                        else -> {
                            try {
                                val errorBody = response.errorBody()?.string()
                                errorBody ?: "Error al registrar usuario (${response.code()})"
                            } catch (e: Exception) {
                                "Error al registrar usuario (${response.code()})"
                            }
                        }
                    }
                    onError(errorMsg)
                }
            }

            override fun onFailure(call: Call<UsuarioResponseDTO>, t: Throwable) {
                val errorMsg = when (t) {
                    is java.net.UnknownHostException ->
                        "Sin conexión a internet"
                    is java.net.SocketTimeoutException ->
                        "Tiempo de espera agotado"
                    is java.net.ConnectException ->
                        "No se puede conectar al servidor"
                    else ->
                        "Error de red: ${t.localizedMessage ?: "Desconocido"}"
                }
                onError(errorMsg)
            }
        })
    }

    /**
     * Cierra la sesión del usuario
     */
    fun logout() {
        sessionManager.logout()
    }

    /**
     * Verifica si hay sesión activa
     */
    fun isLoggedIn(): Boolean = sessionManager.isLoggedIn()

    /**
     * Verifica si el usuario es admin
     */
    fun isAdmin(): Boolean = sessionManager.isAdmin()
}