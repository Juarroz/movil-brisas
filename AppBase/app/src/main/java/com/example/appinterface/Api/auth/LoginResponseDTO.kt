package com.example.appinterface.Api.auth

import com.google.gson.annotations.SerializedName

/**
 * DTO para la respuesta del login
 * Estructura según tu backend
 */
data class LoginResponseDTO(
    // 🔥 CRÍTICO: Añadir el campo userId
    @SerializedName("userId") val userId: Int,

    @SerializedName("token") val token: String,
    @SerializedName("email") val email: String,
    @SerializedName("userName") val userName: String,
    @SerializedName("userRole") val userRole: String,
    @SerializedName("roles") val roles: List<String>,
    @SerializedName("dashboardUrl") val dashboardUrl: String? = null,
    @SerializedName("message") val message: String? = null
)