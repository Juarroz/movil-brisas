package com.example.appinterface.Api.pedidos

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appinterface.R
import com.example.appinterface.Api.pedidos.data.PedidoRepository
import com.example.appinterface.Api.pedidos.data.PedidoRequestDTO
import com.example.appinterface.core.BaseActivity
import com.example.appinterface.core.RetrofitInstance

class PedidoDetailActivity : BaseActivity() {

    // Vistas de Edición y Acciones
    private lateinit var tvPedidoCodigo: TextView
    private lateinit var tvFechaCliente: TextView
    private lateinit var etComentarios: EditText
    private lateinit var spinnerEstado: Spinner
    private lateinit var btnGuardar: Button
    private lateinit var btnEliminar: Button

    // Vistas del Timeline
    private lateinit var rvHistorial: RecyclerView
    private lateinit var tvHistorialEmpty: TextView

    // Arquitectura
    private lateinit var viewModel: PedidoDetailViewModel
    private lateinit var historialAdapter: HistorialAdapter
    private var pedidoId: Int = 0

    // Mapeo de IDs de estado para el Spinner
    private val estadosArray = listOf(
        "1. Cotización Pendiente", "2. Pago Diseño Pendiente", "3. Diseño en Proceso",
        "4. Diseño Aprobado", "5. Tallado (Producción)", "6. Engaste",
        "7. Pulido", "8. Inspección de Calidad", "9. Finalizado", "10. Cancelado"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedido_detail)

        // 1. Setup inicial
        val toolbar = findViewById<Toolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)
        initCommonUI() // Configura las barras de rol

        // 2. Obtener ID y configurar arquitectura
        pedidoId = intent.getIntExtra("EXTRA_ID", 0)
        configurarViewModel()

        // 3. Vincular y configurar Vistas
        inicializarVistas()
        configurarSpinner()
        configurarTimeline()

        // 4. Observar datos
        observarDatos()

        // 5. Iniciar carga
        if (pedidoId > 0) {
            viewModel.start(pedidoId)
        } else {
            Toast.makeText(this, "Error: ID de pedido no válido.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun configurarViewModel() {
        val api = RetrofitInstance.api2kotlin
        val sessionManager = RetrofitInstance.getSessionManager()
        val repository = PedidoRepository(api, sessionManager)
        val factory = PedidoDetailViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(PedidoDetailViewModel::class.java)
    }

    private fun inicializarVistas() {
        // Vistas de Detalle
        tvPedidoCodigo = findViewById(R.id.tvPedidoCodigo)
        tvFechaCliente = findViewById(R.id.tvFechaCliente)
        etComentarios = findViewById(R.id.etComentariosDetalle)
        spinnerEstado = findViewById(R.id.spinnerEstado)
        btnGuardar = findViewById(R.id.btnGuardarCambios)
        btnEliminar = findViewById(R.id.btnEliminarPedido)

        // Vistas del Timeline
        rvHistorial = findViewById(R.id.rvHistorial)
        tvHistorialEmpty = findViewById(R.id.tvHistorialEmpty)

        // Acciones
        btnGuardar.setOnClickListener { guardarCambios() }
        btnEliminar.setOnClickListener { confirmarEliminar() }
    }

    private fun configurarSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, estadosArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEstado.adapter = adapter
    }

    private fun configurarTimeline() {
        historialAdapter = HistorialAdapter(emptyList())
        rvHistorial.layoutManager = LinearLayoutManager(this)
        rvHistorial.adapter = historialAdapter
    }

    private fun observarDatos() {
        // 1. Observar el Pedido principal
        viewModel.pedido.observe(this) { pedido ->
            if (pedido != null) {
                // Rellenar datos de edición
                tvPedidoCodigo.text = "Pedido #${pedido.pedCodigo}"
                tvFechaCliente.text = "Cliente: ${pedido.nombreCliente ?: "N/D"} | Creado: ${pedido.pedFechaCreacion?.take(10) ?: "N/D"}"
                etComentarios.setText(pedido.pedComentarios)

                // Seleccionar estado (el ID 1 es la posición 0 del array)
                val estadoId = pedido.estId ?: 1
                if (estadoId > 0 && estadoId <= estadosArray.size) {
                    spinnerEstado.setSelection(estadoId - 1)
                }
                setupRoleRestrictions(pedido.usuIdEmpleado)
            } else {
                tvPedidoCodigo.text = "Pedido no encontrado"
            }
        }

        // 2. Observar el Historial (Timeline)
        viewModel.historial.observe(this) { historial ->
            historialAdapter.updateList(historial)
            tvHistorialEmpty.visibility = if (historial.isEmpty()) View.VISIBLE else View.GONE
        }

        // 3. Observar mensajes de éxito (Guardar/Eliminar)
        viewModel.operacionExitosa.observe(this) { msg ->
            msg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.clearMessages()

                // Si la operación fue eliminar, cerrar la actividad
                if (it.contains("eliminado")) {
                    finish()
                }
            }
        }

        // 4. Observar errores
        viewModel.errorMessage.observe(this) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.clearMessages()
            }
        }
    }

    // 🔥 FUNCIÓN ACTUALIZADA para usar los datos cargados
    private fun setupRoleRestrictions(empleadoId: Int?) {
        val roles = sessionManager.getRoles()
        val isClient = roles.contains("ROLE_USUARIO")
        val isAdmin = roles.contains("ROLE_ADMINISTRADOR")
        val currentUserId = sessionManager.getUserId()

        // El diseñador solo puede gestionar si está asignado
        val isAssignedDesigner = roles.contains("ROLE_DISEÑADOR") && currentUserId == empleadoId

        val canEdit = isAdmin || isAssignedDesigner

        // Restricciones para la edición
        if (canEdit) {
            btnGuardar.visibility = View.VISIBLE
            spinnerEstado.isEnabled = true
            etComentarios.isEnabled = true
            btnEliminar.visibility = if (isAdmin) View.VISIBLE else View.GONE // Solo Admin elimina
        } else {
            // Cliente o Diseñador no asignado
            btnGuardar.visibility = View.GONE
            btnEliminar.visibility = View.GONE
            spinnerEstado.isEnabled = false
            etComentarios.isEnabled = false

            if (isClient) {
                Toast.makeText(this, "Modificaciones no permitidas para clientes.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun guardarCambios() {
        // ID del estado es la posición + 1
        val nuevoEstadoId = spinnerEstado.selectedItemPosition + 1
        val nuevosComentarios = etComentarios.text.toString()
        val pedidoActual = viewModel.pedido.value

        if (pedidoActual == null) return

        val request = PedidoRequestDTO(
            codigo = pedidoActual.pedCodigo !!,
            comentarios = nuevosComentarios,
            estadoId = nuevoEstadoId,

            personaId = pedidoActual.perId,
            usuarioId = pedidoActual.usuIdCliente
        )

        viewModel.guardarCambios(request)
    }

    private fun confirmarEliminar() {
        if (!sessionManager.isAdmin()) {
            Toast.makeText(this, "Solo los administradores pueden eliminar pedidos.", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("¿Eliminar Pedido?")
            .setMessage("Esta acción es permanente y no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.eliminarPedido()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}