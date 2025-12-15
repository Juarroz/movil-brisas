package com.example.appinterface.core

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.appinterface.Api.auth.ProfileActivity
import com.example.appinterface.Api.usuarios.UsuarioActivity
import com.example.appinterface.R
import com.example.appinterface.Api.contacto.ContactListActivity
// Importamos la nueva actividad de Pedidos
import com.example.appinterface.Api.pedidos.PedidosActivity
import com.example.appinterface.core.data.SessionManager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout

/**
 * BaseActivity - Actividad base con funcionalidad común para todas las pantallas
 *
 * Características:
 * - Manejo de sesión (verifica si el usuario está logueado)
 * - Barra superior (admin o user) según el rol
 * - Tabs de navegación (solo para admins)
 * - Listeners comunes (perfil, notificaciones, logo)
 */
open class BaseActivity : AppCompatActivity() {

    protected var topAdminTabLayout: TabLayout? = null // Para R.id.topAdminTabLayout (Admin)
    protected var topUserTabLayout: TabLayout? = null  // Para R.id.topUserTabLayout (Usuario)
    protected var mainAppBar: AppBarLayout? = null
    protected var btnNotifications: ImageButton? = null
    protected var imgProfileTop: ImageView? = null
    protected var toolbarLogo: ImageView? = null

