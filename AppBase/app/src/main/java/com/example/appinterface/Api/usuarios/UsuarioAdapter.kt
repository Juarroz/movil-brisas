package com.example.appinterface.Api.usuarios

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appinterface.R

class UsuarioAdapter(
    private var items: MutableList<UsuarioResponseDTO>,
    private val listener: Listener
) : RecyclerView.Adapter<UsuarioAdapter.UserVH>() {

    interface Listener {
        fun onToggleActivo(user: UsuarioResponseDTO, position: Int)
        fun onEdit(user: UsuarioResponseDTO, position: Int)
        fun onDelete(user: UsuarioResponseDTO, position: Int)
        fun onViewHistory(user: UsuarioResponseDTO, position: Int)
    }

    fun updateList(newItems: List<UsuarioResponseDTO>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun updateItem(position: Int, user: UsuarioResponseDTO) {
        // Comprobamos que la posición sea válida
        if (position >= 0 && position < items.size) {
            items[position] = user
            notifyItemChanged(position) // Notifica solo el cambio de ese ítem
        }
    }

    fun removeItem(position: Int) {
        // Comprobamos que la posición sea válida
        if (position >= 0 && position < items.size) {
            items.removeAt(position)
            notifyItemRemoved(position)

            // Opcional pero recomendado: notifica al recycler que el rango cambió
            // para evitar "IndexOutOfBoundsException" si se elimina rápido.
            notifyItemRangeChanged(position, items.size)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_card, parent, false)
        return UserVH(view)
    }

    override fun onBindViewHolder(holder: UserVH, position: Int) {
        val user = items[position]
        holder.tvName.text = user.nombre
        holder.tvRole.text = user.rolNombre ?: "—"
        holder.tvStatus.text = if (user.activo) "Activo" else "Inactivo"

        holder.btnMore.setOnClickListener { v ->
            showPopup(v, user, position)
        }

        holder.itemView.setOnClickListener {
            // abrir detalle - opcional
            listener.onViewHistory(user, position)
        }
    }

    private fun showPopup(anchor: View, user: UsuarioResponseDTO, position: Int) {
        val popup = PopupMenu(anchor.context, anchor)
        popup.menuInflater.inflate(R.menu.user_item_menu, popup.menu)
        // Ajustar título dinámico para activar/desactivar
        val toggle = popup.menu.findItem(R.id.menu_toggle_active)
        toggle.title = if (user.activo) "Desactivar" else "Activar"

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_toggle_active -> {
                    listener.onToggleActivo(user, position)
                    true
                }
                R.id.menu_edit -> {
                    listener.onEdit(user, position)
                    true
                }
                R.id.menu_delete -> {
                    listener.onDelete(user, position)
                    true
                }
                R.id.menu_history -> {
                    listener.onViewHistory(user, position)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    override fun getItemCount(): Int = items.size

    class UserVH(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvRole: TextView = view.findViewById(R.id.tvRole)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val btnMore: ImageButton = view.findViewById(R.id.btnMore)
    }


}