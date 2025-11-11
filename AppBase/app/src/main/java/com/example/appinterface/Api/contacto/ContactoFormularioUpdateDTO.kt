package com.example.appinterface.Api.contacto


data class ContactoFormularioUpdateDTO(
    val usuarioId: Int? = null,
    val usuarioIdAdmin: Int? = null,
    val via: String? = null,
    val estado: String? = null,
    val notas: String? = null
)
