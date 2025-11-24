package com.example.appinterface.Api.personalizacion

import com.google.gson.annotations.SerializedName

/**
 * DTO para crear un detalle individual
 * POST /api/personalizaciones/{perId}/detalles
 */
data class PersonalizacionDetalleRequestDTO(
    @SerializedName("val_id")
    val valorId: Int
)

/**
 * Respuesta al crear un detalle
 */
data class PersonalizacionDetalleCreateResponseDTO(
    @SerializedName("success")
    val success: Boolean = false,

    @SerializedName("data")
    val data: DetalleCreado? = null,

    @SerializedName("message")
    val message: String? = null
)

data class DetalleCreado(
    @SerializedName("det_id")
    val detalleId: Int,

    @SerializedName("per_id")
    val personalizacionId: Int,

    @SerializedName("val_id")
    val valorId: Int
)