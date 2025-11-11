package com.example.appinterface.Api.usuarios

data class UsuarioRequestDTO(
    val nombre: String,
    val correo: String,
    val telefono: String?,
    val password: String? = null,
    val docnum: String?,
    val rolId: Int?,
    val tipdocId: Int?,
    val origen: String? = "registro",
    val activo: Boolean = true
)
