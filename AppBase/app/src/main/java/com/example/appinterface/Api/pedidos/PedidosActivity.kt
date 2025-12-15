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
import com.example.appinterface.Api.pedidos.data.PedidoRepository
import androidx.appcompat.widget.Toolbar
import com.example.appinterface.Api.pedidos.data.PedidoDTO
import com.example.appinterface.core.data.SessionManager


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
        // CAMBIAR R.id.topAdminTabLayout por R.id.topAppBar
        val toolbar = findViewById<Toolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar) // Esto configura el Toolbar base como la ActionBar de la Activity

        // Ahora la BaseActivity puede inicializar el resto de la UI
        initCommonUI() // <-- Asegúrate de que esta línea esté aquí si quieres que las barras de rol funcionen

        // El resto de tu lógica se mantiene igual
        inicializarVistas()
        configurarViewModel()
        observarDatos()
        viewModel.cargarPedidos()
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

        // 2. Configurar el RecyclerView y el Clic (Usando las nuevas lambdas de acción)
        // El Adapter ahora maneja tres tipos de clics
        adapter = PedidosAdapter(
            pedidos = emptyList(), // Iniciar con lista vacía
            onClick = { pedidoSeleccionado ->
                // Acción principal: Navegación al detalle
                val intent = Intent(this, PedidoDetailActivity::class.java)
                // Asumo que tu DTO Pedido tiene pedId
                intent.putExtra("EXTRA_ID", pedidoSeleccionado.pedId)
                startActivity(intent)
            },
            onCambiarEstado = { pedidoSeleccionado ->
                // Acción de botón: Abrir diálogo de cambio de estado
                showCambiarEstadoDialog(pedidoSeleccionado)
            },
            onAsignarDisenador = { pedidoSeleccionado ->
                // Acción de botón: Abrir diálogo de asignación de diseñador
                showAsignarDisenadorDialog(pedidoSeleccionado)
            }
        )

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

        viewModel = ViewModelProvider(this, factory).get(PedidosViewModel::class.java)
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

    // Llamarás a esta función desde el click del botón en la Card
    fun showCambiarEstadoDialog(pedido: PedidoDTO) {
        // 💡 NOTA: Asumo que tienes una lista de estados disponibles (StatusDTO)
        // que debes cargar desde tu API una sola vez.

        // Crear el Bundle con la información necesaria
        val bundle = Bundle().apply {
            putInt("PEDIDO_ID", pedido.pedId)
            // Puedes pasar la lista de estados si la tienes
            // putParcelableArrayList("ESTADOS", ArrayList(viewModel.listaEstados.value))
        }

        // Crea el fragmento del diálogo y muéstralo
        val dialog = DialogCambiarEstadoFragment()
        dialog.arguments = bundle
        dialog.show(supportFragmentManager, "CambiarEstadoDialog")
    }

    // CRÍTICO: El diálogo llamará a esta función para ejecutar la acción
    fun ejecutarCambioDeEstado(pedidoId: Int, nuevoEstadoId: Int, comentarios: String) {
        // 💡 Aquí es donde llamarías al ViewModel para ejecutar la acción
        // viewModel.actualizarEstado(pedidoId, nuevoEstadoId, comentarios)
        //     .observe(this, { pedidoActualizado ->
        //         // 1. Mostrar mensaje de éxito
        //         // 2. Recargar la lista de pedidos (o actualizar el ítem en el Adapter)
        //     })

        Toast.makeText(this, "Cambiando Pedido $pedidoId a Estado $nuevoEstadoId...", Toast.LENGTH_LONG).show()
    }
    fun showAsignarDisenadorDialog(pedido: PedidoDTO) {
        // 💡 NOTA: Debes tener una lista de Empleados/Diseñadores para el Spinner.
        if (!sessionManager.isAdmin()) {
            Toast.makeText(this, "Permiso denegado.", Toast.LENGTH_SHORT).show()
            return
        }

        val bundle = Bundle().apply {
            putInt("PEDIDO_ID", pedido.pedId)
            // putParcelableArrayList("EMPLEADOS", ArrayList(viewModel.listaEmpleados.value))
        }

        val dialog = DialogAsignarDisenadorFragment()
        dialog.arguments = bundle
        dialog.show(supportFragmentManager, "AsignarDisenadorDialog")
    }

    // 🔥 CRÍTICO: El diálogo llamará a esta función para ejecutar la acción
    fun ejecutarAsignacion(pedidoId: Int, usuIdEmpleado: Int) {
        // 💡 Aquí es donde llamarías al ViewModel
        // viewModel.asignarDisenador(pedidoId, usuIdEmpleado)
        //     .observe(this, { pedidoActualizado ->
        //         // 1. Mostrar mensaje de éxito
        //         // 2. Recargar la lista
        //     })

        Toast.makeText(this, "Asignando Pedido $pedidoId al Diseñador $usuIdEmpleado...", Toast.LENGTH_LONG).show()
    }

    override fun getCurrentTabIndex(): Int? {
        val sessionManager = SessionManager(this)
        return when {
            // Si es Admin, "Pedidos" es el tercer tab (Index 2)
            sessionManager.isAdmin() -> 2
            // Si es Diseñador, "Pedidos" es el primer tab (Index 0)
            sessionManager.isDesigner() || sessionManager.isLoggedIn() -> 0
            else -> null
        }
    }


}