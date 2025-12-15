package com.example.appinterface.Api.pedidos.data

import com.google.gson.annotations.SerializedName

data class PedidoDTO(
    @SerializedName("ped_id")
    val pedId: Int, // Renombrado a pedId para el Adapter

    @SerializedName("pedCodigo")
    val pedCodigo: String?, // Renombrado a pedCodigo para el Adapter

    @SerializedName("pedFechaCreacion")
    val pedFechaCreacion: String?, // Renombrado a pedFechaCreacion

    @SerializedName("pedComentarios")
    val pedComentarios: String?,

    @SerializedName("estId")
    val estId: Int, // Renombrado a estId para el Adapter (asumo que no es nullable si viene en la BD)

    // --- CAMPOS ENRIQUECIDOS AÑADIDOS ---

    @SerializedName("estadoNombre")
    val estadoNombre: String?, // Nombre del estado (ej: "COTIZACION_PENDIENTE")

    @SerializedName("usuIdCliente")
    val usuIdCliente: Int?, // ID del Cliente (Tu campo anterior 'usuarioId' era ambiguo)

    @SerializedName("usuIdEmpleado")
    val usuIdEmpleado: Int?, // ID del Diseñador/Empleado asignado (CRUCIAL)

    @SerializedName("nombreCliente")
    val nombreCliente: String?, // Nombre completo del cliente (para la tarjeta)

    @SerializedName("nombreEmpleado")
    val nombreEmpleado: String? // Nombre completo del diseñador (para la tarjeta)

    // @SerializedName("perId") // El campo 'perId' ya no es necesario si tenemos 'usuIdCliente'
    // val personaId: Int?
)