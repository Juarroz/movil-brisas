package com.example.appinterface.Api

data class PedidoRequestDTO(
    val pedCodigo: String,
    val pedComentarios: String? = null,
    val estId: Int? = null,
    val perId: Int? = null,
    val usuId: Int? = null
)