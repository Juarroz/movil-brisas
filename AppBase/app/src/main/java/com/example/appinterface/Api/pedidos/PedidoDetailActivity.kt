package com.example.appinterface.Api.pedidos

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.appinterface.R
import com.example.appinterface.core.BaseActivity
import com.example.appinterface.core.RetrofitInstance
import com.example.appinterface.Api.pedidos.data.data.PedidoRepository
import com.example.appinterface.Api.pedidos.model.PedidoRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.appcompat.widget.Toolbar

class PedidoDetailActivity : BaseActivity() {

    private lateinit var etCodigo: EditText
    private lateinit var etComentarios: EditText
    private lateinit var spinnerEstado: Spinner
    private lateinit var btnGuardar: Button
    private lateinit var btnEliminar: Button

    private var pedidoId: Int = 0
    private lateinit var repository: PedidoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedido_detail)

        // 🔥 1A. Configurar la Toolbar base (que está en top_app_bar)
        // La Toolbar es necesaria para el título y para que initCommonUI funcione bien.
        val toolbar = findViewById<Toolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)

        // 🔥 1B. Llamar a initCommonUI para manejar la visibilidad de top_admin_bar/top_user_bar
        initCommonUI()

        // 1. Inicializar Repositorio
        val api = RetrofitInstance.api2kotlin
        val sessionManager = RetrofitInstance.getSessionManager()
        repository = PedidoRepository(api, sessionManager)

        // 2. Vincular Vistas
        etCodigo = findViewById(R.id.etCodigoDetalle)
        etComentarios = findViewById(R.id.etComentariosDetalle)
        spinnerEstado = findViewById(R.id.spinnerEstado)
        btnGuardar = findViewById(R.id.btnGuardarCambios)
        btnEliminar = findViewById(R.id.btnEliminarPedido)

        // 3. Configurar Spinner (Lista de estados)
        configurarSpinner()

        // 4. Cargar datos
        cargarDatosDelIntent()

        // 5. CRÍTICO: Configurar restricciones de UI basadas en el rol
        setupRoleRestrictions()

        // 6. Configurar Acciones
        btnGuardar.setOnClickListener { guardarCambios() }
        btnEliminar.setOnClickListener { confirmarEliminar() }
    }

    private fun configurarSpinner() {
        // La lista de estados debe ser inmutable y en el orden de IDs de la BD (1-6)
        val estados = arrayOf("Diseño", "Tallado", "Engaste", "Pulido", "Finalizado", "Cancelado")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, estados)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEstado.adapter = adapter
    }

    private fun cargarDatosDelIntent() {
        pedidoId = intent.getIntExtra("EXTRA_ID", 0)
        val codigo = intent.getStringExtra("EXTRA_CODIGO") ?: ""
        val comentarios = intent.getStringExtra("EXTRA_COMENTARIOS") ?: ""
        val estadoId = intent.getIntExtra("EXTRA_ESTADO_ID", 1)

        etCodigo.setText(codigo)
        etComentarios.setText(comentarios)

        // Seleccionar el estado correcto en el spinner (ID - 1)
        if (estadoId > 0 && estadoId <= spinnerEstado.adapter.count) {
            spinnerEstado.setSelection(estadoId - 1)
        }
    }

    // 🔥 NUEVA FUNCIÓN PARA RESTRINGIR ACCIONES SEGÚN EL ROL
    private fun setupRoleRestrictions() {
        val roles = sessionManager.getRoles()
        val isClient = roles.contains("ROLE_USUARIO")
        val isAdminOrDesigner = roles.contains("ROLE_ADMINISTRADOR") || roles.contains("ROLE_DISEÑADOR")

        // El cliente (USUARIO) NO puede modificar el estado, eliminar, o editar.
        if (isClient) {
            btnGuardar.visibility = View.GONE
            btnEliminar.visibility = View.GONE
            spinnerEstado.isEnabled = false // Deshabilitar cambio de estado
            etComentarios.isEnabled = false // Deshabilitar edición de comentarios

            // Opcional: Mostrar un mensaje si es solo vista.
            Toast.makeText(this, "Modificaciones no permitidas para clientes.", Toast.LENGTH_SHORT).show()
        } else if (isAdminOrDesigner) {
            // El diseñador/administrador PUEDE actualizar el estado y los comentarios.
            btnGuardar.visibility = View.VISIBLE
            spinnerEstado.isEnabled = true
            etComentarios.isEnabled = true

            // Solo el administrador puede eliminar
            btnEliminar.visibility = if (roles.contains("ROLE_ADMINISTRADOR")) View.VISIBLE else View.GONE
        } else {
            // Rol desconocido (seguridad)
            btnGuardar.visibility = View.GONE
            btnEliminar.visibility = View.GONE
        }
    }

    private fun guardarCambios() {
        val nuevoEstadoId = spinnerEstado.selectedItemPosition + 1
        val nuevosComentarios = etComentarios.text.toString()
        val codigoActual = etCodigo.text.toString()

        // Validar si el usuario tiene permiso (aunque la UI lo oculte, es buena práctica)
        if (!sessionManager.getRoles().any { it == "ROLE_ADMINISTRADOR" || it == "ROLE_DISEÑADOR" }) {
            Toast.makeText(this, "Acción no autorizada.", Toast.LENGTH_SHORT).show()
            return
        }

        val request = PedidoRequest(
            codigo = codigoActual,
            comentarios = nuevosComentarios,
            estadoId = nuevoEstadoId,
            personaId = null,
            usuarioId = null
        )

        CoroutineScope(Dispatchers.IO).launch {
            val resultado = repository.actualizarPedido(pedidoId, request)

            withContext(Dispatchers.Main) {
                if (resultado.isSuccess) {
                    Toast.makeText(this@PedidoDetailActivity, "¡Pedido Actualizado!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@PedidoDetailActivity, "Error al guardar. ${resultado.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun confirmarEliminar() {
        // Validar si el usuario tiene permiso (solo admin puede eliminar)
        if (!sessionManager.isAdmin()) {
            Toast.makeText(this, "Solo los administradores pueden eliminar pedidos.", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("¿Eliminar Pedido?")
            .setMessage("Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    val resultado = repository.eliminarPedido(pedidoId)
                    withContext(Dispatchers.Main) {
                        if (resultado.isSuccess) {
                            Toast.makeText(this@PedidoDetailActivity, "Pedido eliminado", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@PedidoDetailActivity, "Error al eliminar. ${resultado.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}