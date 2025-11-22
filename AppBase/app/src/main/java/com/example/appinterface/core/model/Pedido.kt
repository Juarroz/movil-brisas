package com.example.appinterface.core.model

import com.google.gson.annotations.SerializedName

data class Pedido(
    @SerializedName("ped_id")
    val id: Int,

    @SerializedName("pedCodigo")
    val codigo: String?, // El signo ? es vital para evitar crasheos

    @SerializedName("pedFechaCreacion")
    val fechaCreacion: String?, // Lo dejamos como String por ahora

    @SerializedName("pedComentarios")
    val comentarios: String?,

    @SerializedName("estId")
    val estadoId: Int?,

    @SerializedName("perId")
    val personaId: Int?,

    @SerializedName("usuId")
    val usuarioId: Int?
)