package com.example.appinterface

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.appinterface.Api.ContactoFormularioRequestDTO
import com.example.appinterface.Api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContactActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // usamos el layout que reciclamos
        setContentView(R.layout.activity_formulario)

        // referenciar vistas
        val txtNombre = findViewById<EditText>(R.id.txtNombre)
        val txtCorreo = findViewById<EditText>(R.id.txtCorreo)
        val txtTelefono = findViewById<EditText>(R.id.txtTelefono)
        val txtMensaje = findViewById<EditText>(R.id.txtMensaje)
        val btnEnviar = findViewById<Button>(R.id.btnEnviar)

        btnEnviar.setOnClickListener {
            val nombre = txtNombre.text.toString().trim()
            val correo = txtCorreo.text.toString().trim()
            val telefono = txtTelefono.text.toString().trim()
            val mensaje = txtMensaje.text.toString().trim()

            // validaciones básicas
            if (nombre.isEmpty() || mensaje.isEmpty()) {
                Toast.makeText(this, "Nombre y mensaje son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (correo.isEmpty() && telefono.isEmpty()) {
                Toast.makeText(this, "Ingresa correo o teléfono", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // armar request
            val request = ContactoFormularioRequestDTO(
                nombre = nombre,
                correo = if (correo.isEmpty()) null else correo,
                telefono = if (telefono.isEmpty()) null else telefono,
                mensaje = mensaje,
                terminos = true,
                via = "formulario"
            )

            // llamada a la API
            RetrofitInstance.api2kotlin.enviarContacto(request)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@ContactActivity,
                                "Contacto enviado correctamente",
                                Toast.LENGTH_SHORT
                            ).show()

                            // limpiar campos
                            txtMensaje.text.clear()
                            // si quieres, también:
                            // txtNombre.text.clear()
                            // txtCorreo.text.clear()
                            // txtTelefono.text.clear()
                        } else {
                            Toast.makeText(
                                this@ContactActivity,
                                "Error: ${response.code()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(
                            this@ContactActivity,
                            "Fallo: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }
}

