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
import androidx.appcompat.widget.Toolbar


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

        // 1. Configurar Toolbar
        // Asumiendo que R.id.topAdminTabLayout es el ID de la Toolbar en activity_pedidos.xml
        val toolbar = findViewById<Toolbar>(R.id.topAdminTabLayout)
        setSupportActionBar(toolbar)

        inicializarVistas()
        configurarViewModel()

        // 🔥 Llamada para adaptar el título ANTES de observar datos
        setupRoleUI()

        observarDatos()

        // Cargar datos iniciales
        viewModel.cargarPedidos()
    }

    override fun getCurrentTabIndex(): Int? {
        return 2 // Índice de la pestaña de Pedidos en Admin Tabs
    }

    override fun onResume() {
        super.onResume()
        // Recargar la lista cada vez que volvemos a esta actividad
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
            // Opcional: Pasar ID del cliente/empleado para referencia en Detail
            // intent.putExtra("EXTRA_CLIENTE_ID", pedidoSeleccionado.personaId)
            // intent.putExtra("EXTRA_EMPLEADO_ID", pedidoSeleccionado.usuarioId)

            startActivity(intent)
        }

        rvPedidos.layoutManager = LinearLayoutManager(this)
        rvPedidos.adapter = adapter
    }

    private fun configurarViewModel() {
        // 1. Obtener instancias necesarias
        val api = RetrofitInstance.api2kotlin
        // 🔥 OBTENER EL GESTOR DE SESIÓN
        val sessionManager = RetrofitInstance.getSessionManager()

        // 2. Crear el Repositorio con todas sus dependencias
        val repository = PedidoRepository(api, sessionManager) // ✅ Corregido

        // 3. Crear el ViewModel
        val factory = PedidosViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory)[PedidosViewModel::class.java]
    }

    // 🔥 MÉTODO PARA OBTENER EL ROL PRINCIPAL (usado para mensajes)
    private fun getCurrentRole(): String {
        // Devuelve "ROLE_ADMINISTRADOR", "ROLE_DISEÑADOR", "ROLE_USUARIO", o "ANONYMOUS"
        // Si el usuario tiene múltiples roles, toma el primero (Admin es el más importante)
        return sessionManager.getRoles().firstOrNull() ?: "ANONYMOUS"
    }

    // 🔥 MÉTODO PARA ADAPTAR EL TÍTULO AL ROL
    private fun setupRoleUI() {
        val role = getCurrentRole()

        // Determinar el título basado en el rol que filtra los pedidos
        val titleText = when (role) {
            "ROLE_ADMINISTRADOR" -> "GESTIÓN: Todos los Pedidos"
            "ROLE_DISEÑADOR" -> "MIS PEDIDOS ASIGNADOS"
            "ROLE_USUARIO" -> "MIS PEDIDOS"
            else -> "Pedidos (Inválido)"
        }

        // Establecer el título de la actividad
        supportActionBar?.title = titleText
    }

    // 🔥 ÚNICA VERSIÓN DEL MÉTODO OBSERVARDATOS (CORREGIDO)
    private fun observarDatos() {
        // 1. Observar estado de carga
        viewModel.isLoading.observe(this) { cargando ->
            progressPedidos.visibility = if (cargando) View.VISIBLE else View.GONE
            // Si está cargando, ocultamos la lista vacía para evitar parpadeos
            if (cargando) tvEmptyPedidos.visibility = View.GONE
        }

        // 2. Observar lista de pedidos (éxito)
        viewModel.pedidos.observe(this) { lista ->
            val hasError = viewModel.errorMessage.value != null

            if (lista.isEmpty() && !hasError) {
                // Lista vacía y SIN errores de red/API
                tvEmptyPedidos.visibility = View.VISIBLE
                rvPedidos.visibility = View.GONE

                // Mensaje adaptado
                tvEmptyPedidos.text = if (getCurrentRole() == "ROLE_USUARIO")
                    "Aún no has creado ningún pedido."
                else
                    "No hay pedidos asignados en esta vista."
            } else if (!lista.isEmpty()) {
                // Lista no vacía, mostrar datos
                tvEmptyPedidos.visibility = View.GONE
                rvPedidos.visibility = View.VISIBLE
                adapter.updateList(lista)
            }
            // Si hay error, el punto 3 se encarga de mostrar el mensaje.
        }

        // 3. Observar errores de la API (desde PedidosViewModel)
        viewModel.errorMessage.observe(this) { errorMsg ->
            if (errorMsg != null) {
                tvEmptyPedidos.visibility = View.VISIBLE
                rvPedidos.visibility = View.GONE
                tvEmptyPedidos.text = errorMsg
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
            }
        }

        // 4. Observar mensajes de CRUD exitosos (actualizar/eliminar)
        viewModel.operacionExitosa.observe(this) { msg ->
            msg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.limpiarMensaje()
            }
        }
    }

    // 5. Controlar la visibilidad del botón de CREAR
    override fun onCreateOptionsMenu(menu: android.view.Menu): Boolean {
        menuInflater.inflate(R.menu.menu_pedidos, menu)

        // Ocultar el botón "+" si no es administrador (o si es cliente/diseñador)
        val createItem = menu.findItem(R.id.action_crear_pedido)
        if (createItem != null) {
            // Asumimos que solo el administrador puede crear pedidos manualmente
            createItem.isVisible = sessionManager.isAdmin()
        }

        return true
    }

    // 6. Manejo del click en el ícono "+"
    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_crear_pedido -> {
                val intent = Intent(this, PedidoCreateActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}