package com.example.appinterface.Api.auth

data class LoginResponseDTO(
    val token: String,
    val tokenType: String? = null,
    val username: String? = null,
    val roles: List<String>? = null
)