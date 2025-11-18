package com.example.appinterface.Api.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.appinterface.MainActivity
import com.example.appinterface.R
import com.example.appinterface.core.data.SessionManager

/**
 * ProfileActivity - Pantalla de perfil del usuario
 *
 * Muestra:
 * - Username/Email
 * - Roles del usuario
 * - Botón para cerrar sesión
 */
class ProfileActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Inicializar SessionManager
        sessionManager = SessionManager(this)

        // Inicializar vistas de forma segura
        val tvUsername: TextView? = findViewById(R.id.tv_profile_username)
        val tvRoles: TextView? = findViewById(R.id.tv_profile_roles)
        val btnLogout: Button? = findViewById(R.id.btn_logout)
        val btnBack: Button? = findViewById(R.id.btn_back_to_main)

        // Cargar datos del usuario
        tvUsername?.text = sessionManager.getUsername() ?: "Sin correo"
        tvRoles?.text = sessionManager.getRoles().joinToString(", ").ifEmpty { "Sin roles" }

        // Configurar botón de cerrar sesión
        btnLogout?.setOnClickListener {
            showLogoutConfirmation()
        }

        // Configurar botón de volver
        btnBack?.setOnClickListener {
            finish()
        }
    }

    /**
     * Muestra diálogo de confirmación para cerrar sesión
     */
    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar sesión")
            .setMessage("¿Estás seguro de que deseas cerrar sesión?")
            .setPositiveButton("Sí") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Cierra la sesión y redirige a MainActivity
     */
    private fun performLogout() {
        // Limpiar sesión
        sessionManager.logout()

        // Redirigir a MainActivity y limpiar backstack
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish()
    }
}