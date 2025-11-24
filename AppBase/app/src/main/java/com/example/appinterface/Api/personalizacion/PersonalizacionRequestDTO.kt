package com.example.appinterface.Api.personalizacion

import com.google.gson.annotations.SerializedName

/**
 * DTO para crear una nueva personalización
 * Endpoint: POST /api/personalizaciones
 */
data class PersonalizacionRequestDTO(
    @SerializedName("usu_id_cliente")
    val usuarioClienteId: Int?,

    @SerializedName("valores")
    val valores: List<Int> // Lista de val_id seleccionados
) {
    companion object {
        /**
         * Factory method para crear desde el estado actual
         */
        fun desdeEstado(estado: PersonalizacionState, usuarioId: Int?): PersonalizacionRequestDTO {
            return PersonalizacionRequestDTO(
                usuarioClienteId = usuarioId,
                valores = estado.obtenerIdsSeleccionados()
            )
        }
    }
}

/**
 * DTO alternativo si el backend espera los detalles de forma diferente
 */
data class PersonalizacionConDetallesRequestDTO(
    @SerializedName("usu_id_cliente")
    val usuarioClienteId: Int?,

    @SerializedName("per_fecha")
    val fecha: String, // Formato: "YYYY-MM-DD"

    @SerializedName("detalles")
    val detalles: List<DetalleRequest>
)

data class DetalleRequest(
    @SerializedName("val_id")
    val valorId: Int
)