package com.example.appinterface.Api.personalizacion

import com.google.gson.annotations.SerializedName

/**
 * Representa una categoría de personalización
 * Coincide con GET /api/opciones:
 * {"id": 1, "nombre": "Forma de la gema"}
 */
data class PersonalizacionOption(
    @SerializedName("id")
    val id: Int,

    @SerializedName("nombre")
    val nombre: String
) {
    /**
     * Obtiene el slug normalizado para identificar la categoría
     * "Forma de la gema" → "forma"
     * "Gema central" → "gema"
     */
    fun obtenerClave(): String {
        return when {
            nombre.equals("Forma de la gema", ignoreCase = true) -> "forma"
            nombre.equals("Gema central", ignoreCase = true) -> "gema"
            nombre.equals("Material", ignoreCase = true) -> "material"
            nombre.equals("Tamaño de la gema", ignoreCase = true) -> "tamano"
            nombre.equals("Talla del anillo", ignoreCase = true) -> "talla"
            else -> {
                // Fallback más específico
                when {
                    nombre.contains("forma", ignoreCase = true) -> "forma"
                    nombre.startsWith("Gema", ignoreCase = true) -> "gema" // EXACTO al inicio
                    nombre.contains("material", ignoreCase = true) -> "material"
                    nombre.contains("tamaño", ignoreCase = true) -> "tamano"
                    nombre.contains("talla", ignoreCase = true) -> "talla"
                    else -> nombre.lowercase().replace(" ", "")
                }
            }
        }
    }
}
