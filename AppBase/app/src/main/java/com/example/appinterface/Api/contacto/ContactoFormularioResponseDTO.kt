package com.example.appinterface.Api.contacto

data class ContactoFormularioResponseDTO(
    val id: Long? = null,
    val nombre: String? = null,
    val correo: String? = null,
    val telefono: String? = null,
    val mensaje: String? = null,
    val fechaEnvio: String? = null,
    val via: String? = null,
    val terminos: Boolean? = null,
    val estado: String? = null,
    val notas: String? = null,
    val usuarioId: Long? = null,
    val usuarioNombre: String? = null,
    val usuarioIdAdmin: Long? = null,
    val usuarioAdminNombre: String? = null
)