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
import com.example.appinterface.Api.contacto.ContactCreateActivity
import com.example.appinterface.R

class MainActivity : AppCompatActivity() {

    private lateinit var btnNotifications: ImageButton
    private lateinit var imgProfileTop: ImageView
    private lateinit var btnRoles: Button
    private lateinit var btnPedidos: Button
    private lateinit var btnFormulario: Button
    private lateinit var btnGatito: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnNotifications = findViewById(R.id.btn_notifications)
        imgProfileTop = findViewById(R.id.img_profile_top)

        btnRoles = findViewById(R.id.MostrarApikotlin)
        btnPedidos = findViewById(R.id.buttonPedidos)
        btnFormulario = findViewById(R.id.buttonSegundaActividad)
        btnGatito = findViewById(R.id.button)

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
}
