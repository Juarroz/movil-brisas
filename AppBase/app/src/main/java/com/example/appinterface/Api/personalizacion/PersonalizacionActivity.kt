package com.example.appinterface.Api.personalizacion

import android.content.ContentValues.TAG
import android.content.Intent
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
import com.example.appinterface.Api.contacto.ContactCreateActivity
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

    companion object {
        const val EXTRA_RESUMEN_PERSONALIZACION = "extra_resumen_personalizacion"
        const val EXTRA_ID_PERSONALIZACION = "extra_id_personalizacion"
    }

    // Repository
    private lateinit var repository: PersonalizacionRepository

    // Estado de la personalización
    private val estado = PersonalizacionState()

    // NUEVO: Para guardar la personalización una sola vez
    private var personalizacionGuardada: PersonalizacionGuardada? = null

    // Vistas - Toolbar
    private lateinit var toolbar: MaterialToolbar

    // Vistas - Preview
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

    // Para detectar swipes
    private lateinit var gestureDetector: GestureDetectorCompat
    private val vistas = listOf("frontal", "perfil", "superior")
    private var indiceVistaActual = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personalizacion)

        repository = PersonalizacionRepository()
        initViews()
        setupToolbar()
        setupGestureDetector()
        setupAdapters()
        setupListeners()
        cargarDatosIniciales()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        ivMainPreview = findViewById(R.id.iv_main_preview)
        btnPrevView = findViewById(R.id.btn_prev_view)
        btnNextView = findViewById(R.id.btn_next_view)
        tvCurrentView = findViewById(R.id.tv_current_view)
        progressLoading = findViewById(R.id.progress_loading)
        rvForma = findViewById(R.id.rv_forma)
        rvGema = findViewById(R.id.rv_gema)
        rvMaterial = findViewById(R.id.rv_material)
        chipGroupTamano = findViewById(R.id.chip_group_tamano)
        chip7mm = findViewById(R.id.chip_7mm)
        chip8mm = findViewById(R.id.chip_8mm)
        spinnerTalla = findViewById(R.id.spinner_talla)
        tvSummary = findViewById(R.id.tv_summary)
        fabSave = findViewById(R.id.fab_save)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

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
                        vistaAnterior()
                    } else {
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

    private fun setupAdapters() {
        formaAdapter = PersonalizacionAdapter("forma") { valor ->
            onFormaSeleccionada(valor)
        }
        rvForma.adapter = formaAdapter

        gemaAdapter = PersonalizacionAdapter("gema") { valor ->
            onGemaSeleccionada(valor)
        }
        rvGema.adapter = gemaAdapter

        materialAdapter = PersonalizacionAdapter("material") { valor ->
            onMaterialSeleccionado(valor)
        }
        rvMaterial.adapter = materialAdapter
    }

    private fun setupListeners() {
        btnPrevView.setOnClickListener { vistaAnterior() }
        btnNextView.setOnClickListener { vistaSiguiente() }

        chipGroupTamano.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.chip_7mm -> onTamanoSeleccionado(chip7mm.tag.toString(), "7 mm")
                R.id.chip_8mm -> onTamanoSeleccionado(chip8mm.tag.toString(), "8 mm")
            }
        }

        // MODIFICADO: Nuevo flujo del botón
        fabSave.setOnClickListener {
            continuarConFormulario()
        }
    }

    private fun vistaSiguiente() {
        indiceVistaActual = (indiceVistaActual + 1) % vistas.size
        cambiarVista(vistas[indiceVistaActual])
    }

    private fun vistaAnterior() {
        indiceVistaActual = if (indiceVistaActual - 1 < 0) vistas.size - 1 else indiceVistaActual - 1
        cambiarVista(vistas[indiceVistaActual])
    }

    private fun cargarDatosIniciales() {
        lifecycleScope.launch {
            try {
                mostrarCargando(true)

                val opcionesResult = repository.obtenerOpciones()
                if (opcionesResult.isFailure) {
                    mostrarError("Error al cargar opciones: ${opcionesResult.exceptionOrNull()?.message}")
                    return@launch
                }

                val opciones = opcionesResult.getOrNull() ?: emptyList()

                Log.d(TAG, "=== OPCIONES CARGADAS ===")
                opciones.forEach { opcion ->
                    Log.d(TAG, "ID: ${opcion.id}, Nombre: '${opcion.nombre}', Clave: '${opcion.obtenerClave()}'")
                }

                val valoresMap = mutableMapOf<String, List<PersonalizacionValor>>()

                for (opcion in opciones) {
                    val clave = opcion.obtenerClave()
                    val valoresResult = repository.obtenerValoresDisponibles(opcion.id)

                    if (valoresResult.isSuccess) {
                        val valores = valoresResult.getOrNull() ?: emptyList()
                        valoresMap[clave] = valores

                        Log.d(TAG, "Opcion ID ${opcion.id} ('${opcion.nombre}') -> Clave '$clave' -> ${valores.size} valores:")
                        valores.forEach { v ->
                            Log.d(TAG, "  - ${v.nombre} (ID: ${v.id})")
                        }
                    }
                }

                valoresPorCategoria = valoresMap

                Log.d(TAG, "=== MAPA FINAL ===")
                valoresPorCategoria.forEach { (clave, valores) ->
                    Log.d(TAG, "'$clave' -> ${valores.size} valores")
                }
                Log.d(TAG, "==================")

                configurarUIConDatos()
                actualizarVistaPrevia()

            } catch (e: Exception) {
                mostrarError("Error inesperado: ${e.message}")
            } finally {
                mostrarCargando(false)
            }
        }
    }

    private fun configurarUIConDatos() {
        valoresPorCategoria["forma"]?.let { formas ->
            formaAdapter.submitList(formas)
            if (formas.isNotEmpty()) {
                formaAdapter.setSelectedPosition(0)
                estado.actualizar("forma", formas[0])
            }
        }

        valoresPorCategoria["gema"]?.let { gemas ->
            gemaAdapter.submitList(gemas)
            if (gemas.isNotEmpty()) {
                gemaAdapter.setSelectedPosition(0)
                estado.actualizar("gema", gemas[0])
            }
        }

        valoresPorCategoria["material"]?.let { materiales ->
            materialAdapter.submitList(materiales)
            if (materiales.isNotEmpty()) {
                materialAdapter.setSelectedPosition(0)
                estado.actualizar("material", materiales[0])
            }
        }

        valoresPorCategoria["tamano"]?.let { tamanos ->
            if (tamanos.isNotEmpty()) {
                estado.actualizar("tamano", tamanos[0])
            }
        }

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

        actualizarResumen()
    }

    private fun onFormaSeleccionada(valor: PersonalizacionValor) {
        estado.actualizar("forma", valor)
        actualizarVistaPrevia()
        actualizarResumen()
    }

    private fun onGemaSeleccionada(valor: PersonalizacionValor) {
        estado.actualizar("gema", valor)
        actualizarVistaPrevia()
        actualizarResumen()
    }

    private fun onMaterialSeleccionado(valor: PersonalizacionValor) {
        estado.actualizar("material", valor)
        actualizarVistaPrevia()
        actualizarResumen()
    }

    private fun onTamanoSeleccionado(slug: String, nombre: String) {
        valoresPorCategoria["tamano"]?.find { it.obtenerSlug() == slug }?.let { valor ->
            estado.actualizar("tamano", valor)
            actualizarResumen()
        }
    }

    private fun onTallaSeleccionada(valor: PersonalizacionValor) {
        estado.actualizar("talla", valor)
        actualizarResumen()
    }

    private fun actualizarVistaPrevia() {
        val urlActual = estado.construirUrlImagen(ApiConfig.BASE_URL_ASSETS, estado.vistaActual)
        mostrarCargandoImagen(true)

        ImagenHelper.cargarVistaAnillo(ivMainPreview, urlActual, true)

        ivMainPreview.postDelayed({
            mostrarCargandoImagen(false)
        }, 500)

        ImagenHelper.precargarVistas(
            this,
            estado.gemaSlug,
            estado.formaSlug,
            estado.materialSlug
        )
    }

    private fun cambiarVista(vista: String) {
        estado.vistaActual = vista

        tvCurrentView.text = when (vista) {
            "superior" -> "Vista Superior"
            "frontal" -> "Vista Frontal"
            "perfil" -> "Vista Perfil"
            else -> "Vista"
        }

        val url = estado.construirUrlImagen(ApiConfig.BASE_URL_ASSETS, vista)
        cargarImagenConFade(url)
    }

    private fun cargarImagenConFade(url: String) {
        val fadeOut = AlphaAnimation(1f, 0f).apply {
            duration = 150
            fillAfter = true
        }

        val fadeIn = AlphaAnimation(0f, 1f).apply {
            duration = 150
            fillAfter = true
        }

        fadeOut.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
            override fun onAnimationStart(animation: android.view.animation.Animation?) {}

            override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                ImagenHelper.cargarVistaAnillo(ivMainPreview, url, true)
                ivMainPreview.startAnimation(fadeIn)
            }

            override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
        })

        ivMainPreview.startAnimation(fadeOut)
    }

    private fun actualizarResumen() {
        tvSummary.text = estado.obtenerResumen()
    }

    // ==========================================
    // NUEVO FLUJO: GUARDAR Y ABRIR FORMULARIO
    // ==========================================

    /**
     * NUEVO: Flujo completo - Guarda personalización y abre formulario
     */
    private fun continuarConFormulario() {
        lifecycleScope.launch {
            try {

                // Validar que esté completa
                if (!estado.esValido()) {
                    mostrarError(estado.obtenerMensajeError() ?: "Personalización incompleta")
                    return@launch
                }

                // Si ya está guardada, solo abrir formulario
                if (personalizacionGuardada != null) {
                    abrirFormularioConResumen(personalizacionGuardada!!)
                    return@launch
                }

                // Guardar personalización
                fabSave.isEnabled = false
                fabSave.text = "Guardando..."

                val usuarioId = if (isLoggedIn()) {
                    sessionManager.getUserId()
                } else {
                    null
                }

                val result = repository.crearPersonalizacion(estado, usuarioId)

                if (result.isSuccess) {
                    val personalizacion = result.getOrNull()!!
                    personalizacionGuardada = personalizacion

                    Log.d(TAG, "Personalización guardada - ID: ${personalizacion.id}")

                    // Abrir formulario con el resumen
                    abrirFormularioConResumen(personalizacion)

                } else {
                    mostrarError("Error al guardar: ${result.exceptionOrNull()?.message}")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error al continuar", e)
                mostrarError("Error inesperado: ${e.message}")
            } finally {
                fabSave.isEnabled = true
                fabSave.text = "Continuar"
            }
        }
    }

    /**
     * NUEVO: Abre el formulario de contacto con el resumen pre-cargado
     */
    private fun abrirFormularioConResumen(personalizacion: PersonalizacionGuardada) {
        val intent = Intent(this, ContactCreateActivity::class.java).apply {
            putExtra(EXTRA_RESUMEN_PERSONALIZACION, personalizacion.generarResumenParaFormulario())
            putExtra(EXTRA_ID_PERSONALIZACION, personalizacion.id)
        }
        startActivity(intent)
    }

    // ==========================================
    // UTILIDADES UI
    // ==========================================

    private fun mostrarCargando(mostrar: Boolean) {
        fabSave.isEnabled = !mostrar
    }

    private fun mostrarCargandoImagen(mostrar: Boolean) {
        progressLoading.visibility = if (mostrar) View.VISIBLE else View.GONE
    }

    private fun mostrarError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}