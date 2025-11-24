package com.example.appinterface.Api.pedidos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appinterface.R
import com.example.appinterface.Api.pedidos.model.Pedido
import android.graphics.Color

class PedidosAdapter(
    private var listaPedidos: List<Pedido> = emptyList(),
    private val onPedidoClick: (Pedido) -> Unit
) : RecyclerView.Adapter<PedidosAdapter.PedidoViewHolder>() {

    class PedidoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCodigo: TextView = view.findViewById(R.id.tvCodigo)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val tvComentarios: TextView = view.findViewById(R.id.tvComentarios)
        val tvEstadoBadge: TextView = view.findViewById(R.id.tvEstadoBadge)

        fun bind(pedido: Pedido, onClick: (Pedido) -> Unit) {

            val fechaLimpia = pedido.fechaCreacion?.take(10) ?: "Sin fecha"
            tvFecha.text = fechaLimpia
            tvCodigo.text = "Pedido: ${pedido.codigo ?: "S/N"}"
            tvComentarios.text = pedido.comentarios ?: "Sin comentarios"

            // 3. LÓGICA DE ESTADO BASADA EN IDs DE BD
            when (pedido.estadoId) {
                1 -> { // diseño
                    tvEstadoBadge.text = "DISEÑO"
                    tvEstadoBadge.setBackgroundColor(Color.parseColor("#42A5F5")) // Azul
                }
                2, 3, 4 -> { // tallado, engaste, pulido (En Proceso)
                    val estadoNombre = when (pedido.estadoId) {
                        2 -> "TALLADO"
                        3 -> "ENGASTE"
                        4 -> "PULIDO"
                        else -> "PROCESO"
                    }
                    tvEstadoBadge.text = estadoNombre
                    tvEstadoBadge.setBackgroundColor(Color.parseColor("#FFB300")) // Naranja
                }
                5 -> { // finalizado
                    tvEstadoBadge.text = "FINALIZADO"
                    tvEstadoBadge.setBackgroundColor(Color.parseColor("#4CAF50")) // Verde
                }
                6 -> { // cancelado
                    tvEstadoBadge.text = "CANCELADO"
                    tvEstadoBadge.setBackgroundColor(Color.parseColor("#D32F2F")) // Rojo
                }
                else -> { // Desconocido
                    tvEstadoBadge.text = "N/D"
                    tvEstadoBadge.setBackgroundColor(Color.GRAY)
                }
            }

            itemView.setOnClickListener { onClick(pedido) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pedido, parent, false)
        return PedidoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        holder.bind(listaPedidos[position], onPedidoClick)
    }

    override fun getItemCount(): Int = listaPedidos.size

    fun updateList(nuevaLista: List<Pedido>) {
        listaPedidos = nuevaLista
        notifyDataSetChanged()
    }
}