    // Session Manager
    protected lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar SessionManager
        sessionManager = SessionManager(this)
    }

    /**
     * Verifica si el usuario actual es admin
     */
    protected open fun isAdmin(): Boolean {
        return sessionManager.isAdmin()
    }

    /**
     * Verifica si el usuario actual es diseñador
     */
    protected fun isDesigner(): Boolean {
        return sessionManager.isDesigner()
    }

    /**
     * Verifica si hay sesión activa
     */
    protected fun isLoggedIn(): Boolean {
        return sessionManager.isLoggedIn()
    }

    /**
     * Obtiene el username del usuario logueado
     */
    protected fun getCurrentUsername(): String? {
        return sessionManager.getUsername()
    }

    /**
     * Inicializa la UI común (barra superior, tabs, listeners)
     */
    protected fun initCommonUI() {
        // Localizar vistas comunes (Toolbar)
        mainAppBar = findViewById(R.id.appBarLayout)
        btnNotifications = findViewById(R.id.btn_notifications)
        imgProfileTop = findViewById(R.id.img_profile_top)
        toolbarLogo = findViewById(R.id.toolbar_logo)

        // Localizar Vistas de Rol (usando los IDs únicos)
        topAdminTabLayout = findViewById(R.id.topAdminTabLayout)
        topUserTabLayout = findViewById(R.id.topUserTabLayout)

        // Inicializar barras según el rol (NUEVA LÓGICA)
        setupRoleBars()

        // Listeners comunes de la barra superior
        setupTopBarListeners()
    }

    private fun setupTopBarListeners() {
        btnNotifications?.setOnClickListener {
            onNotificationClicked()
        }

        imgProfileTop?.setOnClickListener {
            onProfileClicked()
        }

        toolbarLogo?.setOnClickListener {
            navigateHome()
        }
    }

    /**
     * Acción por defecto al pulsar el logo
     */
    protected open fun navigateHome() {
        val intent = Intent(this, com.example.appinterface.MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
    }

    /**
     * Comportamiento por defecto al pulsar notificaciones
     */
    protected open fun onNotificationClicked() {
        android.widget.Toast.makeText(
            this,
            getString(R.string.notifications),
            android.widget.Toast.LENGTH_SHORT
        ).show()
    }

    /**
     * Comportamiento por defecto al pulsar perfil
     * - Si está logueado → Abrir perfil
     * - Si NO está logueado → Mostrar login
     */
    protected open fun onProfileClicked() {
        if (isLoggedIn()) {
            // Usuario logueado → Ir a perfil
            startActivity(Intent(this, ProfileActivity::class.java))
        } else {
            // Usuario NO logueado → Mostrar login
            showLoginBottomSheet()
        }
    }

    /**
     * Muestra el bottom sheet de login
     */
    private fun showLoginBottomSheet() {
        val fm = (this as androidx.fragment.app.FragmentActivity).supportFragmentManager
        val loginBottomSheet = com.example.appinterface.Api.auth.LoginBottomSheetFragment()
        loginBottomSheet.show(fm, "login_bottom_sheet")
    }

    /**
     * Retorna el índice de la pestaña actual (para resaltar en tabs)
     * Debe ser sobreescrito por las subclases
     */
    protected open fun getCurrentTabIndex(): Int? {
        return null
    }

    /**
     * Configura las barras específicas de rol. Este es el NUEVO método principal.
     */
    protected fun setupRoleBars() {
        val admin = isAdmin()
        val designer = isDesigner()
        val loggedIn = isLoggedIn()

        android.util.Log.d("BaseActivity", "=== setupRoleBars INICIO ===")
        android.util.Log.d("BaseActivity", "isAdmin: $admin, isDesigner: $designer, isLoggedIn: $loggedIn")

        if (admin) {
            // ROL: ADMINISTRADOR -> Muestra la barra completa de gestión
            if (topAdminTabLayout != null) {
                setupAdminTabs(topAdminTabLayout!!)
                topUserTabLayout?.let { it.visibility = View.GONE }
            }
        } else if (designer) {
            // ROL: DISEÑADOR -> Por ahora usa la barra de usuario, pero podría usar una propia (designer_bar)
            // La clave es que usa el topUserTabLayout, pero la lógica de la Actividad de Pedidos
            // sabrá darle permisos de admin en esa pantalla específica.
            topUserTabLayout?.let {
                it.visibility = View.VISIBLE
                // Podrías llamar a setupUserTabs(it) aquí si tu barra de usuario tiene pestañas
            }
            topAdminTabLayout?.let { it.visibility = View.GONE }

        } else if (loggedIn) {
            // ROL: USUARIO/CLIENTE -> Muestra la barra de usuario
            topUserTabLayout?.let {
                it.visibility = View.VISIBLE
            }
            topAdminTabLayout?.let { it.visibility = View.GONE }
        } else {
            // ANÓNIMO (Si solo tienes top_app_bar, el topUserTabLayout y topAdminTabLayout estarán ocultos por defecto)
            topUserTabLayout?.let { it.visibility = View.GONE }
            topAdminTabLayout?.let { it.visibility = View.GONE }
        }
        android.util.Log.d("BaseActivity", "=== setupRoleBars FIN ===")
    }

    /**
     * Configura el TabLayout de navegación SOLAMENTE para administradores.
     */
    private fun setupAdminTabs(tabLayout: TabLayout) {
        // 1. Mostrar y configurar pestañas
        tabLayout.visibility = View.VISIBLE
        tabLayout.removeAllTabs()

        // --- AGREGAMOS LAS CUATRO PESTAÑAS ---
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_users)))      // Index 0
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_contacts)))   // Index 1
        tabLayout.addTab(tabLayout.newTab().setText("Pedidos"))                          // Index 2
        tabLayout.addTab(tabLayout.newTab().setText("Personalización"))                  // Index 3

        // 2. Seleccionar la pestaña actual
        val currentTab = getCurrentTabIndex() // Será null en MainActivity, 0 en UsuarioActivity, etc.
        if (currentTab != null && currentTab >= 0 && currentTab < tabLayout.tabCount) {
            tabLayout.getTabAt(currentTab)?.select()
        }

        // 🔥 3. Navegación (Listener debe estar siempre para todas las pantallas Admin)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {

                val targetActivityClass = when (tab.position) {
                    0 -> com.example.appinterface.Api.usuarios.UsuarioActivity::class.java
                    1 -> com.example.appinterface.Api.contacto.ContactListActivity::class.java
                    2 -> com.example.appinterface.Api.pedidos.PedidosActivity::class.java
                    3 -> com.example.appinterface.Api.personalizacion.PersonalizacionActivity::class.java
                    else -> null
                }

                // CRÍTICO: El chequeo a prueba de bucles es el que se mantiene.
                if (targetActivityClass != null && targetActivityClass != this@BaseActivity::class.java) {
                    val intent = Intent(this@BaseActivity, targetActivityClass)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)

                    // Finalizar la Activity actual para que la nueva tome su lugar
                    finish()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })


        // 4. Efecto de desplazamiento (se mantiene)
        if (mainAppBar != null) {
            mainAppBar!!.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
                tabLayout.translationY = verticalOffset * 0.5f
            })
        }
    }

    /**
     * Cierra la sesión del usuario
     */
    protected fun logout() {
        sessionManager.logout()

        // Mostrar mensaje
        android.widget.Toast.makeText(
            this,
            "Sesión cerrada correctamente",
            android.widget.Toast.LENGTH_SHORT
        ).show()

        // Redirigir al MainActivity
        val intent = Intent(this, com.example.appinterface.MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish()
    }
}