package com.example.appinterface.Api.personalizacion

import android.content.Context
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

/**
 * Helper para cargar imágenes de personalización usando Glide
 */
object ImagenHelper {

    /**
     * Carga una vista de anillo desde una URL
     * @param imageView ImageView donde cargar la imagen
     * @param url URL completa de la imagen
     * @param esPrincipal Si es true, usa placeholder más grande
     */
    fun cargarVistaAnillo(
        imageView: ImageView,
        url: String,
        esPrincipal: Boolean = false
    ) {
        val options = RequestOptions()
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_dialog_alert)
            .diskCacheStrategy(DiskCacheStrategy.ALL)

        Glide.with(imageView.context)
            .load(url)
            .apply(options)
            .into(imageView)
    }

    /**
     * Carga un icono de opción (para miniaturas de gemas, formas, materiales)
     * @param imageView ImageView donde cargar el icono
     * @param url URL completa del icono
     * @param iconoPorDefecto Recurso drawable a usar si falla la carga
     */
    fun cargarIconoOpcion(
        imageView: ImageView,
        url: String,
        @DrawableRes iconoPorDefecto: Int = android.R.drawable.ic_menu_gallery
    ) {
        val options = RequestOptions()
            .placeholder(iconoPorDefecto)
            .error(iconoPorDefecto)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()

        Glide.with(imageView.context)
            .load(url)
            .apply(options)
            .into(imageView)
    }

    /**
     * Precarga las 3 vistas de una combinación para mejorar performance
     */
    fun precargarVistas(
        context: Context,
        gemaSlug: String,
        formaSlug: String,
        materialSlug: String
    ) {
        val vistas = listOf("superior", "frontal", "perfil")

        vistas.forEach { vista ->
            val url = construirUrlVista(gemaSlug, formaSlug, materialSlug, vista)

            Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .preload()
        }
    }

    /**
     * Construye la URL de una vista específica
     */
    private fun construirUrlVista(
        gemaSlug: String,
        formaSlug: String,
        materialSlug: String,
        vista: String
    ): String {
        return "${ApiConfig.BASE_URL_ASSETS}/assets/img/personalizacion/vistas-anillos/" +
                "$gemaSlug/$formaSlug/$materialSlug/$vista.jpg"
    }
}