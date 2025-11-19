package com.example.appinterface.Api.usuarios

/**
 * DTO para crear usuario - Registro
 */
data class UsuarioRequestDTO(
    val nombre: String,
    val correo: String,
    val telefono: String? = null,
    val password: String,
    val docnum: String? = null,
    val rolId: Int = 1,  // Por defecto: usuario normal
    val tipdocId: Int? = null,
    val origen: String = "registro",
    val activo: Boolean = true
)