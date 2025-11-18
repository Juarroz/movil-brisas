package com.example.appinterface.Api.auth

/**
 * DTO para la respuesta del login
 * Estructura según tu backend
 */
data class LoginResponseDTO(
    val token: String,
    val email: String,
    val userName: String,
    val userRole: String,
    val roles: List<String>,
    val dashboardUrl: String? = null,
    val message: String? = null
)