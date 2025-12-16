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
         * 🔥 CORREGIDO: Ahora usa formato ISO 8601 con hora
         */
        fun crearConFechaActual(usuarioId: Int?, valores: List<Int>): PersonalizacionRequestDTO {
            // 🔥 CAMBIO CRÍTICO: De "yyyy-MM-dd" a "yyyy-MM-dd'T'HH:mm:ss"
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
            return PersonalizacionRequestDTO(
                usuarioClienteId = usuarioId,
                fecha = dateFormat.format(Date()),
                valoresSeleccionados = valores
            )
        }

        /**
         * Obtiene la fecha actual en formato ISO 8601
         * 🔥 CORREGIDO: Ahora incluye hora
         */
        fun obtenerFechaActual(): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
            return dateFormat.format(Date())
        }
    }
}