package com.example.appinterface.Api.contacto

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.appinterface.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContactActivity : AppCompatActivity() {

    private lateinit var txtNombre: EditText
    private lateinit var txtCorreo: EditText
    private lateinit var txtTelefono: EditText
    private lateinit var txtMensaje: EditText
    private lateinit var btnEnviar: Button
    private lateinit var listViewContactos: ListView

    private val repository = ContactoRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contactos)

        txtNombre = findViewById(R.id.txtNombre)
        txtCorreo = findViewById(R.id.txtCorreo)
        txtTelefono = findViewById(R.id.txtTelefono)
        txtMensaje = findViewById(R.id.txtMensaje)
        btnEnviar = findViewById(R.id.btnEnviar)
        listViewContactos = findViewById(R.id.listViewContactos)

        btnEnviar.setOnClickListener { enviarContacto() }
        listarContactos()
    }

    // ======================
    // ENVIAR CONTACTO
    // ======================
    private fun enviarContacto() {
        val nombre = txtNombre.text.toString().trim()
        val correo = txtCorreo.text.toString().trim()
        val telefono = txtTelefono.text.toString().trim()
        val mensaje = txtMensaje.text.toString().trim()

        if (nombre.isEmpty() || mensaje.isEmpty()) {
            Toast.makeText(this, "Nombre y mensaje son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }
        if (correo.isEmpty() && telefono.isEmpty()) {
            Toast.makeText(this, "Ingresa correo o teléfono", Toast.LENGTH_SHORT).show()
            return
        }

        val request = ContactoFormularioRequestDTO(
            nombre = nombre,
            correo = if (correo.isEmpty()) null else correo,
            telefono = if (telefono.isEmpty()) null else telefono,
            mensaje = mensaje,
            terminos = true,
            via = "formulario"
        )

        repository.enviarContacto(request).enqueue(object : Callback<ContactoFormularioResponseDTO> {
            override fun onResponse(
                call: Call<ContactoFormularioResponseDTO>,
                response: Response<ContactoFormularioResponseDTO>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ContactActivity, "Contacto enviado correctamente", Toast.LENGTH_SHORT).show()
                    limpiarCampos()
                    listarContactos()
                } else {
                    Toast.makeText(this@ContactActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ContactoFormularioResponseDTO>, t: Throwable) {
                Toast.makeText(this@ContactActivity, "Fallo: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // ======================
    // LISTAR CONTACTOS
    // ======================
    private fun listarContactos() {
        repository.listarContactos().enqueue(object : Callback<List<ContactoFormularioResponseDTO>> {
            override fun onResponse(
                call: Call<List<ContactoFormularioResponseDTO>>,
                response: Response<List<ContactoFormularioResponseDTO>>
            ) {
                if (response.isSuccessful) {
                    val contactos = response.body() ?: emptyList()
                    val adapter = ArrayAdapter(
                        this@ContactActivity,
                        android.R.layout.simple_list_item_1,
                        contactos.map { "${it.nombre} - ${it.estado ?: "pendiente"}" }
                    )
                    listViewContactos.adapter = adapter

                    listViewContactos.setOnItemClickListener { _, _, position, _ ->
                        mostrarOpcionesContacto(contactos[position])
                    }
                } else {
                    Toast.makeText(this@ContactActivity, "Error al listar: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ContactoFormularioResponseDTO>>, t: Throwable) {
                Toast.makeText(this@ContactActivity, "Fallo al listar: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // ======================
    // OPCIONES POR CONTACTO
    // ======================
    private fun mostrarOpcionesContacto(contacto: ContactoFormularioResponseDTO) {
        val opciones = arrayOf("Actualizar estado", "Eliminar contacto")
        AlertDialog.Builder(this)
            .setTitle(contacto.nombre)
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> mostrarDialogActualizar(contacto)
                    1 -> eliminarContacto(contacto)
                }
            }
            .show()
    }

    // ======================
    // ACTUALIZAR CONTACTO
    // ======================
    private fun mostrarDialogActualizar(contacto: ContactoFormularioResponseDTO) {
        val input = EditText(this)
        input.hint = "Nuevo estado (pendiente, atendido, archivado)"

        AlertDialog.Builder(this)
            .setTitle("Actualizar estado")
            .setView(input)
            .setPositiveButton("Actualizar") { _, _ ->
                val estado = input.text.toString().trim()
                if (estado.isNotEmpty()) {
                    val datos = mapOf("estado" to estado)
                    repository.actualizarContacto(contacto.id?.toInt() ?: 0, datos)
                        .enqueue(object : Callback<ContactoFormularioResponseDTO> {
                            override fun onResponse(
                                call: Call<ContactoFormularioResponseDTO>,
                                response: Response<ContactoFormularioResponseDTO>
                            ) {
                                if (response.isSuccessful) {
                                    Toast.makeText(this@ContactActivity, "Actualizado correctamente", Toast.LENGTH_SHORT).show()
                                    listarContactos()
                                } else {
                                    Toast.makeText(this@ContactActivity, "Error al actualizar: ${response.code()}", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onFailure(call: Call<ContactoFormularioResponseDTO>, t: Throwable) {
                                Toast.makeText(this@ContactActivity, "Fallo al actualizar: ${t.message}", Toast.LENGTH_SHORT).show()
                            }
                        })
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // ======================
    // ELIMINAR CONTACTO
    // ======================
    private fun eliminarContacto(contacto: ContactoFormularioResponseDTO) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar contacto")
            .setMessage("¿Estás seguro de eliminar a ${contacto.nombre}?")
            .setPositiveButton("Sí") { _, _ ->
                repository.eliminarContacto(contacto.id?.toInt() ?: 0)
                    .enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@ContactActivity, "Eliminado correctamente", Toast.LENGTH_SHORT).show()
                                listarContactos()
                            } else {
                                Toast.makeText(this@ContactActivity, "Error al eliminar: ${response.code()}", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Toast.makeText(this@ContactActivity, "Fallo al eliminar: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // ======================
    // LIMPIAR CAMPOS
    // ======================
    private fun limpiarCampos() {
        txtNombre.text.clear()
        txtCorreo.text.clear()
        txtTelefono.text.clear()
        txtMensaje.text.clear()
    }
}
