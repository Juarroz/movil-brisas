package com.example.appinterface.Api.personalizacion

import com.google.gson.annotations.SerializedName

/**
 * Representa una categoría de personalización
 * Ejemplo: "Forma de la gema", "Gema central", "Material"
 *
 * Corresponde a la tabla: opcion_personalizacion
 */
data class PersonalizacionOpcion(
    @SerializedName("id")
    val id: Int,

    @SerializedName("nombre")
    val nombre: String
) {
    /**
     * Determina la clave interna para mapeo
     * Ejemplos: "forma", "gema", "material", "tamano", "talla"
     */
    fun obtenerClave(): String {
        return when {
            nombre.contains("forma", ignoreCase = true) -> "forma"
            nombre.contains("gema", ignoreCase = true) -> "gema"
            nombre.contains("material", ignoreCase = true) -> "material"
            nombre.contains("tamaño", ignoreCase = true) ||
                    nombre.contains("tamano", ignoreCase = true) -> "tamano"
            nombre.contains("talla", ignoreCase = true) -> "talla"
            else -> "otros"
        }
    }

    /**
     * Obtiene el emoji asociado a la categoría
     */
    fun obtenerEmoji(): String {
        return when (obtenerClave()) {
            "forma" -> "📦"
            "gema" -> "💎"
            "material" -> "🏅"
            "tamano" -> "📏"
            "talla" -> "💍"
            else -> "⚙️"
        }
    }
}

/**
 * Respuesta del endpoint GET /api/opciones
 */
data class OpcionesResponseDTO(
    @SerializedName("success")
    val success: Boolean = false,

    @SerializedName("data")
    val data: List<PersonalizacionOpcion> = emptyList(),

    @SerializedName("message")
    val message: String? = null
)