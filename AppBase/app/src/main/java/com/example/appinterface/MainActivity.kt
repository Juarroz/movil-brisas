package com.example.appinterface

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import android.widget.VideoView
import com.example.appinterface.Api.contacto.ContactCreateBottomSheetFragment
import com.example.appinterface.core.BaseActivity
import com.example.appinterface.Api.personalizacion.PersonalizacionActivity
import com.example.appinterface.R

/**
 * MainActivity - Pantalla principal de la aplicación
 *
 */
class MainActivity : BaseActivity() {

    private lateinit var btnFormulario: Button
    private lateinit var btnPersonalizar: Button
    private var videoView: VideoView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔥 CRÍTICO: Usar el layout UNIFICADO para todos los roles
        setContentView(R.layout.activity_main)

        // ELIMINAR: loadLayoutBasedOnRole()
        // loadLayoutBasedOnRole()

        // Inicializar UI común (esto llama a setupRoleBars que oculta/muestra las barras)
        initCommonUI()

        // Inicializar vistas específicas de MainActivity
        initMainViews()

        // Inicializar el video si existe en el layout cargado
        initVideoBackground()
    }

    /**
     * Inicializa el VideoView del header
     */
    private fun initVideoBackground() {
        val view = findViewById<VideoView?>(R.id.videoBackground)
        videoView = view ?: return

        val path = "android.resource://" + packageName + "/" + R.raw.hero_video
        videoView?.setVideoURI(Uri.parse(path))

        videoView?.setOnPreparedListener { mp ->
            mp.isLooping = true
            videoView?.start()
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

    private fun abrirFormularioContacto() {
        // 1. Crear una instancia del Fragment (usando el constructor limpio para el Home)
        val contactSheet = ContactCreateBottomSheetFragment.newInstance(
            resumen = null, // No hay resumen desde el Home
            personalizacionId = null // No hay ID desde el Home
        )

        // 2. Mostrarlo usando el FragmentManager
        // Nota: MainActivity debe heredar de AppCompatActivity o FragmentActivity
        contactSheet.show(supportFragmentManager, ContactCreateBottomSheetFragment.TAG_SHEET)

        // 🔥 ELIMINAR: startActivity(Intent(this, ContactCreateBottomSheetFragment::class.java))
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
        initCommonUI()
        initMainViews()
        initVideoBackground()
        videoView?.start()
    }

    override fun onPause() {
        super.onPause()
        videoView?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        videoView?.stopPlayback()
    }
}
