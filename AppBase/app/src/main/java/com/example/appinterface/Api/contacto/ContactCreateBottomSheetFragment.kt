// /java/com/example/appinterface/Api/contacto/ContactCreateBottomSheetFragment.kt

package com.example.appinterface.Api.contacto

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.appinterface.Api.personalizacion.PersonalizacionActivity // Necesario para las claves EXTRA
import com.example.appinterface.R
import com.example.appinterface.core.RetrofitInstance
import com.example.appinterface.core.data.SessionManager // Necesario para obtener sessionManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * ContactCreateBottomSheetFragment - Formulario de contacto como Bottom Sheet.
 * Migrado desde ContactCreateActivity.
 */
class ContactCreateBottomSheetFragment : BottomSheetDialogFragment() {

    private val TAG = "ContactSheet"
    private lateinit var txtNombre: TextInputEditText
    private lateinit var txtCorreo: TextInputEditText
    private lateinit var txtTelefono: TextInputEditText
    private lateinit var txtMensaje: TextInputEditText
    private lateinit var btnEnviar: Button

    private lateinit var sessionManager: SessionManager // Añadido para acceder a la sesión
    private val repository = ContactoRepository()

    private var personalizacionId: Int? = null

    companion object {
        const val TAG_SHEET = "ContactCreateSheet"

        // Constructor con argumentos para pasar el resumen/ID
        fun newInstance(resumen: String?, personalizacionId: Int?): ContactCreateBottomSheetFragment {
            val fragment = ContactCreateBottomSheetFragment()
            val args = Bundle()
            args.putString(PersonalizacionActivity.EXTRA_RESUMEN_PERSONALIZACION, resumen)
            personalizacionId?.let {
                args.putInt(PersonalizacionActivity.EXTRA_ID_PERSONALIZACION, it)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el NUEVO layout del Bottom Sheet
        return inflater.inflate(R.layout.bottom_sheet_contact_create, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar SessionManager (debe ser desde RetrofitInstance o donde lo tengas accesible)
        sessionManager = RetrofitInstance.getSessionManager()

        // Inicializar vistas del formulario
        initFormViews(view)

        // Cargar resumen si viene de PersonalizacionActivity
        cargarResumenSiExiste()
    }

    private fun initFormViews(view: View) {
        // Asegúrate de que estos IDs coincidan con tu contact_create_bottom_sheet.xml
        txtNombre = view.findViewById(R.id.txtNombre)
        txtCorreo = view.findViewById(R.id.txtCorreo)
        txtTelefono = view.findViewById(R.id.txtTelefono)
        txtMensaje = view.findViewById(R.id.txtMensaje)
        btnEnviar = view.findViewById(R.id.btnFormulario)

        btnEnviar.setOnClickListener { safeEnviarContacto() }
    }

    private fun cargarResumenSiExiste() {
        val resumenPersonalizacion = arguments?.getString(PersonalizacionActivity.EXTRA_RESUMEN_PERSONALIZACION)

        if (resumenPersonalizacion != null) {
            // 🔥 SIMPLIFICADO: Solo agregar el resumen al mensaje
            val mensajeCompleto = buildString {
                appendLine("Hola, me interesa esta personalización:")
                appendLine()
                append(resumenPersonalizacion) // Ya viene formateado
                appendLine()
                appendLine("Por favor, ¿podrían contactarme para más información?")
            }

            txtMensaje.setText(mensajeCompleto)
            txtMensaje.setSelection(txtMensaje.text?.length ?: 0)
        }

        // Si el usuario está logueado, precargar correo
        if (sessionManager.isLoggedIn()) {
            val username = sessionManager.getUsername()
            if (username != null && username.contains("@")) {
                txtCorreo.setText(username)
                txtCorreo.isEnabled = false
            }
        }
    }

    private fun safeEnviarContacto() {
        // ... (Lógica de validación y manejo de excepciones de tu Activity, se copia igual) ...
        try {
            enviarContacto()
        } catch (e: Exception) {
            Log.e(TAG, "Exception al iniciar envío", e)
            Toast.makeText(requireContext(), "Error interno: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // 🔥 Copia y adapta esta función de tu ContactCreateActivity.kt
    private fun enviarContacto() {
        val nombre = txtNombre.text.toString().trim()
        val correo = txtCorreo.text.toString().trim()
        val telefono = txtTelefono.text.toString().trim()
        val mensaje = txtMensaje.text.toString().trim()

        // Validaciones
        if (nombre.isEmpty()) {
            Toast.makeText(requireContext(), "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
            return
        }

        if (mensaje.isEmpty()) {
            Toast.makeText(requireContext(), "El mensaje es obligatorio", Toast.LENGTH_SHORT).show()
            return
        }

        val usuarioId = if (sessionManager.isLoggedIn()) {
            sessionManager.getUserId()
        } else {
            null
        }

        // 🔥 YA NO AGREGAMOS REFERENCIA A PERSONALIZACIÓN
        // El resumen ya está incluido en el mensaje
        val request = ContactoFormularioRequestDTO(
            nombre = nombre,
            correo = if (correo.isEmpty()) null else correo,
            telefono = if (telefono.isEmpty()) null else telefono,
            mensaje = mensaje, // Ya incluye el resumen de personalización
            usuarioId = usuarioId,
            terminos = true,
            via = "formulario"
        )

        btnEnviar.isEnabled = false
        btnEnviar.alpha = 0.6f

        repository.enviarContacto(request).enqueue(object : Callback<ContactoFormularioResponseDTO> {
            override fun onResponse(
                call: Call<ContactoFormularioResponseDTO>,
                response: Response<ContactoFormularioResponseDTO>
            ) {
                btnEnviar.isEnabled = true
                btnEnviar.alpha = 1.0f

                if (response.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "¡Contacto enviado! Nos comunicaremos pronto",
                        Toast.LENGTH_LONG
                    ).show()
                    dismiss() // Cerrar el Bottom Sheet
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error del servidor: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ContactoFormularioResponseDTO>, t: Throwable) {
                btnEnviar.isEnabled = true
                btnEnviar.alpha = 1.0f
                Toast.makeText(
                    requireContext(),
                    "Error de conexión: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}