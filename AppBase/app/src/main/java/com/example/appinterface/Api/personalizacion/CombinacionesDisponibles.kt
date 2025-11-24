package com.example.appinterface.Api.personalizacion

/**
 * Validador de combinaciones de personalización disponibles
 * Solo incluye combinaciones que tienen imágenes en el servidor
 */
object CombinacionesDisponibles {

    // Formas con imágenes disponibles
    val FORMAS_VALIDAS = setOf(
        "redonda",
        "ovalada"
    )

    // Gemas con imágenes disponibles
    val GEMAS_VALIDAS = setOf(
        "diamante",
        "esmeralda",
        "rubi",
        "zafiro"
    )

    // Materiales con imágenes disponibles
    val MATERIALES_VALIDOS = setOf(
        "oro-amarillo",
        "oro-blanco",
        "oro-rosa"
    )

    // Vistas disponibles
    val VISTAS_DISPONIBLES = setOf(
        "superior",
        "frontal",
        "perfil"
    )

    /**
     * Valida si una combinación tiene imágenes disponibles
     */
    fun esCombinaciónValida(forma: String, gema: String, material: String): Boolean {
        return FORMAS_VALIDAS.contains(forma.lowercase()) &&
                GEMAS_VALIDAS.contains(gema.lowercase()) &&
                MATERIALES_VALIDOS.contains(material.lowercase())
    }

    /**
     * Filtra valores para mostrar solo los que tienen combinaciones válidas
     */
    fun filtrarValoresDisponibles(
        valores: List<PersonalizacionValor>,
        categoria: String
    ): List<PersonalizacionValor> {
        return when (categoria) {
            "forma" -> valores.filter {
                FORMAS_VALIDAS.contains(it.obtenerSlug())
            }
            "gema" -> valores.filter {
                GEMAS_VALIDAS.contains(it.obtenerSlug())
            }
            "material" -> valores.filter {
                MATERIALES_VALIDOS.contains(it.obtenerSlug())
            }
            else -> valores // Tamaño y talla no afectan las imágenes
        }
    }

    /**
     * Obtiene sugerencias de combinaciones si la actual no es válida
     */
    fun obtenerSugerencias(forma: String?, gema: String?, material: String?): String {
        val sugerencias = mutableListOf<String>()

        if (forma != null && !FORMAS_VALIDAS.contains(forma.lowercase())) {
            sugerencias.add("Formas disponibles: Redonda, Ovalada")
        }
        if (gema != null && !GEMAS_VALIDAS.contains(gema.lowercase())) {
            sugerencias.add("Gemas disponibles: Diamante, Esmeralda, Rubí, Zafiro")
        }
        if (material != null && !MATERIALES_VALIDOS.contains(material.lowercase())) {
            sugerencias.add("Materiales disponibles: Oro Amarillo, Oro Blanco, Oro Rosa")
        }

        return if (sugerencias.isNotEmpty()) {
            sugerencias.joinToString("\n")
        } else {
            "Esta combinación está disponible"
        }
    }
}