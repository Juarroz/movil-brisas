package com.example.appinterface.Api.personalizacion

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GestureDetectorCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.appinterface.R
import com.example.appinterface.core.BaseActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * Actividad para personalizar joyas
 * Permite seleccionar forma, gema, material, tamaño y talla
 * Muestra vista previa en tiempo real de la combinación seleccionada
 */
class PersonalizacionActivity : BaseActivity() {

    // Repository
    private lateinit var repository: PersonalizacionRepository

    // Estado de la personalización
    private val estado = PersonalizacionState()

    // Vistas - Toolbar
    private lateinit var toolbar: MaterialToolbar

    // Vistas - Preview (MODIFICADO: sin miniaturas)
    private lateinit var ivMainPreview: ImageView
    private lateinit var btnPrevView: ImageView
    private lateinit var btnNextView: ImageView
    private lateinit var tvCurrentView: TextView
    private lateinit var progressLoading: ProgressBar

    // Vistas - RecyclerViews
    private lateinit var rvForma: RecyclerView
    private lateinit var rvGema: RecyclerView
    private lateinit var rvMaterial: RecyclerView

    // Vistas - Tamaño y Talla
    private lateinit var chipGroupTamano: ChipGroup
    private lateinit var chip7mm: Chip
    private lateinit var chip8mm: Chip
    private lateinit var spinnerTalla: AutoCompleteTextView

    // Vistas - Resumen
    private lateinit var tvSummary: TextView
    private lateinit var fabSave: ExtendedFloatingActionButton

    // Adapters
    private lateinit var formaAdapter: PersonalizacionAdapter
    private lateinit var gemaAdapter: PersonalizacionAdapter
    private lateinit var materialAdapter: PersonalizacionAdapter

    // Datos
    private var valoresPorCategoria: Map<String, List<PersonalizacionValor>> = emptyMap()

