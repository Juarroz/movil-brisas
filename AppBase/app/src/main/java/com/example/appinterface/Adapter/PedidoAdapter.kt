package com.example.appinterface.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appinterface.Api.PedidoResponseDTO
import com.example.appinterface.R

class PedidoAdapter(private val items: MutableList<PedidoResponseDTO> = mutableListOf()) :
    RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder>() {

    class PedidoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCodigo: TextView = view.findViewById(R.id.tvCodigo)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val tvComentarios: TextView = view.findViewById(R.id.tvComentarios)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pedido, parent, false)
        return PedidoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val p = items[position]
        holder.tvCodigo.text = p.pedCodigo ?: "—"
        holder.tvFecha.text = p.pedFechaCreacion ?: "—"
        holder.tvComentarios.text = p.pedComentarios ?: ""
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<PedidoResponseDTO>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
