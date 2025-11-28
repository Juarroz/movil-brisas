package com.example.appinterface.Api.contacto

import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.example.appinterface.Api.personalizacion.PersonalizacionActivity
import com.example.appinterface.R
import com.example.appinterface.core.BaseActivity
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * ContactCreateActivity - Formulario de contacto
 *
 * AHORA hereda de BaseActivity para usar las barras superiores por rol
 * Mantiene toda la lógica original del formulario
 */
class ContactCreateActivity : BaseActivity() {

    private val TAG = "ContactCreateActivity"

    private lateinit var txtNombre: TextInputEditText
    private lateinit var txtCorreo: TextInputEditText
    private lateinit var txtTelefono: TextInputEditText
    private lateinit var txtMensaje: TextInputEditText
    private lateinit var btnEnviar: Button

    private val repository = ContactoRepository()

    // Para guardar el ID de personalización si viene
    private var personalizacionId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e(TAG, "Uncaught exception in thread ${thread.name}", throwable)
        }

        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()
        )

        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()
        )

        // CAMBIO: Cargar layout según el rol del usuario
        loadLayoutBasedOnRole()

        // CAMBIO: Inicializar UI común (barra superior con logo, notificaciones, perfil)
        // NOTA: BaseActivity ya inicializa sessionManager, no necesitas hacerlo de nuevo
        initCommonUI()

        // Inicializar vistas del formulario
        initFormViews()

        // Cargar resumen si viene de PersonalizacionActivity
        cargarResumenSiExiste()
    }

    /**
     * NUEVO: Carga el layout correcto según el rol del usuario
     */
    private fun loadLayoutBasedOnRole() {
        when {
            !isLoggedIn() -> {
                setContentView(R.layout.activity_contact_create)
            }
            isAdmin() -> {
                setContentView(R.layout.activity_contact_create_admin)
            }
            else -> {
                setContentView(R.layout.activity_contact_create_user)
            }
        }
    }

    /**
     * NUEVO: Inicializa las vistas específicas del formulario
     */
    private fun initFormViews() {
        txtNombre = findViewById(R.id.txtNombre)
        txtCorreo = findViewById(R.id.txtCorreo)
        txtTelefono = findViewById(R.id.txtTelefono)
        txtMensaje = findViewById(R.id.txtMensaje)
        btnEnviar = findViewById(R.id.btnFormulario)

        btnEnviar.setOnClickListener { safeEnviarContacto() }
    }

    /**
     * Carga el resumen de personalización si existe en el Intent
     */
    private fun cargarResumenSiExiste() {
        val resumenPersonalizacion = intent.getStringExtra(
            PersonalizacionActivity.EXTRA_RESUMEN_PERSONALIZACION
        )

        personalizacionId = intent.getIntExtra(
            PersonalizacionActivity.EXTRA_ID_PERSONALIZACION,
            -1
        ).takeIf { it != -1 }

        if (resumenPersonalizacion != null) {
            Log.d(TAG, "📋 Resumen recibido - ID: $personalizacionId")

            // Pre-cargar el mensaje con el resumen
            val mensajeCompleto = buildString {
                appendLine("Hola, me interesa esta personalización:")
                appendLine()
                appendLine(resumenPersonalizacion)
                appendLine()
                appendLine("Por favor, ¿podrían contactarme para más información?")
            }

            txtMensaje.setText(mensajeCompleto)
            txtMensaje.setSelection(txtMensaje.text?.length ?: 0)

            Toast.makeText(
                this,
                "Resumen de personalización cargado en el mensaje",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun safeEnviarContacto() {
        try {
            enviarContacto()
        } catch (e: Exception) {
            Log.e(TAG, "Exception al iniciar envío", e)
            Toast.makeText(this, "Error interno: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun enviarContacto() {
        val nombre = txtNombre.text.toString().trim()
        val correo = txtCorreo.text.toString().trim()
        val telefono = txtTelefono.text.toString().trim()
        val mensaje = txtMensaje.text.toString().trim()

        if (nombre.isEmpty() || mensaje.isEmpty()) {
            Toast.makeText(this, "Nombre y mensaje son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }
        if (correo.isEmpty() && telefono.isEmpty()) {
            Toast.makeText(this, "Ingresa correo o teléfono", Toast.LENGTH_SHORT).show()
            return
        }

        // CAMBIO: Usar sessionManager heredado de BaseActivity
        val usuarioId = if (sessionManager.isLoggedIn()) {
            sessionManager.getUserId()
        } else {
            null
        }

        // Agregar referencia de personalización al mensaje si existe
        val mensajeFinal = if (personalizacionId != null) {
            "$mensaje\n\n[Ref. Personalización ID: $personalizacionId]"
        } else {
            mensaje
        }

        val request = ContactoFormularioRequestDTO(
            nombre = nombre,
            correo = if (correo.isEmpty()) null else correo,
            telefono = if (telefono.isEmpty()) null else telefono,
            mensaje = mensajeFinal,
            usuarioId = usuarioId,  // Se envía el ID del usuario
            terminos = true,
            via = "formulario"
        )

        btnEnviar.isEnabled = false
        btnEnviar.alpha = 0.6f

        try {
            repository.enviarContacto(request).enqueue(object : Callback<ContactoFormularioResponseDTO> {
                override fun onResponse(
                    call: Call<ContactoFormularioResponseDTO>,
                    response: Response<ContactoFormularioResponseDTO>
                ) {
                    try {
                        btnEnviar.isEnabled = true
                        btnEnviar.alpha = 1.0f
                        if (response.isSuccessful) {
                            val body = response.body()
                            Log.d(TAG, "✅ Contacto enviado - ID: ${body?.id}")

                            Toast.makeText(
                                this@ContactCreateActivity,
                                "¡Contacto enviado! Nos comunicaremos pronto",
                                Toast.LENGTH_LONG
                            ).show()

                            navigateHome()

                        } else {
                            Log.e(TAG, "Server error code=${response.code()} errorBody=${response.errorBody()?.string()}")
                            Toast.makeText(
                                this@ContactCreateActivity,
                                "Error servidor: ${response.code()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (ex: Exception) {
                        Log.e(TAG, "Exception en onResponse", ex)
                        Toast.makeText(
                            this@ContactCreateActivity,
                            "Error en respuesta",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ContactoFormularioResponseDTO>, t: Throwable) {
                    Log.e(TAG, "onFailure enviarContacto", t)
                    btnEnviar.isEnabled = true
                    btnEnviar.alpha = 1.0f
                    Toast.makeText(
                        this@ContactCreateActivity,
                        "Fallo red: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Exception enqueue", e)
            btnEnviar.isEnabled = true
            btnEnviar.alpha = 1.0f
            Toast.makeText(this, "Error al enviar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun limpiarCampos() {
        txtNombre.text?.clear()
        txtCorreo.text?.clear()
        txtTelefono.text?.clear()
        txtMensaje.text?.clear()
    }
}