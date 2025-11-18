package com.example.appinterface.Api.auth

/**
 * DTO para login - Tu backend espera "email" no "username"
 */
data class LoginRequestDTO(
    val email: String,      // ← Cambio de "username" a "email"
    val password: String
)