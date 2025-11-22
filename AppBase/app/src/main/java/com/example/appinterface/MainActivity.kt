package com.example.appinterface

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.appinterface.Api.contacto.ContactCreateActivity
import com.example.appinterface.core.BaseActivity
import com.example.appinterface.Api.personalizacion.PersonalizacionActivity
import com.example.appinterface.R

/**
 * MainActivity - Pantalla principal de la aplicación
 *
 * Carga diferentes layouts según el estado de sesión:
 * - Sin sesión → activity_main.xml (solo top_app_bar)
 * - Admin → activity_main_admin.xml (top_app_bar + top_admin_bar)
 * - User → activity_main_user.xml (top_app_bar + top_user_bar)
 */
class MainActivity : BaseActivity() {

    private lateinit var btnFormulario: Button
    private lateinit var btnPersonalizar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cargar el layout según el rol del usuario
        loadLayoutBasedOnRole()

        // Inicializar UI común (barra superior, tabs si es admin)
        initCommonUI()

        // Inicializar vistas específicas de MainActivity
        initMainViews()
    }

    /**
     * Carga el layout correcto según el rol del usuario
     */
    private fun loadLayoutBasedOnRole() {
        when {
            !isLoggedIn() -> {
                // Sin sesión → Layout básico
                setContentView(R.layout.activity_main)
            }
            isAdmin() -> {
                // Admin → Layout con admin bar
                setContentView(R.layout.activity_main_admin)
            }
            else -> {
                // User normal → Layout con user bar
                setContentView(R.layout.activity_main_user)
            }
        }
    }

    /**
     * Inicializa las vistas específicas de MainActivity
     */
    private fun initMainViews() {
        btnFormulario = findViewById(R.id.btnFormulario)
        btnFormulario.setOnClickListener {
            abrirFormularioContacto()
        }

        btnPersonalizar = findViewById(R.id.btnPersonalizar)
        btnPersonalizar.setOnClickListener {
            abrirPersonalizacion()
        }
    }

    /**
     * Abre el formulario de contacto
     */
    private fun abrirFormularioContacto() {
        startActivity(Intent(this, ContactCreateActivity::class.java))
    }

    /**
     * Abre la pantalla de personalización de joyas
     */
    private fun abrirPersonalizacion() {
        startActivity(Intent(this, PersonalizacionActivity::class.java))
    }

    /**
     * Ejemplo de función para mostrar personas/roles
     */
    fun crearmostrarpersonas(view: View) {
        if (isLoggedIn()) {
            val username = getCurrentUsername() ?: "Usuario"
            val roles = sessionManager.getRoles().joinToString(", ")
            Toast.makeText(
                this,
                "Usuario: $username\nRoles: $roles",
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(
                this,
                "Debes iniciar sesión primero",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Cierra la sesión del usuario desde un botón
     */
    fun cerrarSesion(view: View) {
        logout() // Método heredado de BaseActivity
    }

    /**
     * Se llama cuando se reanuda la actividad
     * (por ejemplo, después de cerrar sesión desde ProfileActivity)
     */
    override fun onResume() {
        super.onResume()
        // Recargar el layout por si cambió el estado de sesión
        loadLayoutBasedOnRole()
        initCommonUI()
        initMainViews()
    }
}