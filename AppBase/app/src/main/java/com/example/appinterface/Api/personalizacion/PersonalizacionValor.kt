package com.example.appinterface.Api.personalizacion

import com.google.gson.annotations.SerializedName

/**
 * Representa un valor específico de una opción de personalización
 * Ejemplo: "Redonda", "Diamante", "Oro Rosa"
 *
 * Corresponde a la tabla: valor_personalizacion
 */
data class PersonalizacionValor(
    @SerializedName("id")
    val id: Int,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("imagen")
    val imagen: String? = null,

    @SerializedName("opcId")
    val opcId: Int,

    @SerializedName("opcionNombre")
    val opcionNombre: String? = null
) {
    /**
     * Genera el slug normalizado para construir rutas de imágenes
     * Ejemplos: "Oro Rosa" -> "oro-rosa", "Rubí" -> "rubi"
     */
    fun obtenerSlug(): String {
        return nombre
            .lowercase()
            .replace("á", "a")
            .replace("é", "e")
            .replace("í", "i")
            .replace("ó", "o")
            .replace("ú", "u")
            .replace("ñ", "n")
            .replace(" ", "-")
            .trim()
    }

    /**
     * Verifica si este valor tiene imagen disponible
     */
    fun tieneImagen(): Boolean {
        return !imagen.isNullOrEmpty()
    }

    /**
     * Construye la URL del icono de la opción
     * Solo aplica para opciones con imagen
     */
    fun construirUrlIcono(baseUrl: String, categoria: String): String? {
        return if (tieneImagen()) {
            "$baseUrl/assets/img/personalizacionproductos/opciones/$categoria/${imagen}"
        } else {
            null
        }
    }
}

/**
 * Respuesta del endpoint GET /api/valores
 */
data class ValoresResponseDTO(
    @SerializedName("success")
    val success: Boolean = false,

    @SerializedName("data")
    val data: List<PersonalizacionValor> = emptyList(),

    @SerializedName("message")
    val message: String? = null
)

/**
 * Extensión para filtrar valores por categoría
 */
fun List<PersonalizacionValor>.porCategoria(opcId: Int): List<PersonalizacionValor> {
    return this.filter { it.opcId == opcId }
}

/**
 * Extensión para filtrar valores por nombre de categoría
 */
fun List<PersonalizacionValor>.porNombreCategoria(nombreCategoria: String): List<PersonalizacionValor> {
    return this.filter {
        it.opcionNombre?.contains(nombreCategoria, ignoreCase = true) == true
    }
}