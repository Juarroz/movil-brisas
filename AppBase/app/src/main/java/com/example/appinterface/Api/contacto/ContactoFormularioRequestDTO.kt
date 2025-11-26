package com.example.appinterface.Api.contacto

data class ContactoFormularioRequestDTO(
    val nombre: String,
    val correo: String?,
    val telefono: String?,
    val mensaje: String,
    val usuarioId: Int? = null,
    val terminos: Boolean = true,
    val via: String = "formulario"
)