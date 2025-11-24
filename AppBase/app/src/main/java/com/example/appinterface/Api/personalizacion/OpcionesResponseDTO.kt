package com.example.appinterface.Api.personalizacion

import com.google.gson.annotations.SerializedName

/**
 * Respuesta del endpoint GET /api/opciones
 * Retorna las categorías de personalización disponibles
 */
data class OpcionesResponseDTO(
    @SerializedName("success")
    val success: Boolean = false,

    @SerializedName("data")
    val data: List<PersonalizacionOption> = emptyList(),

    @SerializedName("message")
    val message: String? = null
)