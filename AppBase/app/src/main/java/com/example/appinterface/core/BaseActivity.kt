package com.example.appinterface.core

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.appinterface.Api.contacto.ContactActivity
import com.example.appinterface.Api.usuarios.UsuarioActivity

/**
 * Clase base para Activities que usan las barras superior y de pestañas.
 * Centraliza lógica común: inicializar pestañas, mostrar/ocultar según roles,
 * y sincronizar el "ocultado más lento" de la TabLayout con el AppBar principal.
 */
open class BaseActivity : AppCompatActivity() {

    /**
     * Determina si el usuario es ADMIN.
     * Por defecto intenta leer SharedPreferences. Puedes overridear este método
     * en Activities de prueba o cuando cambies tu mecanismo de autenticación.
     */
    protected open fun isAdmin(): Boolean {
        val prefs = getSharedPreferences("brisas_prefs", Context.MODE_PRIVATE)
        val roles = prefs.getStringSet("roles", emptySet())
        return roles?.contains("ADMIN") == true
    }

    /**
     * Inicializa y muestra u oculta las pestañas según isAdmin().
     * @param tabLayout TabLayout ya localizado (findViewById)
     * @param mainAppBar AppBarLayout principal (para sync del offset)
     */
    protected fun setupTabs(tabLayout: TabLayout?, mainAppBar: AppBarLayout?) {
        if (tabLayout == null) return

        val admin = isAdmin()

        if (!admin) {
            tabLayout.visibility = View.GONE
            tabLayout.removeAllTabs()
            return
        }

        // mostrar y poblar pestañas
        tabLayout.visibility = View.VISIBLE
        tabLayout.removeAllTabs()
        tabLayout.addTab(tabLayout.newTab().setText(getString(com.example.appinterface.R.string.tab_users)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(com.example.appinterface.R.string.tab_contacts)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(com.example.appinterface.R.string.tab_orders)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(com.example.appinterface.R.string.tab_custom)))

        // comportamiento por selección: navegación simple (intents)
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

        // sincronizar ocultado más lento: mover TabLayout a mitad de velocidad
        if (mainAppBar != null) {
            mainAppBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
                // verticalOffset es negativo cuando se oculta hacia arriba
                tabLayout.translationY = verticalOffset * 0.5f
            })
        }
    }
}
