package com.example.appinterface.Api.pedidos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.example.appinterface.R
import com.example.appinterface.core.RetrofitInstance
import android.graphics.Color
import com.example.appinterface.Api.pedidos.data.PedidoDTO
import com.example.appinterface.core.data.SessionManager

class PedidosAdapter(
    private var pedidos: List<PedidoDTO>,
    private val onClick: (PedidoDTO) -> Unit, // Para ir al detalle
    private val onCambiarEstado: (PedidoDTO) -> Unit, // Abre DialogCambiarEstadoFragment
    private val onAsignarDisenador: (PedidoDTO) -> Unit // Abre DialogAsignarDisenadorFragment
) : RecyclerView.Adapter<PedidosAdapter.PedidoViewHolder>() {

    private val sessionManager: SessionManager? by lazy {
        try {
            // Intentar obtener la instancia. Si RetrofitInstance no está lista, puede lanzar excepción.
            RetrofitInstance.getSessionManager()
        } catch (e: Exception) {
            // Manejar la excepción si la instancia de Retrofit falla
            null
        }
    }

    // Ahora accedemos a los roles de forma segura (tolerando null)
    private val isAdmin: Boolean
        get() = sessionManager?.isAdmin() ?: false

    private val isDesigner: Boolean
        get() = sessionManager?.isDesigner() ?: false

    private val currentUserId: Int?
        get() = sessionManager?.getUserId()

    class PedidoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Renombré tvEstadoBadge a tvEstado para coincidir con el XML item_pedido.xml
        val tvCodigo: TextView = view.findViewById(R.id.tvCodigo)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val tvCliente: TextView = view.findViewById(R.id.tvCliente) // Nuevo
        val tvDisenador: TextView = view.findViewById(R.id.tvDisenador) // Nuevo
        val tvEstado: TextView = view.findViewById(R.id.tvEstado) // Nuevo ID
        // Botones de acción
        val btnAsignar: MaterialButton = view.findViewById(R.id.btnAsignarDisenadorCard)
        val btnCambiarEstado: MaterialButton = view.findViewById(R.id.btnCambiarEstadoCard)
        val btnVerDetalle: MaterialButton = view.findViewById(R.id.btnVerDetalle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pedido, parent, false)
        return PedidoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val pedido = pedidos[position]
        val context = holder.itemView.context

        // 1. Datos e Identificación
        holder.tvCodigo.text = "#${pedido.pedCodigo ?: "S/N"}"
        holder.tvFecha.text = pedido.pedFechaCreacion?.take(10) ?: "Sin fecha"

        // Asumo que tu DTO Pedido tiene nombreCliente y nombreEmpleado
        holder.tvCliente.text = pedido.nombreCliente ?: "Cliente Desconocido"
        holder.tvDisenador.text = pedido.nombreEmpleado ?: "PENDIENTE ASIGNAR"

        // 2. LÓGICA DE ESTADO (Usando tus IDs y colores)
        val estadoNombre = pedido.estadoNombre?.replace('_', ' ')?.uppercase() ?: "N/D"
        holder.tvEstado.text = estadoNombre

        val estadoColor: Int
        val textColor: Int

        // Mapeo de Colores basado en tu lógica anterior y tu paleta
        when (pedido.estId) {
            1 -> { // Cotización Pendiente (Usando un color que destaque el 'pendiente')
                estadoColor = ContextCompat.getColor(context, R.color.green_medium)
                textColor = ContextCompat.getColor(context, R.color.white)
            }
            3, 4, 5, 6, 7, 8 -> { // En Proceso (Diseño, Tallado, etc. - Usamos el color principal)
                estadoColor = ContextCompat.getColor(context, R.color.green_brisas)
                textColor = ContextCompat.getColor(context, R.color.white)
            }
            9 -> { // Finalizado/Entregado
                estadoColor = ContextCompat.getColor(context, R.color.green_dark)
                textColor = ContextCompat.getColor(context, R.color.white)
            }
            10 -> { // Cancelado
                estadoColor = ContextCompat.getColor(context, R.color.gray_dark)
                textColor = ContextCompat.getColor(context, R.color.white)
            }
            else -> { // Desconocido/Otros
                estadoColor = Color.GRAY
                textColor = Color.WHITE
            }
        }

        // Aplicar el color de fondo (asumo que bg_status_rounded es un drawable de forma simple)
        holder.tvEstado.background.setTint(estadoColor)
        holder.tvEstado.setTextColor(textColor)


        // 3. Lógica de Roles y Acciones

        // Asignar: Solo Administrador
        if (isAdmin) {
            holder.btnAsignar.visibility = View.VISIBLE
            holder.btnAsignar.setOnClickListener { onAsignarDisenador(pedido) }
        } else {
            holder.btnAsignar.visibility = View.GONE
        }

        // Cambiar Estado: Admin O Diseñador asignado
        val puedeCambiarEstado = isAdmin || (isDesigner && pedido.usuIdEmpleado == currentUserId && currentUserId != null)

        if (puedeCambiarEstado) {
            holder.btnCambiarEstado.visibility = View.VISIBLE
            holder.btnCambiarEstado.setOnClickListener { onCambiarEstado(pedido) }
        } else {
            holder.btnCambiarEstado.visibility = View.GONE
        }

        holder.btnVerDetalle.visibility = View.VISIBLE
        holder.btnVerDetalle.setOnClickListener { onClick(pedido) }
    }

    override fun getItemCount(): Int = pedidos.size

    fun updateList(newList: List<PedidoDTO>) {
        pedidos = newList
        notifyDataSetChanged()
    }
}