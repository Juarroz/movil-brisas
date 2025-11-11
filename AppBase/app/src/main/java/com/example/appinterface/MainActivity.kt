package com.example.appinterface

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appinterface.Api.auth.LoginActivity
import com.example.appinterface.Api.auth.ProfileActivity
import com.example.appinterface.Api.contacto.ContactActivity
import com.example.appinterface.Api.usuarios.UsuarioActivity
import com.example.appinterface.R

class MainActivity : AppCompatActivity() {

    private lateinit var btnNotifications: ImageButton
    private lateinit var imgProfileTop: ImageView

    // Tus botones existentes
    private lateinit var btnRoles: Button
    private lateinit var btnPedidos: Button
    private lateinit var btnFormulario: Button
    private lateinit var btnGatito: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // referencias UI
        btnNotifications = findViewById(R.id.btn_notifications)
        imgProfileTop = findViewById(R.id.img_profile_top)

        btnRoles = findViewById(R.id.MostrarApikotlin)
        btnPedidos = findViewById(R.id.buttonPedidos)
        btnFormulario = findViewById(R.id.buttonSegundaActividad)
        btnGatito = findViewById(R.id.button)

        // referencias (asegúrate que los ids coinciden con tus layouts)
        val topTabLayout = findViewById<com.google.android.material.tabs.TabLayout>(R.id.topTabLayout)
        val mainAppBar = findViewById<com.google.android.material.appbar.AppBarLayout>(R.id.appBarLayout)

        // TODO: habilitar cuando el login y roles estén implementados
        /*
        val prefs = getSharedPreferences("brisas_prefs", Context.MODE_PRIVATE)
        val roles = prefs.getStringSet("roles", emptySet()) // usar el set que guardes en login
        val isAdmin = roles?.contains("ADMIN") == true
        */

        // Por ahora, puedes forzar isAdmin a false o true según necesites probar
        val isAdmin = true  // cambia a true para ver la barra de pestañas

        if (isAdmin) {
            // mostrar la barra de pestañas
            topTabLayout?.visibility = View.VISIBLE

            // crear pestañas usando strings.xml
            topTabLayout?.apply {
                removeAllTabs()
                addTab(newTab().setText(getString(R.string.tab_users)))
                addTab(newTab().setText(getString(R.string.tab_contacts)))
                addTab(newTab().setText(getString(R.string.tab_orders)))
                addTab(newTab().setText(getString(R.string.tab_custom)))
            }

            // listener para selección (opcional)
            topTabLayout?.addOnTabSelectedListener(object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab) {
                    when (tab.position) {
                        0 -> { val intent = Intent(this@MainActivity, UsuarioActivity::class.java)
                            startActivity(intent)}
                        1 -> { /* Contactos */ }
                        2 -> { /* Pedidos */ }
                        3 -> { /* Personalizaciones */ }
                    }
                }
                override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab) {}
                override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab) {}
            })

            // sincronizar ocultado más lento
            if (mainAppBar != null && topTabLayout != null) {
                mainAppBar.addOnOffsetChangedListener(
                    com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
                        topTabLayout.translationY = verticalOffset * 0.5f
                    }
                )
            }
        } else {
            // ocultar la barra para usuarios normales
            topTabLayout?.visibility = View.GONE
            topTabLayout?.removeAllTabs()
        }

        // listeners existentes
        btnNotifications.setOnClickListener {
            Toast.makeText(this, "Notificaciones (pendiente)", Toast.LENGTH_SHORT).show()
        }

        imgProfileTop.setOnClickListener {
            val prefs = getSharedPreferences("brisas_prefs", Context.MODE_PRIVATE)
            val username = prefs.getString("username", null)
            if (username != null) {
                startActivity(Intent(this, ProfileActivity::class.java))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }

        btnRoles.setOnClickListener { v -> crearmostrarpersonas(v) }
        btnPedidos.setOnClickListener { irAPedidos() }
        btnFormulario.setOnClickListener { abrirFormularioContacto() }
        btnGatito.setOnClickListener { abrirGatito() }
    }

    // Métodos referenciados en XML (mantén firmas para android:onClick)
    fun crearmostrarpersonas(view: View) {
        Toast.makeText(this, "Mostrar roles (ejecución)", Toast.LENGTH_SHORT).show()
    }

    fun irAPedidos() {
        Toast.makeText(this, "Ir a Pedidos (pendiente)", Toast.LENGTH_SHORT).show()
    }

    private fun abrirFormularioContacto() {
        Toast.makeText(this, "Abrir formulario de contacto", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, ContactActivity::class.java))
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
