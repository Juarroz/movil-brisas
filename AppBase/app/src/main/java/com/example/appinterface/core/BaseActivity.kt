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
import com.example.appinterface.Api.contacto.ContactActivity
import com.example.appinterface.Api.usuarios.UsuarioActivity
import com.example.appinterface.R
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout

/**
 * BaseActivity: centraliza la UI y la lógica común de top bar y tabs.
 *
 * Uso:
 *  - En la Activity hija, después de setContentView(...) llamar initCommonUI()
 *  - Override isAdmin() según el mecanismo de auth si es necesario.
 */
open class BaseActivity : AppCompatActivity() {

    // Vistas comunes accesibles por subclases
    protected var topTabLayout: TabLayout? = null
    protected var mainAppBar: AppBarLayout? = null
    protected var btnNotifications: ImageButton? = null
    protected var imgProfileTop: ImageView? = null

    protected open fun isAdmin(): Boolean {
        val prefs = getSharedPreferences("brisas_prefs", Context.MODE_PRIVATE)
        val roles = prefs.getStringSet("roles", emptySet())
        return roles?.contains("ADMIN") == true
    }

    /**
     * Inicializa las vistas comunes. LLAMAR desde la Activity hija
     * **después** de setContentView(...).
     */
    protected fun initCommonUI() {
        // localizar vistas (pueden ser null si layout no las contiene)
        topTabLayout = findViewById(R.id.topTabLayout)
        mainAppBar = findViewById(R.id.appBarLayout)
        btnNotifications = findViewById(R.id.btn_notifications)
        imgProfileTop = findViewById(R.id.img_profile_top)

        // inicializar tabs (si existe TabLayout)
        setupTabs(topTabLayout, mainAppBar)

        // listeners comunes de la barra superior
        setupTopBarListeners()
    }

    /**
     * Configura listeners por defecto para los botones de la toolbar.
     * Subclases pueden sobrescribir onNotificationClicked() u onProfileClicked().
     */
    private fun setupTopBarListeners() {
        btnNotifications?.setOnClickListener {
            onNotificationClicked()
        }

        imgProfileTop?.setOnClickListener {
            onProfileClicked()
        }
    }

    // Comportamiento por defecto al pulsar notificaciones (se puede override)
    protected open fun onNotificationClicked() {
        // por defecto: mostrar toast ligero
        android.widget.Toast.makeText(this, getString(R.string.notifications), android.widget.Toast.LENGTH_SHORT).show()
    }

    // Comportamiento por defecto al pulsar perfil: abrir Profile o Login
    protected open fun onProfileClicked() {
        val prefs = getSharedPreferences("brisas_prefs", Context.MODE_PRIVATE)
        val username = prefs.getString("username", null)
        if (username != null) {
            startActivity(Intent(this, ProfileActivity::class.java))
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    /**
     * Inicializa y muestra u oculta las pestañas según isAdmin().
     * Mantuve tu implementación funcional y añadí pequeños comentarios.
     */
    protected fun setupTabs(tabLayout: TabLayout?, mainAppBar: AppBarLayout?) {
        if (tabLayout == null) return

        val admin = isAdmin()

        if (!admin) {
            tabLayout.visibility = View.GONE
            tabLayout.removeAllTabs()
            return
        }

        tabLayout.visibility = View.VISIBLE
        tabLayout.removeAllTabs()
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_users)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_contacts)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_orders)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_custom)))

        // navegación simple por selección de pestaña
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> startActivity(Intent(this@BaseActivity, UsuarioActivity::class.java))
                    1 -> startActivity(Intent(this@BaseActivity, ContactActivity::class.java))
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

        // efecto: las tabs se mueven a media velocidad respecto al appbar
        if (mainAppBar != null) {
            mainAppBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
                tabLayout.translationY = verticalOffset * 0.5f
            })
        }
    }
}
