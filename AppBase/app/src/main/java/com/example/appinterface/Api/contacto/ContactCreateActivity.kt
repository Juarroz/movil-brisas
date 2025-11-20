package com.example.appinterface.Api.contacto

import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.appinterface.R
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContactCreateActivity : AppCompatActivity() {

    private val TAG = "ContactCreateActivity"

    private lateinit var txtNombre: TextInputEditText
    private lateinit var txtCorreo: TextInputEditText
    private lateinit var txtTelefono: TextInputEditText
    private lateinit var txtMensaje: TextInputEditText
    private lateinit var btnEnviar: Button
    private lateinit var toolbar: Toolbar

    private val repository = ContactoRepository()

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

        setContentView(R.layout.activity_contact_create)

        // Configurar toolbar
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Contáctanos"
        }

        // Configurar el botón de navegación para volver atrás
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // Inicializar vistas
        txtNombre = findViewById(R.id.txtNombre)
        txtCorreo = findViewById(R.id.txtCorreo)
        txtTelefono = findViewById(R.id.txtTelefono)
        txtMensaje = findViewById(R.id.txtMensaje)
        btnEnviar = findViewById(R.id.btnFormulario)

        btnEnviar.setOnClickListener { safeEnviarContacto() }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
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

        val request = ContactoFormularioRequestDTO(
            nombre = nombre,
            correo = if (correo.isEmpty()) null else correo,
            telefono = if (telefono.isEmpty()) null else telefono,
            mensaje = mensaje,
            terminos = true,
            via = "formulario"
        )

        btnEnviar.isEnabled = false
        btnEnviar.alpha = 0.6f

        try {
            repository.enviarContacto(request).enqueue(object : Callback<ContactoFormularioResponseDTO> {
                override fun onResponse(call: Call<ContactoFormularioResponseDTO>, response: Response<ContactoFormularioResponseDTO>) {
                    try {
                        btnEnviar.isEnabled = true
                        btnEnviar.alpha = 1.0f
                        if (response.isSuccessful) {
                            Toast.makeText(this@ContactCreateActivity, "Contacto enviado correctamente", Toast.LENGTH_SHORT).show()
                            limpiarCampos()
                            // Opcional: Volver a la actividad anterior después de enviar
                            // finish()
                        } else {
                            Log.e(TAG, "Server error code=${response.code()} errorBody=${response.errorBody()}")
                            Toast.makeText(this@ContactCreateActivity, "Error servidor: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    } catch (ex: Exception) {
                        Log.e(TAG, "Exception en onResponse", ex)
                        Toast.makeText(this@ContactCreateActivity, "Error en respuesta", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ContactoFormularioResponseDTO>, t: Throwable) {
                    Log.e(TAG, "onFailure enviarContacto", t)
                    btnEnviar.isEnabled = true
                    btnEnviar.alpha = 1.0f
                    Toast.makeText(this@ContactCreateActivity, "Fallo red: ${t.message}", Toast.LENGTH_SHORT).show()
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