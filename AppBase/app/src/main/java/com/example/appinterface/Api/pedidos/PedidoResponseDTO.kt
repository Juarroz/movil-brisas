package com.example.appinterface.Api.pedidos

data class PedidoResponseDTO(
    val ped_id: Int? = null,
    val pedCodigo: String? = null,
    val pedFechaCreacion: String? = null,
    val pedComentarios: String? = null,
    val estId: Int? = null,
    val perId: Int? = null,
    val usuId: Int? = null
)