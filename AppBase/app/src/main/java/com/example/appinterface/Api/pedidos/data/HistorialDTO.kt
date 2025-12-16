package com.example.appinterface.Api.pedidos.data

import com.google.gson.annotations.SerializedName

data class HistorialDTO(
    // ID del registro de historial
    @SerializedName("hisId")
    val hisId: Int,

    // Fecha y hora del cambio
    @SerializedName("hisFechaCambio")
    val hisFechaCambio: String?,

    // Comentarios registrados
    @SerializedName("hisComentarios")
    val hisComentarios: String?,

    // URL de la imagen de evidencia
    @SerializedName("hisImagen")
    val hisImagen: String?,

    // ID del estado al que cambió
    @SerializedName("estId")
    val estId: Int,

    @SerializedName("estadoNombre")
    val estadoNombre: String?,

    // 🔥 Asegurado para trazabilidad
    @SerializedName("responsableId")
    val responsableId: Int?,

    @SerializedName("responsableNombre")
    val responsableNombre: String?
)