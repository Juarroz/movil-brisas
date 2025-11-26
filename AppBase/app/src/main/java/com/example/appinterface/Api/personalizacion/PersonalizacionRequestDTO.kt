package com.example.appinterface.Api.personalizacion

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

data class PersonalizacionRequestDTO(
    @SerializedName("usuarioClienteId")
    val usuarioClienteId: Int?,

    @SerializedName("fecha")
    val fecha: String,

    @SerializedName("valoresSeleccionados")
    val valoresSeleccionados: List<Int>
) {
    companion object {
        /**
         * Crea un DTO con la fecha actual del sistema
         */
        fun crearConFechaActual(usuarioId: Int?, valores: List<Int>): PersonalizacionRequestDTO {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return PersonalizacionRequestDTO(
                usuarioClienteId = usuarioId,
                fecha = dateFormat.format(Date()),
                valoresSeleccionados = valores
            )
        }

        /**
         * Obtiene la fecha actual en formato yyyy-MM-dd
         */
        fun obtenerFechaActual(): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return dateFormat.format(Date())
        }
    }
}