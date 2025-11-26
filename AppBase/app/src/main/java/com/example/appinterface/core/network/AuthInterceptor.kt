package com.example.appinterface.core.network

import com.example.appinterface.core.data.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * AuthInterceptor - Interceptor de OkHttp que añade el token JWT
 * automáticamente a todas las peticiones que requieran autenticación.
 *
 * Esto evita tener que añadir el header manualmente en cada llamada.
 */
class AuthInterceptor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Si no hay token, continúa sin modificar la petición
        val token = sessionManager.getAuthToken()
        if (token.isNullOrBlank()) {
            return chain.proceed(originalRequest)
        }

        // Añade el header Authorization con el token
        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", token)
            .build()

        return chain.proceed(authenticatedRequest)
    }
}