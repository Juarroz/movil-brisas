package com.example.appinterface.Api.personalizacion

import com.google.gson.annotations.SerializedName

/**
 * Respuesta al crear una personalización
 * POST /api/personalizaciones
 */
data class PersonalizacionCreateResponseDTO(
    @SerializedName("success")
    val success: Boolean = false,

    @SerializedName("data")
    val data: PersonalizacionCreada? = null,

    @SerializedName("message")
    val message: String? = null
)

data class PersonalizacionCreada(
    @SerializedName("id")
    val id: Int,

    @SerializedName("per_fecha")
    val fecha: String,

    @SerializedName("usu_id_cliente")
    val usuarioClienteId: Int?
)

/**
 * Respuesta de una personalización existente
 * GET /api/personalizaciones/{id}
 */
data class PersonalizacionResponseDTO(
    @SerializedName("success")
    val success: Boolean = false,

    @SerializedName("data")
    val data: PersonalizacionData? = null,

    @SerializedName("message")
    val message: String? = null
)

data class PersonalizacionData(
    @SerializedName("id")
    val id: Int,

    @SerializedName("per_fecha")
    val fecha: String,

    @SerializedName("usu_id_cliente")
    val usuarioClienteId: Int?
)

/**
 * Respuesta de los detalles de una personalización
 * GET /api/personalizaciones/{id}/detalles
 */
data class PersonalizacionDetallesResponseDTO(
    @SerializedName("success")
    val success: Boolean = false,

    @SerializedName("data")
    val data: List<DetallePersonalizacion> = emptyList(),

    @SerializedName("message")
    val message: String? = null
)

data class DetallePersonalizacion(
    @SerializedName("det_id")
    val detalleId: Int,

    @SerializedName("val_id")
    val valorId: Int,

    @SerializedName("val_nombre")
    val valorNombre: String,

    @SerializedName("opc_id")
    val opcionId: Int,

    @SerializedName("opc_nombre")
    val opcionNombre: String
) {
    /**
     * Determina la categoría del detalle
     */
    fun obtenerCategoria(): String {
        return when {
            opcionNombre.contains("forma", ignoreCase = true) -> "forma"
            opcionNombre.contains("gema", ignoreCase = true) -> "gema"
            opcionNombre.contains("material", ignoreCase = true) -> "material"
            opcionNombre.contains("tamaño", ignoreCase = true) -> "tamano"
            opcionNombre.contains("talla", ignoreCase = true) -> "talla"
            else -> "otros"
        }
    }
}

/**
 * Respuesta de listado de personalizaciones
 * GET /api/personalizaciones
 */
data class PersonalizacionesListResponseDTO(
    @SerializedName("success")
    val success: Boolean = false,

    @SerializedName("data")
    val data: List<PersonalizacionResumen> = emptyList(),

    @SerializedName("message")
    val message: String? = null
)

data class PersonalizacionResumen(
    @SerializedName("id")
    val id: Int,

    @SerializedName("per_fecha")
    val fecha: String,

    @SerializedName("usu_id_cliente")
    val usuarioClienteId: Int?,

    @SerializedName("cantidad_detalles")
    val cantidadDetalles: Int? = null
)