package com.example.appinterface.core

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.appinterface.Api.auth.LoginActivity
import com.example.appinterface.Api.auth.ProfileActivity
import com.example.appinterface.Api.usuarios.UsuarioActivity
import com.example.appinterface.R
import com.example.appinterface.Api.contacto.ContactListActivity
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

    // Vistas comunes accesibles por subclases
    protected var topTabLayout: TabLayout? = null
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
        // Localizar vistas (pueden ser null si layout no las contiene)
        topTabLayout = findViewById(R.id.topTabLayout)
        mainAppBar = findViewById(R.id.appBarLayout)
        btnNotifications = findViewById(R.id.btn_notifications)
        imgProfileTop = findViewById(R.id.img_profile_top)
        toolbarLogo = findViewById(R.id.toolbar_logo)

        // Inicializar tabs (si existe TabLayout)
        setupTabs(topTabLayout, mainAppBar)

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
     * Configura las pestañas de navegación (solo para admins)
     */
    protected fun setupTabs(tabLayout: TabLayout?, mainAppBar: AppBarLayout?) {
        if (tabLayout == null) return

        val admin = isAdmin()

        if (!admin) {
            // Si no es admin, ocultar las tabs
            tabLayout.visibility = View.GONE
            tabLayout.removeAllTabs()
            return
        }

        // Es admin → Mostrar tabs
        tabLayout.visibility = View.VISIBLE
        tabLayout.removeAllTabs()
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_users)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_contacts)))
        // tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_orders)))
        // tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_custom)))

        // Seleccionar la pestaña actual
        val currentTab = getCurrentTabIndex()
        if (currentTab != null && currentTab >= 0 && currentTab < tabLayout.tabCount) {
            tabLayout.getTabAt(currentTab)?.select()
        }

        // Navegación por selección de pestaña
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> startActivity(Intent(this@BaseActivity, UsuarioActivity::class.java))
                    1 -> startActivity(Intent(this@BaseActivity, ContactListActivity::class.java))
                    2 -> {
                        // TODO: Start PedidosActivity cuando exista
                    }
                    3 -> {
                        // TODO: Start PersonalizacionesActivity cuando exista
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        // Efecto: las tabs se mueven a media velocidad respecto al appbar
        if (mainAppBar != null) {
            mainAppBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
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
