package com.example.appinterface.Api.pedidos.model

import com.google.gson.annotations.SerializedName

data class PedidoRequest(
    @SerializedName("pedCodigo")
    val codigo: String,

    @SerializedName("pedComentarios")
    val comentarios: String?,

    @SerializedName("estId")
    val estadoId: Int,

    @SerializedName("perId")
    val personaId: Int?,

    @SerializedName("usuId")
    val usuarioId: Int?
)