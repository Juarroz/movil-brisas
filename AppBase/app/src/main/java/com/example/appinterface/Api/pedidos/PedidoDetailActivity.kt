package com.example.appinterface.Api.pedidos

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity // Importante: Usamos AppCompatActivity o BaseActivity
import com.example.appinterface.R
import com.example.appinterface.core.BaseActivity // Heredar de BaseActivity para mantener sesión
import com.example.appinterface.core.RetrofitInstance
import com.example.appinterface.core.data.PedidoRepository
import com.example.appinterface.core.model.PedidoRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PedidoDetailActivity : BaseActivity() { // Heredamos de BaseActivity

    private lateinit var etCodigo: EditText
    private lateinit var etComentarios: EditText
    private lateinit var spinnerEstado: Spinner
    private lateinit var btnGuardar: Button
    private lateinit var btnEliminar: Button

    // Variables para datos
    private var pedidoId: Int = 0
    private lateinit var repository: PedidoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedido_detail)

        // 1. Inicializar Repositorio
        val api = RetrofitInstance.api2kotlin
        repository = PedidoRepository(api)

        // 2. Vincular Vistas
        etCodigo = findViewById(R.id.etCodigoDetalle)
        etComentarios = findViewById(R.id.etComentariosDetalle)
        spinnerEstado = findViewById(R.id.spinnerEstado)
        btnGuardar = findViewById(R.id.btnGuardarCambios)
        btnEliminar = findViewById(R.id.btnEliminarPedido)

        // 3. Configurar Spinner (Lista de estados)
        configurarSpinner()

        // 4. Cargar datos recibidos de la lista anterior
        cargarDatosDelIntent()

        // 5. Configurar Botones
        btnGuardar.setOnClickListener { guardarCambios() }
        btnEliminar.setOnClickListener { confirmarEliminar() }
    }

    private fun configurarSpinner() {
        val estados = arrayOf("diseño", "tallado", "engaste", "pulido", "finalizado", "cancelado")
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

        // Seleccionar el estado correcto en el spinner
        // Si estadoId es 1, seleccionamos posición 0
        if (estadoId > 0 && estadoId <= spinnerEstado.adapter.count) {
            spinnerEstado.setSelection(estadoId - 1)
        }
    }

    private fun guardarCambios() {
        val nuevoEstadoId = spinnerEstado.selectedItemPosition + 1 // +1 porque el index empieza en 0
        val nuevosComentarios = etComentarios.text.toString()
        val codigoActual = etCodigo.text.toString()

        // Crear el objeto para enviar
        val request = PedidoRequest(
            codigo = codigoActual,
            comentarios = nuevosComentarios,
            estadoId = nuevoEstadoId,
            personaId = null, // No cambiamos esto
            usuarioId = null  // No cambiamos esto
        )

        // Llamada al servidor (Usamos Corutinas)
        CoroutineScope(Dispatchers.IO).launch {
            val resultado = repository.actualizarPedido(pedidoId, request)

            withContext(Dispatchers.Main) {
                if (resultado.isSuccess) {
                    Toast.makeText(this@PedidoDetailActivity, "¡Pedido Actualizado!", Toast.LENGTH_SHORT).show()
                    finish() // Cierra la pantalla y vuelve a la lista
                } else {
                    Toast.makeText(this@PedidoDetailActivity, "Error al guardar", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun confirmarEliminar() {
        AlertDialog.Builder(this)
            .setTitle("¿Eliminar Pedido?")
            .setMessage("Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                // Llamada para eliminar
                CoroutineScope(Dispatchers.IO).launch {
                    val resultado = repository.eliminarPedido(pedidoId)
                    withContext(Dispatchers.Main) {
                        if (resultado.isSuccess) {
                            Toast.makeText(this@PedidoDetailActivity, "Pedido eliminado", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@PedidoDetailActivity, "Error al eliminar", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}