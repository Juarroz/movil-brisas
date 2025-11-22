package com.example.appinterface.Api.personalizacion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.appinterface.R

/**
 * Adapter para mostrar opciones de personalización en RecyclerView horizontal
 * Maneja selección única y actualización eficiente con DiffUtil
 */
class PersonalizacionAdapter(
    private val categoria: String,
    private val onItemSelected: (PersonalizacionValor) -> Unit
) : ListAdapter<PersonalizacionValor, PersonalizacionAdapter.OpcionViewHolder>(OpcionDiffCallback()) {

    // Índice del item actualmente seleccionado
    private var selectedPosition: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OpcionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_personalizacion_option, parent, false)
        return OpcionViewHolder(view)
    }

    override fun onBindViewHolder(holder: OpcionViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, position == selectedPosition)
    }

    /**
     * Actualiza la selección y notifica los cambios
     */
    fun setSelectedPosition(position: Int) {
        if (position in 0 until itemCount) {
            val previousPosition = selectedPosition
            selectedPosition = position

            // Notificar solo los items que cambiaron
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
        }
    }

    /**
     * Selecciona un item por su ID de valor
     */
    fun selectItemById(valorId: Int) {
        val position = currentList.indexOfFirst { it.id == valorId }
        if (position != -1) {
            setSelectedPosition(position)
        }
    }

    /**
     * Obtiene el item actualmente seleccionado
     */
    fun getSelectedItem(): PersonalizacionValor? {
        return if (selectedPosition in 0 until itemCount) {
            getItem(selectedPosition)
        } else {
            null
        }
    }

    inner class OpcionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Fíjate que ahora dice MaterialCardView
        private val cardOption: MaterialCardView = itemView.findViewById(R.id.card_option)
        private val ivIcon: ImageView = itemView.findViewById(R.id.iv_option_icon)
        private val tvName: TextView = itemView.findViewById(R.id.tv_option_name)
        private val viewIndicator: View = itemView.findViewById(R.id.view_selected_indicator)

        fun bind(valor: PersonalizacionValor, isSelected: Boolean) {
            // Configurar nombre
            tvName.text = valor.nombre

            // Configurar icono (si existe)
            if (valor.tieneImagen()) {
                ivIcon.visibility = View.VISIBLE
                val urlIcono = valor.construirUrlIcono(
                    ApiConfig.BASE_URL_ASSETS,
                    obtenerCategoriaParaRuta()
                )
                if (urlIcono != null) {
                    ImagenHelper.cargarIconoOpcion(
                        ivIcon,
                        urlIcono,
                        obtenerIconoPorDefecto()
                    )
                }
            } else {
                ivIcon.visibility = View.GONE
            }

            // Aplicar estilo de selección
            aplicarEstiloSeleccion(isSelected)

            // Configurar click listener
            cardOption.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    setSelectedPosition(adapterPosition)
                    onItemSelected(valor)
                }
            }
        }

        /**
         * Aplica el estilo visual según si está seleccionado o no
         */
        private fun aplicarEstiloSeleccion(isSelected: Boolean) {
            val context = itemView.context

            if (isSelected) {
                // Estilo seleccionado
                cardOption.strokeColor = ContextCompat.getColor(context, R.color.green_brisas)
                cardOption.strokeWidth = context.resources.getDimensionPixelSize(R.dimen.stroke_width_selected)
                cardOption.cardElevation = context.resources.getDimension(R.dimen.elevation_selected)
                tvName.setTextColor(ContextCompat.getColor(context, R.color.green_brisas))
                tvName.setTypeface(null, android.graphics.Typeface.BOLD)
                viewIndicator.visibility = View.VISIBLE
            } else {
                // Estilo no seleccionado
                cardOption.strokeColor = ContextCompat.getColor(context, R.color.gray_light)
                cardOption.strokeWidth = context.resources.getDimensionPixelSize(R.dimen.stroke_width_unselected)
                cardOption.cardElevation = context.resources.getDimension(R.dimen.elevation_unselected)
                tvName.setTextColor(ContextCompat.getColor(context, R.color.gray_dark))
                tvName.setTypeface(null, android.graphics.Typeface.NORMAL)
                viewIndicator.visibility = View.GONE
            }
        }

        /**
         * Obtiene el nombre de carpeta para la categoría
         */
        private fun obtenerCategoriaParaRuta(): String {
            return when (categoria) {
                "forma" -> "forma"
                "gema" -> "gemas"
                "material" -> "material"
                "tamano" -> "tama-piedra-central"
                else -> categoria
            }
        }

        /**
         * Obtiene el icono por defecto según la categoría
         */
        private fun obtenerIconoPorDefecto(): Int {
            return when (categoria) {
                "forma" -> R.drawable.ic_shape_default
                "gema" -> R.drawable.ic_diamond
                "material" -> R.drawable.ic_material_default
                else -> R.drawable.ic_default
            }
        }
    }

    /**
     * DiffUtil para comparación eficiente de listas
     */
    private class OpcionDiffCallback : DiffUtil.ItemCallback<PersonalizacionValor>() {
        override fun areItemsTheSame(
            oldItem: PersonalizacionValor,
            newItem: PersonalizacionValor
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: PersonalizacionValor,
            newItem: PersonalizacionValor
        ): Boolean {
            return oldItem == newItem
        }
    }
}