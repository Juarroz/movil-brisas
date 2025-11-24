package com.example.appinterface.Api.pedidos

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
// Importaciones necesarias
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.appinterface.R
import com.example.appinterface.core.BaseActivity
import com.example.appinterface.core.RetrofitInstance
import com.example.appinterface.Api.pedidos.data.data.PedidoRepository
import androidx.appcompat.widget.Toolbar // Aseguramos el import

class PedidosActivity : BaseActivity() {

    // Referencias a la interfaz (UI)
    private lateinit var rvPedidos: RecyclerView
    private lateinit var progressPedidos: ProgressBar
    private lateinit var tvEmptyPedidos: TextView
    private lateinit var swipeRefresh: SwipeRefreshLayout

    // Referencias a la arquitectura
    private lateinit var adapter: PedidosAdapter
    private lateinit var viewModel: PedidosViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedidos)

        // 👇 SOLUCIÓN: CONECTAR EL TOOLBAR Y ACTIVAR EL MENÚ
        val toolbar = findViewById<Toolbar>(R.id.toolbar_pedidos)
        setSupportActionBar(toolbar)

        inicializarVistas()
        configurarViewModel()
        observarDatos()

        // Cargar datos iniciales
        viewModel.cargarPedidos()
    }

    override fun getCurrentTabIndex(): Int? {
        return 2
    }

    override fun onResume() {
        super.onResume()
        viewModel.cargarPedidos()
    }

    private fun inicializarVistas() {
        // Vinculación de vistas
        rvPedidos = findViewById(R.id.rvPedidos)
        progressPedidos = findViewById(R.id.progressPedidos)
        tvEmptyPedidos = findViewById(R.id.tvEmptyPedidos)

        // 1. Configurar SwipeRefresh
        swipeRefresh = findViewById(R.id.swipeRefresh)
        swipeRefresh.setOnRefreshListener {
            viewModel.cargarPedidos()
            swipeRefresh.isRefreshing = false
        }

        // 2. Configurar el RecyclerView y el Clic
        adapter = PedidosAdapter { pedidoSeleccionado ->
            val intent = Intent(this, PedidoDetailActivity::class.java)
            // Empaquetamos los datos para enviarlos
            intent.putExtra("EXTRA_ID", pedidoSeleccionado.id)
            intent.putExtra("EXTRA_CODIGO", pedidoSeleccionado.codigo)
            intent.putExtra("EXTRA_COMENTARIOS", pedidoSeleccionado.comentarios)
            intent.putExtra("EXTRA_ESTADO_ID", pedidoSeleccionado.estadoId)

            startActivity(intent)
        }

        rvPedidos.layoutManager = LinearLayoutManager(this)
        rvPedidos.adapter = adapter
    }

    private fun configurarViewModel() {
        val api = RetrofitInstance.api2kotlin
        val repository = PedidoRepository(api)
        val factory = PedidosViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory)[PedidosViewModel::class.java]
    }

    private fun observarDatos() {
        viewModel.isLoading.observe(this) { cargando ->
            progressPedidos.visibility = if (cargando) View.VISIBLE else View.GONE
            if (cargando) tvEmptyPedidos.visibility = View.GONE
        }

        viewModel.pedidos.observe(this) { resultado ->
            resultado.onSuccess { lista ->
                if (lista.isEmpty()) {
                    tvEmptyPedidos.visibility = View.VISIBLE
                    rvPedidos.visibility = View.GONE
                } else {
                    tvEmptyPedidos.visibility = View.GONE
                    rvPedidos.visibility = View.VISIBLE
                    adapter.updateList(lista)
                }
            }.onFailure { error ->
                tvEmptyPedidos.visibility = View.VISIBLE
                tvEmptyPedidos.text = "Error de conexión"
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // 3. Manejo del menú: Muestra el ícono "+"
    override fun onCreateOptionsMenu(menu: android.view.Menu): Boolean {
        menuInflater.inflate(R.menu.menu_pedidos, menu)
        return true
    }

    // 4. Manejo del click en el ícono "+"
    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_crear_pedido -> {
                // Ya no necesitamos la prueba, el código de navegación se ejecuta
                val intent = android.content.Intent(this, PedidoCreateActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}