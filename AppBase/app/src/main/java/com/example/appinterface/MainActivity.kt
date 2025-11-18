package com.example.appinterface

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.appinterface.Api.contacto.ContactCreateActivity
import com.example.appinterface.core.BaseActivity
import com.example.appinterface.R

/**
 * MainActivity - Pantalla principal de la aplicación
 *
 * Comportamiento según sesión:
 * - Usuario NO logueado → Muestra top_app_bar.xml (sin sesión)
 * - Usuario logueado (USER) → Muestra top_user_bar.xml (con perfil)
 * - Usuario logueado (ADMIN) → Muestra top_admin_bar.xml (con tabs)
 */
class MainActivity : BaseActivity() {

    private lateinit var btnFormulario: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Establecer el layout según el rol del usuario
        setContentLayoutBasedOnRole()

        // Inicializar UI común (barra superior, tabs si es admin)
        initCommonUI()

        // Inicializar vistas específicas de MainActivity
        initMainViews()
    }

    /**
     * Establece el layout correcto según el rol del usuario
     */
    private fun setContentLayoutBasedOnRole() {
        when {
            !isLoggedIn() -> {
                // Usuario NO logueado → Layout sin sesión
                setContentView(R.layout.activity_main)
            }
            isAdmin() -> {
                // Usuario ADMIN → Layout con admin bar y tabs
                setContentView(R.layout.activity_main_admin)
            }
            else -> {
                // Usuario normal (USER) → Layout con user bar
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
    }

    /**
     * Abre el formulario de contacto
     */
    private fun abrirFormularioContacto() {
        startActivity(Intent(this, ContactCreateActivity::class.java))
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
     * Cierra la sesión del usuario
     * (Ya está implementado en BaseActivity, pero lo puedes usar aquí)
     */
    fun cerrarSesion(view: View) {
        logout() // Método heredado de BaseActivity
    }

    // No necesitas override isAdmin() aquí, usa el de BaseActivity
    // que ya lee correctamente de SessionManager
}