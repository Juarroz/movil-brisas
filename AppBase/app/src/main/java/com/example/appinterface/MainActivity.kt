package com.example.appinterface

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.appinterface.Api.contacto.ContactCreateActivity
import com.example.appinterface.core.BaseActivity
import com.example.appinterface.R

class MainActivity : BaseActivity() {

    private lateinit var btnRoles: Button
    private lateinit var btnPedidos: Button
    private lateinit var btnFormulario: Button
    private lateinit var btnGatito: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar UI común (toolbar, tabs, listeners comunes)
        initCommonUI()

        // Referencias propias de esta pantalla
        btnRoles = findViewById(R.id.MostrarApikotlin)
        btnPedidos = findViewById(R.id.buttonPedidos)
        btnFormulario = findViewById(R.id.buttonSegundaActividad)
        btnGatito = findViewById(R.id.button)

        // listeners específicos
        btnRoles.setOnClickListener { v -> crearmostrarpersonas(v) }
        btnPedidos.setOnClickListener { irAPedidos() }
        btnFormulario.setOnClickListener { abrirFormularioContacto() }
        btnGatito.setOnClickListener { abrirGatito() }
    }

    // Mantener firmas usadas desde XML
    fun crearmostrarpersonas(view: View) {
        Toast.makeText(this, "Mostrar roles (ejecución)", Toast.LENGTH_SHORT).show()
    }

    fun irAPedidos() {
        Toast.makeText(this, "Ir a Pedidos (pendiente)", Toast.LENGTH_SHORT).show()
    }

    private fun abrirFormularioContacto() {
        startActivity(Intent(this, ContactCreateActivity::class.java))
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

    // Para fines de pruebas: devolver true (admin). Cambiar según auth real.
    override fun isAdmin(): Boolean = true
}
