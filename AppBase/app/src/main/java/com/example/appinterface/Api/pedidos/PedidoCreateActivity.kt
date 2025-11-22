package com.example.appinterface.Api.pedidos

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.appinterface.R
import com.example.appinterface.core.BaseActivity
import com.example.appinterface.core.RetrofitInstance
import com.example.appinterface.core.data.PedidoRepository
import com.example.appinterface.core.model.PedidoRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PedidoCreateActivity : BaseActivity() {

    private lateinit var etCodigo: EditText
    private lateinit var etPerId: EditText
    private lateinit var etEmpleadoId: EditText
    private lateinit var etComentarios: EditText
    private lateinit var btnCrear: Button

    private lateinit var repository: PedidoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedido_create)

        // 1. Inicializar Repositorio
        repository = PedidoRepository(RetrofitInstance.api2kotlin)

        // 2. Vincular Vistas
        etCodigo = findViewById(R.id.etCrearCodigo)
        etPerId = findViewById(R.id.etCrearPerId)
        etEmpleadoId = findViewById(R.id.etCrearEmpleadoId)
        etComentarios = findViewById(R.id.etCrearComentarios)
        btnCrear = findViewById(R.id.btnCrearPedido)

        // 3. Configurar Acción
        btnCrear.setOnClickListener {
            crearNuevoPedido()
        }
    }

    private fun crearNuevoPedido() {
        // Validación y preparación de datos
        val codigo = etCodigo.text.toString()
        val perIdText = etPerId.text.toString()

        if (codigo.isBlank() || perIdText.isBlank()) {
            Toast.makeText(this, "Código e ID Personalización son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val perId = perIdText.toIntOrNull()
        val empleadoId = etEmpleadoId.text.toString().toIntOrNull()
        val comentarios = etComentarios.text.toString()

        // El pedido siempre se crea en estado 1 ('diseño' según tu BD)
        val estadoInicial = 1

        val request = PedidoRequest(
            codigo = codigo,
            comentarios = comentarios,
            estadoId = estadoInicial,
            personaId = perId,
            usuarioId = empleadoId
        )

        // Llamada al servidor
        CoroutineScope(Dispatchers.IO).launch {
            val resultado = repository.crearPedido(request)

            withContext(Dispatchers.Main) {
                if (resultado.isSuccess) {
                    Toast.makeText(this@PedidoCreateActivity, "¡Pedido Creado con Éxito!", Toast.LENGTH_LONG).show()
                    finish() // Cierra la pantalla
                } else {
                    Toast.makeText(this@PedidoCreateActivity, "Error al crear: verifica datos y IDs.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}