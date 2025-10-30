package com.example.appinterface.Api

data class ContactoFormularioRequestDTO(
    val nombre: String,
    val correo: String?,
    val telefono: String?,
    val mensaje: String,
    val terminos: Boolean = true,
    val via: String = "formulario"
)