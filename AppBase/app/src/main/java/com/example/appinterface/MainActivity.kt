package com.example.appinterface

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.appinterface.Api.auth.LoginActivity
import com.example.appinterface.Api.auth.ProfileActivity
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var btnMenu: ImageButton
    private lateinit var btnNotifications: ImageButton
    private lateinit var imgProfileTop: ImageView

    // Tus botones existentes
    private lateinit var btnRoles: Button
    private lateinit var btnPedidos: Button
    private lateinit var btnFormulario: Button
    private lateinit var btnGatito: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // el XML nuevo

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.navigation_view)
        btnMenu = findViewById(R.id.btn_menu)
        btnNotifications = findViewById(R.id.btn_notifications)
        imgProfileTop = findViewById(R.id.img_profile_top)

        // tus botones
        btnRoles = findViewById(R.id.MostrarApikotlin)
        btnPedidos = findViewById(R.id.buttonPedidos)
        btnFormulario = findViewById(R.id.buttonSegundaActividad)
        btnGatito = findViewById(R.id.button)

        // listeners básicos
        btnMenu.setOnClickListener {
            toggleDrawer()
        }

        btnNotifications.setOnClickListener {
            Toast.makeText(this, "Notificaciones (pendiente)", Toast.LENGTH_SHORT).show()
            // startActivity(Intent(this, NotificationsActivity::class.java))
        }

        imgProfileTop.setOnClickListener {
            // si está logueado abrir perfil, si no abrir login
            val prefs = getSharedPreferences("brisas_prefs", Context.MODE_PRIVATE)
            val username = prefs.getString("username", null)
            if (username != null) {
                startActivity(Intent(this, ProfileActivity::class.java))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }

        // si los onClick en XML ya llaman a crearmostrarpersonas/irAPedidos, puedes mantenerlos.
        // Pero también enlazo listeners aquí para validación programática.
        btnRoles.setOnClickListener { v -> crearmostrarpersonas(v) }
        btnPedidos.setOnClickListener { irAPedidos() }
        btnFormulario.setOnClickListener { abrirFormularioContacto() }
        btnGatito.setOnClickListener { abrirGatito() }

        setupNavHeaderAndMenu()
    }

    private fun toggleDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            drawerLayout.openDrawer(GravityCompat.END)
        }
    }

    private fun setupNavHeaderAndMenu() {
        val header = navView.getHeaderView(0)
        val headerImage: ImageView = header.findViewById(R.id.header_profile_image)
        val headerName: TextView = header.findViewById(R.id.header_name)
        val headerEmail: TextView = header.findViewById(R.id.header_email)

        val prefs = getSharedPreferences("brisas_prefs", Context.MODE_PRIVATE)
        val username = prefs.getString("username", null)
        val roles = prefs.getStringSet("roles", emptySet())

        if (username != null) {
            headerName.text = username
            headerEmail.text = roles?.joinToString(", ") ?: ""
            // si tuvieras URL de foto, cargar con Glide/Coil aquí
        } else {
            headerName.text = "Invitado"
            headerEmail.text = "Inicia sesión para más opciones"
        }

        // Mostrar u ocultar grupo admin
        navView.menu.setGroupVisible(R.id.group_admin, roles?.contains("ADMIN") == true)

        // Login / Logout visibles según estado
        navView.menu.findItem(R.id.nav_login).isVisible = username == null
        navView.menu.findItem(R.id.nav_logout).isVisible = username != null

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Ir a Inicio", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_contact -> {
                    abrirFormularioContacto()
                }
                R.id.nav_orders -> {
                    irAPedidos()
                }
                R.id.nav_dashboard -> {
                    Toast.makeText(this, "Dashboard admin", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_login -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
                R.id.nav_logout -> {
                    logout()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.END)
            true
        }
    }

    // Métodos referenciados en XML (mantén firmas para android:onClick)
    fun crearmostrarpersonas(view: View) {
        // tu implementación actual o llamada a método que ya tenías
        Toast.makeText(this, "Mostrar roles (ejecución)", Toast.LENGTH_SHORT).show()
        // ejemplo: llamar a tu método que consume la API de roles
    }

    fun irAPedidos() {
        // si en XML lo llamabas por onClick, aquí se adapta; sino crea overload con View
        Toast.makeText(this, "Ir a Pedidos (pendiente)", Toast.LENGTH_SHORT).show()
        // startActivity(Intent(this, PedidosActivity::class.java))
    }

    private fun abrirFormularioContacto() {
        Toast.makeText(this, "Abrir formulario de contacto", Toast.LENGTH_SHORT).show()
        // startActivity(Intent(this, ContactFormActivity::class.java))
    }

    private fun abrirGatito() {
        Toast.makeText(this, "Gatito!", Toast.LENGTH_SHORT).show()
    }

    private fun logout() {
        val prefs = getSharedPreferences("brisas_prefs", Context.MODE_PRIVATE).edit()
        prefs.clear()
        prefs.apply()
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
        recreate()
    }
}
