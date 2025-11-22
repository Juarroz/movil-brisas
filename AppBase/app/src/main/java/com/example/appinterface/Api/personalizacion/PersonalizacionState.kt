package com.example.appinterface.Api.personalizacion

/**
 * Estado de la personalización
 */
data class PersonalizacionState(
    val selecciones: MutableMap<String, PersonalizacionValor> = mutableMapOf(),
    var vistaActual: String = "frontal"
) {

    // Getters de slugs
    val gemaSlug: String
        get() = selecciones["gema"]?.obtenerSlug() ?: "diamante"

    val formaSlug: String
        get() = selecciones["forma"]?.obtenerSlug() ?: "redonda"

    val materialSlug: String
        get() = selecciones["material"]?.obtenerSlug() ?: "oro-amarillo"

    /**
     * Actualiza una selección
     */
    fun actualizar(categoria: String, valor: PersonalizacionValor) {
        selecciones[categoria] = valor
    }

    /**
     * Construye la URL de una vista específica
     */
    fun construirUrlImagen(baseUrl: String, vista: String): String {
        return "$baseUrl/assets/img/personalizacion/vistas-anillos/" +
                "$gemaSlug/$formaSlug/$materialSlug/$vista.jpg"
    }

    /**
     * Obtiene todas las URLs de las 3 vistas
     */
    fun obtenerUrlsVistas(baseUrl: String): Map<String, String> {
        return mapOf(
            "superior" to construirUrlImagen(baseUrl, "superior"),
            "frontal" to construirUrlImagen(baseUrl, "frontal"),
            "perfil" to construirUrlImagen(baseUrl, "perfil")
        )
    }

    /**
     * Genera resumen textual
     */
    fun obtenerResumen(): String {
        val forma = selecciones["forma"]?.nombre ?: "No seleccionada"
        val gema = selecciones["gema"]?.nombre ?: "No seleccionada"
        val material = selecciones["material"]?.nombre ?: "No seleccionado"
        val tamano = selecciones["tamano"]?.nombre ?: "No seleccionado"
        val talla = selecciones["talla"]?.nombre ?: "No seleccionada"

        return """
            Forma: $forma
            Gema: $gema
            Material: $material
            Tamaño: $tamano
            Talla: $talla
        """.trimIndent()
    }

    /**
     * Valida que todas las opciones requeridas estén seleccionadas
     */
    fun esValido(): Boolean {
        return selecciones.containsKey("forma") &&
                selecciones.containsKey("gema") &&
                selecciones.containsKey("material") &&
                selecciones.containsKey("tamano") &&
                selecciones.containsKey("talla")
    }

    /**
     * Obtiene mensaje de error si no es válido
     */
    fun obtenerMensajeError(): String? {
        return when {
            !selecciones.containsKey("forma") -> "Selecciona una forma"
            !selecciones.containsKey("gema") -> "Selecciona una gema"
            !selecciones.containsKey("material") -> "Selecciona un material"
            !selecciones.containsKey("tamano") -> "Selecciona un tamaño"
            !selecciones.containsKey("talla") -> "Selecciona una talla"
            else -> null
        }
    }

    /**
     * Obtiene IDs de valores seleccionados para enviar al backend
     */
    fun obtenerIdsSeleccionados(): List<Int> {
        return selecciones.values.map { it.id }
    }
}