    // NUEVO: Para detectar swipes
    private lateinit var gestureDetector: GestureDetectorCompat
    private val vistas = listOf("frontal", "perfil", "superior")
    private var indiceVistaActual = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personalizacion)

        // Inicializar repository
        repository = PersonalizacionRepository()

        // Inicializar vistas
        initViews()

        // Configurar toolbar
        setupToolbar()

        // NUEVO: Configurar gesture detector para swipes
        setupGestureDetector()

        // Configurar adapters
        setupAdapters()

        // Configurar listeners
        setupListeners()

        // Cargar datos iniciales
        cargarDatosIniciales()
    }

    /**
     * Inicializa todas las vistas
     */
    private fun initViews() {
        // Toolbar
        toolbar = findViewById(R.id.toolbar)

        // Preview (MODIFICADO: sin miniaturas)
        ivMainPreview = findViewById(R.id.iv_main_preview)
        btnPrevView = findViewById(R.id.btn_prev_view)
        btnNextView = findViewById(R.id.btn_next_view)
        tvCurrentView = findViewById(R.id.tv_current_view)
        progressLoading = findViewById(R.id.progress_loading)

        // RecyclerViews
        rvForma = findViewById(R.id.rv_forma)
        rvGema = findViewById(R.id.rv_gema)
        rvMaterial = findViewById(R.id.rv_material)

        // Tamaño y Talla
        chipGroupTamano = findViewById(R.id.chip_group_tamano)
        chip7mm = findViewById(R.id.chip_7mm)
        chip8mm = findViewById(R.id.chip_8mm)
        spinnerTalla = findViewById(R.id.spinner_talla)

        // Resumen
        tvSummary = findViewById(R.id.tv_summary)
        fabSave = findViewById(R.id.fab_save)
    }

    /**
     * Configura el toolbar con botón de retroceso
     */
    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    /**
     * NUEVO: Configura el detector de gestos para swipes
     */
    private fun setupGestureDetector() {
        val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
            private val SWIPE_THRESHOLD = 100
            private val SWIPE_VELOCITY_THRESHOLD = 100

            override fun onDown(e: MotionEvent): Boolean = true

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (e1 == null) return false

                val diffX = e2.x - e1.x
                val diffY = e2.y - e1.y

                if (abs(diffX) > abs(diffY) &&
                    abs(diffX) > SWIPE_THRESHOLD &&
                    abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {

                    if (diffX > 0) {
                        // Swipe derecha → vista anterior
                        vistaAnterior()
                    } else {
                        // Swipe izquierda → vista siguiente
                        vistaSiguiente()
                    }
                    return true
                }
                return false
            }
        }

        gestureDetector = GestureDetectorCompat(this, gestureListener)

        ivMainPreview.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }

    /**
     * Configura los adapters de los RecyclerViews
     */
    private fun setupAdapters() {
        // Adapter de Forma
        formaAdapter = PersonalizacionAdapter("forma") { valor ->
            onFormaSeleccionada(valor)
        }
        rvForma.adapter = formaAdapter

        // Adapter de Gema
        gemaAdapter = PersonalizacionAdapter("gema") { valor ->
            onGemaSeleccionada(valor)
        }
        rvGema.adapter = gemaAdapter

        // Adapter de Material
        materialAdapter = PersonalizacionAdapter("material") { valor ->
            onMaterialSeleccionado(valor)
        }
        rvMaterial.adapter = materialAdapter
    }

    /**
     * Configura todos los listeners de la UI
     */
    private fun setupListeners() {
        // NUEVO: Listeners de flechas de navegación
        btnPrevView.setOnClickListener { vistaAnterior() }
        btnNextView.setOnClickListener { vistaSiguiente() }

        // Listener de tamaño (ChipGroup)
        chipGroupTamano.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.chip_7mm -> onTamanoSeleccionado(chip7mm.tag.toString(), "7 mm")
                R.id.chip_8mm -> onTamanoSeleccionado(chip8mm.tag.toString(), "8 mm")
            }
        }

        // Listener del botón guardar
        fabSave.setOnClickListener {
            guardarPersonalizacion()
        }
    }

    /**
     * NUEVO: Navega a la vista siguiente (cíclico)
     */
    private fun vistaSiguiente() {
        indiceVistaActual = (indiceVistaActual + 1) % vistas.size
        cambiarVista(vistas[indiceVistaActual])
    }

    /**
     * NUEVO: Navega a la vista anterior (cíclico)
     */
    private fun vistaAnterior() {
        indiceVistaActual = if (indiceVistaActual - 1 < 0) vistas.size - 1 else indiceVistaActual - 1
        cambiarVista(vistas[indiceVistaActual])
    }

    /**
     * Carga los datos iniciales desde la API
     */
    private fun cargarDatosIniciales() {
        lifecycleScope.launch {
            try {
                mostrarCargando(true)

                // Cargar todas las opciones
                val opcionesResult = repository.obtenerOpciones()
                if (opcionesResult.isFailure) {
                    mostrarError("Error al cargar opciones: ${opcionesResult.exceptionOrNull()?.message}")
                    return@launch
                }

                val opciones = opcionesResult.getOrNull() ?: emptyList()

                // NUEVO: Log de opciones cargadas
                Log.d(TAG, "=== OPCIONES CARGADAS ===")
                opciones.forEach { opcion ->
                    Log.d(TAG, "ID: ${opcion.id}, Nombre: '${opcion.nombre}', Clave: '${opcion.obtenerClave()}'")
                }

                // Cargar valores de cada opción
                val valoresMap = mutableMapOf<String, List<PersonalizacionValor>>()

                for (opcion in opciones) {
                    val clave = opcion.obtenerClave()
                    val valoresResult = repository.obtenerValoresDisponibles(opcion.id)

                    if (valoresResult.isSuccess) {
                        val valores = valoresResult.getOrNull() ?: emptyList()
                        valoresMap[clave] = valores

                        // NUEVO: Log de valores por opción
                        Log.d(TAG, "Opcion ID ${opcion.id} ('${opcion.nombre}') -> Clave '$clave' -> ${valores.size} valores:")
                        valores.forEach { v ->
                            Log.d(TAG, "  - ${v.nombre} (ID: ${v.id})")
                        }
                    }
                }

                valoresPorCategoria = valoresMap

                // NUEVO: Log del mapa final
                Log.d(TAG, "=== MAPA FINAL ===")
                valoresPorCategoria.forEach { (clave, valores) ->
                    Log.d(TAG, "'$clave' -> ${valores.size} valores")
                }
                Log.d(TAG, "==================")

                // Configurar UI con los datos
                configurarUIConDatos()

                // Cargar vista previa inicial
                actualizarVistaPrevia()

            } catch (e: Exception) {
                mostrarError("Error inesperado: ${e.message}")
            } finally {
                mostrarCargando(false)
            }
        }
    }

    /**
     * Configura la UI con los datos cargados
     */
    private fun configurarUIConDatos() {
        // Configurar Forma
        valoresPorCategoria["forma"]?.let { formas ->
            formaAdapter.submitList(formas)
            if (formas.isNotEmpty()) {
                formaAdapter.setSelectedPosition(0)
                estado.actualizar("forma", formas[0])
            }
        }

        // Configurar Gema
        valoresPorCategoria["gema"]?.let { gemas ->
            gemaAdapter.submitList(gemas)
            if (gemas.isNotEmpty()) {
                gemaAdapter.setSelectedPosition(0)
                estado.actualizar("gema", gemas[0])
            }
        }

        // Configurar Material
        valoresPorCategoria["material"]?.let { materiales ->
            materialAdapter.submitList(materiales)
            if (materiales.isNotEmpty()) {
                materialAdapter.setSelectedPosition(0)
                estado.actualizar("material", materiales[0])
            }
        }

        // Configurar Tamaño (por defecto 7mm ya está seleccionado)
        valoresPorCategoria["tamano"]?.let { tamanos ->
            if (tamanos.isNotEmpty()) {
                estado.actualizar("tamano", tamanos[0])
            }
        }

        // Configurar Talla (dropdown)
        valoresPorCategoria["talla"]?.let { tallas ->
            val nombresTallas = tallas.map { it.nombre }
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                nombresTallas
            )
            spinnerTalla.setAdapter(adapter)

            if (tallas.isNotEmpty()) {
                spinnerTalla.setText(tallas[0].nombre, false)
                estado.actualizar("talla", tallas[0])

                spinnerTalla.setOnItemClickListener { _, _, position, _ ->
                    onTallaSeleccionada(tallas[position])
                }
            }
        }

        // Actualizar resumen
        actualizarResumen()
    }

    // ==========================================
    // HANDLERS DE SELECCIÓN
    // ==========================================

    /**
     * Handler cuando se selecciona una forma
     */
    private fun onFormaSeleccionada(valor: PersonalizacionValor) {
        estado.actualizar("forma", valor)
        actualizarVistaPrevia()
        actualizarResumen()
    }

    /**
     * Handler cuando se selecciona una gema
     */
    private fun onGemaSeleccionada(valor: PersonalizacionValor) {
        estado.actualizar("gema", valor)
        actualizarVistaPrevia()
        actualizarResumen()
    }

    /**
     * Handler cuando se selecciona un material
     */
    private fun onMaterialSeleccionado(valor: PersonalizacionValor) {
        estado.actualizar("material", valor)
        actualizarVistaPrevia()
        actualizarResumen()
    }

    /**
     * Handler cuando se selecciona un tamaño
     */
    private fun onTamanoSeleccionado(slug: String, nombre: String) {
        valoresPorCategoria["tamano"]?.find { it.obtenerSlug() == slug }?.let { valor ->
            estado.actualizar("tamano", valor)
            actualizarResumen()
        }
    }

    /**
     * Handler cuando se selecciona una talla
     */
    private fun onTallaSeleccionada(valor: PersonalizacionValor) {
        estado.actualizar("talla", valor)
        actualizarResumen()
    }

    // ==========================================
    // ACTUALIZACIÓN DE VISTAS
    // ==========================================

    /**
     * Actualiza la vista previa con la combinación actual
     */
    private fun actualizarVistaPrevia() {
        val urlActual = estado.construirUrlImagen(ApiConfig.BASE_URL_ASSETS, estado.vistaActual)
        mostrarCargandoImagen(true)

        // Cargar imagen sin animación (la primera vez)
        ImagenHelper.cargarVistaAnillo(ivMainPreview, urlActual, true)

        // Simular fin de carga (en producción usar Glide listener)
        ivMainPreview.postDelayed({
            mostrarCargandoImagen(false)
        }, 500)

        // Precargar las otras vistas en background
        ImagenHelper.precargarVistas(
            this,
            estado.gemaSlug,
            estado.formaSlug,
            estado.materialSlug
        )
    }

    /**
     * MODIFICADO: Cambia la vista previa con animación fade
     */
    private fun cambiarVista(vista: String) {
        estado.vistaActual = vista

        // Actualizar texto de vista actual
        tvCurrentView.text = when (vista) {
            "superior" -> "Vista Superior"
            "frontal" -> "Vista Frontal"
            "perfil" -> "Vista Perfil"
            else -> "Vista"
        }

        // Actualizar imagen con animación fade
        val url = estado.construirUrlImagen(ApiConfig.BASE_URL_ASSETS, vista)
        cargarImagenConFade(url)
    }

    /**
     * NUEVO: Carga imagen con animación fade
     */
    private fun cargarImagenConFade(url: String) {
        // Animación fade out
        val fadeOut = AlphaAnimation(1f, 0f).apply {
            duration = 150
            fillAfter = true
        }

        // Animación fade in
        val fadeIn = AlphaAnimation(0f, 1f).apply {
            duration = 150
            fillAfter = true
        }

        fadeOut.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
            override fun onAnimationStart(animation: android.view.animation.Animation?) {}

            override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                // Cargar nueva imagen
                ImagenHelper.cargarVistaAnillo(ivMainPreview, url, true)
                // Aplicar fade in
                ivMainPreview.startAnimation(fadeIn)
            }

            override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
        })

        ivMainPreview.startAnimation(fadeOut)
    }

    /**
     * Actualiza el resumen textual de la personalización
     */
    private fun actualizarResumen() {
        tvSummary.text = estado.obtenerResumen()
    }

    // ==========================================
    // GUARDADO DE PERSONALIZACIÓN
    // ==========================================

    /**
     * Guarda la personalización en el backend
     */
    private fun guardarPersonalizacion() {
        lifecycleScope.launch {
            try {
                // Validar que esté completa
                if (!estado.esValido()) {
                    mostrarError(estado.obtenerMensajeError() ?: "Personalización incompleta")
                    return@launch
                }

                fabSave.isEnabled = false
                fabSave.text = "Guardando..."

                // Obtener usuario ID (si está logueado)
                val usuarioId = if (isLoggedIn()) {
                    sessionManager.getUserId()
                } else {
                    null
                }

                // Guardar en el backend
                val result = repository.crearPersonalizacion(estado, usuarioId)

                if (result.isSuccess) {
                    val personalizacion = result.getOrNull()
                    Toast.makeText(
                        this@PersonalizacionActivity,
                        "¡Personalización guardada! ID: ${personalizacion?.id}",
                        Toast.LENGTH_LONG
                    ).show()

                    // Aquí podrías:
                    // 1. Abrir un chat con asesor
                    // 2. Navegar a otra actividad
                    // 3. Enviar email/notificación

                    // Por ahora, volver atrás
                    finish()
                } else {
                    mostrarError("Error al guardar: ${result.exceptionOrNull()?.message}")
                }

            } catch (e: Exception) {
                mostrarError("Error inesperado: ${e.message}")
            } finally {
                fabSave.isEnabled = true
                fabSave.text = "Continuar con asesor"
            }
        }
    }

    // ==========================================
    // UTILIDADES UI
    // ==========================================

    /**
     * Muestra u oculta el indicador de carga general
     */
    private fun mostrarCargando(mostrar: Boolean) {
        // Aquí podrías mostrar un ProgressBar fullscreen
        // Por ahora solo deshabilitamos la UI
        fabSave.isEnabled = !mostrar
    }

    /**
     * Muestra u oculta el indicador de carga de imagen
     */
    private fun mostrarCargandoImagen(mostrar: Boolean) {
        progressLoading.visibility = if (mostrar) View.VISIBLE else View.GONE
    }

    /**
     * Muestra un mensaje de error
     */
    private fun mostrarError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    /**
     * Maneja el botón de retroceso
     */
    override fun onBackPressed() {
        // Aquí podrías mostrar un diálogo de confirmación
        // "¿Estás seguro? Perderás tu personalización"
        super.onBackPressed()
    }
}