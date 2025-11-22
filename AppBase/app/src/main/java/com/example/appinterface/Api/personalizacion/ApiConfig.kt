package com.example.appinterface.Api.personalizacion

import android.content.ContentValues.TAG
import android.util.Log

/**
 * Configuración de URLs para personalización
 */
object ApiConfig {
    // URL base de la API (para emulador Android)
    const val BASE_URL_API = "http://10.0.2.2:8080/api/"

    // URL base para assets (imágenes)
    const val BASE_URL_ASSETS = "http://10.0.2.2:8080"

    /**
     * Construye la ruta completa para un icono de opción
     */
    fun construirUrlIcono(categoria: String, nombreArchivo: String): String {
        return "$BASE_URL_ASSETS/assets/img/personalizacion/opciones/$categoria/$nombreArchivo"
    }

    /**
     * Construye la ruta completa para una vista de anillo
     */
    /**
     * Construye la ruta completa para una vista de anillo
     */
    fun construirUrlVistaAnillo(
        gema: String,
        forma: String,
        material: String,
        vista: String
    ): String {
        val url = "$BASE_URL_ASSETS/assets/img/personalizacion/vistas-anillos/" +
                "${gema.lowercase()}/${forma.lowercase()}/${material.lowercase()}/${vista.lowercase()}.jpg"

        Log.d(TAG, "URL generada: $url")
        return url
    }
}