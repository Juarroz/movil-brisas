package com.example.appinterface.Api.pedidos

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.example.appinterface.R
import com.example.appinterface.Api.pedidos.data.HistorialDTO
import java.text.SimpleDateFormat
import java.util.Locale

class HistorialAdapter(private var historialList: List<HistorialDTO>) :
    RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder>() {

    // 🔥 Mapeo de Estados (Basado en tu lógica de Laravel, adaptado a Android)
    private data class EstadoInfo(
        val nombre: String,
        val colorResId: Int, // Referencia al color de Android (ej: R.color.green_brisas)
        val iconResId: Int // Referencia al Vector Asset (ej: R.drawable.ic_check_circle)
    )

    private val estadoMap = mapOf(
        1 to EstadoInfo("Cotización Pendiente", R.color.gray_dark, R.drawable.ic_check_circle),
        2 to EstadoInfo("Pago Diseño Pendiente", R.color.orange_warning, R.drawable.ic_check_circle),
        3 to EstadoInfo("Diseño en Proceso", R.color.blue_info, R.drawable.ic_edit_square),
        4 to EstadoInfo("Diseño Aprobado", R.color.green_medium, R.drawable.ic_thumb_up),
        5 to EstadoInfo("Tallado (Producción)", R.color.production_process, R.drawable.ic_gear),
        6 to EstadoInfo("Engaste", R.color.production_process, R.drawable.ic_gem),
        7 to EstadoInfo("Pulido", R.color.production_process, R.drawable.ic_sparkle),
        8 to EstadoInfo("Inspección de Calidad", R.color.purple_light, R.drawable.ic_shield_check),
        9 to EstadoInfo("Finalizado (Entrega)", R.color.green_dark, R.drawable.ic_gift),
        10 to EstadoInfo("Cancelado", R.color.red_error, R.drawable.ic_x_circle)
    )

    // Formateador de fechas
    private val dateFormatter = SimpleDateFormat("dd/MMM/yyyy HH:mm", Locale("es", "CO"))

    // ViewHolder
    inner class HistorialViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val timelineLine: View = view.findViewById(R.id.timeline_line)
        val timelineMarker: ImageView = view.findViewById(R.id.timeline_marker)
        val tvEstadoTitle: TextView = view.findViewById(R.id.tvEstadoTitle)
        val tvDateResponsible: TextView = view.findViewById(R.id.tvDateResponsible)
        val tvComentarios: TextView = view.findViewById(R.id.tvComentarios)
        val btnVerImagen: MaterialButton = view.findViewById(R.id.btnVerImagen)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historial, parent, false)
        return HistorialViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistorialViewHolder, position: Int) {
        val item = historialList[position]
        val context = holder.itemView.context
        val info = estadoMap[item.estId] ?: EstadoInfo("Desconocido", R.color.gray_dark, R.drawable.ic_question_circle)

        // 1. Contenido de Texto
        holder.tvEstadoTitle.text = info.nombre
        holder.tvComentarios.text = item.hisComentarios ?: "Sin comentarios."

        // Formatear fecha
        val fecha = try {
            item.hisFechaCambio?.let { dateFormatter.format(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US).parse(it)!!) } ?: "Fecha N/D"
        } catch (e: Exception) {
            "Fecha Inválida"
        }

        holder.tvDateResponsible.text = "$fecha | Por: ${item.responsableNombre ?: "Sistema"}"

        // 2. Estética del Timeline (Línea y Marcador)

        // Color del marcador
        val markerColor = ContextCompat.getColor(context, info.colorResId)

        // Aplicar color al fondo del círculo (que ya tiene la forma en bg_circle_green_brisas)
        holder.timelineMarker.background.setTint(markerColor)

        // Aplicar ícono (Deberás crear los Vector Assets: ic_edit_square, ic_thumb_up, ic_gear, etc.)
        holder.timelineMarker.setImageResource(info.iconResId)

        // Manejar la línea: Ocultar en el último elemento (que está al final del historial)
        if (position == itemCount - 1) {
            holder.timelineLine.visibility = View.INVISIBLE
        } else {
            holder.timelineLine.visibility = View.VISIBLE
        }

        // 3. Botón de Imagen/Evidencia
        if (!item.hisImagen.isNullOrBlank()) {
            holder.btnVerImagen.visibility = View.VISIBLE
            // Configurar el clic para abrir el enlace
            holder.btnVerImagen.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.hisImagen))
                context.startActivity(intent)
            }
        } else {
            holder.btnVerImagen.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = historialList.size

    fun updateList(newList: List<HistorialDTO>) {
        historialList = newList
        notifyDataSetChanged()
    }
}