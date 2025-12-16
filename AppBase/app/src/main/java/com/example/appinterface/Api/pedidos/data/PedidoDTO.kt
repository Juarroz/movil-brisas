package com.example.appinterface.Api.pedidos.data

import com.google.gson.annotations.SerializedName

data class PedidoDTO(
    // 🔥 CORRECCIÓN CRÍTICA: Cambiar "ped_id" por "pedId"
    @SerializedName("pedId")
    val pedId: Int,

    @SerializedName("pedCodigo")
    val pedCodigo: String?,

    @SerializedName("pedFechaCreacion")
    val pedFechaCreacion: String?,

    @SerializedName("pedComentarios")
    val pedComentarios: String?,

    @SerializedName("estId")
    val estId: Int?,

    @SerializedName("estadoNombre")
    val estadoNombre: String?,

    @SerializedName("perId")
    val perId: Int?,

    @SerializedName("usuIdCliente")
    val usuIdCliente: Int?,

    @SerializedName("usuIdEmpleado")
    val usuIdEmpleado: Int?,

    @SerializedName("nombreCliente")
    val nombreCliente: String?,

    @SerializedName("nombreEmpleado")
    val nombreEmpleado: String?
)