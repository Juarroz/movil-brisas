package com.example.appinterface.Api.usuarios

data class UsuarioResponseDTO(
    val id: Long,
    val nombre: String,
    val correo: String,
    val telefono: String?,
    val activo: Boolean,
    val docnum: String?,
    val origen: String?,
    val rolId: Int?,
    val rolNombre: String?,
    val tipdocId: Int?,
    val tipdocNombre: String?
)