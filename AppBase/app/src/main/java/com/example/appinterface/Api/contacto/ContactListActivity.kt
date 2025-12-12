package com.example.appinterface.Api.contacto

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import com.example.appinterface.core.BaseActivity
import com.example.appinterface.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContactListActivity : BaseActivity() {

    private lateinit var listContainer: LinearLayout
    private val repository = ContactoRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_list)

        listContainer = findViewById(R.id.listContainer)
        cargarContactos()
        initCommonUI()
    }

    override fun getCurrentTabIndex(): Int = 1

    private fun cargarContactos() {
        listContainer.removeAllViews()

        repository.listarContactos().enqueue(object : Callback<List<ContactoFormularioResponseDTO>> {
            override fun onResponse(
                call: Call<List<ContactoFormularioResponseDTO>>,
                response: Response<List<ContactoFormularioResponseDTO>>
            ) {
                if (response.isSuccessful) {
                    val lista = response.body() ?: emptyList()

                    if (lista.isEmpty()) {
                        val emptyView = LayoutInflater.from(this@ContactListActivity)
                            .inflate(R.layout.item_contact_card, listContainer, false)

                        emptyView.findViewById<TextView>(R.id.txtNombre).text = "No hay contactos"
                        emptyView.findViewById<TextView>(R.id.txtCorreo).visibility = android.view.View.GONE
                        emptyView.findViewById<TextView>(R.id.txtTelefono).visibility = android.view.View.GONE
                        emptyView.findViewById<TextView>(R.id.txtMensaje).visibility = android.view.View.GONE
                        emptyView.findViewById<TextView>(R.id.txtEstado).visibility = android.view.View.GONE
                        emptyView.findViewById<TextView>(R.id.txtNotas).visibility = android.view.View.GONE
                        emptyView.findViewById<TextView>(R.id.txtVia).visibility = android.view.View.GONE
                        emptyView.findViewById<android.view.View>(R.id.btnEdit).visibility = android.view.View.GONE
                        emptyView.findViewById<android.view.View>(R.id.btnDelete).visibility = android.view.View.GONE

                        listContainer.addView(emptyView)
                        return
                    }

                    lista.forEach { contacto ->
                        val itemView = LayoutInflater.from(this@ContactListActivity)
                            .inflate(R.layout.item_contact_card, listContainer, false)

                        itemView.findViewById<TextView>(R.id.txtNombre).text = contacto.nombre ?: ""
                        itemView.findViewById<TextView>(R.id.txtCorreo).text = contacto.correo ?: ""
                        itemView.findViewById<TextView>(R.id.txtTelefono).text = contacto.telefono ?: ""
                        itemView.findViewById<TextView>(R.id.txtMensaje).text = contacto.mensaje ?: ""
                        itemView.findViewById<TextView>(R.id.txtEstado).text = contacto.estado ?: ""
                        itemView.findViewById<TextView>(R.id.txtNotas).text = contacto.notas ?: ""
                        itemView.findViewById<TextView>(R.id.txtVia).text = contacto.via ?: ""

                        val btnEdit = itemView.findViewById<android.view.View>(R.id.btnEdit)
                        val btnDelete = itemView.findViewById<android.view.View>(R.id.btnDelete)

                        btnEdit.setOnClickListener { showEditDialog(contacto) }

                        btnDelete.setOnClickListener {
                            AlertDialog.Builder(this@ContactListActivity)
                                .setTitle("Eliminar")
                                .setMessage("¿Eliminar a ${contacto.nombre}?")
                                .setPositiveButton("Sí") { _, _ ->
                                    repository.eliminarContacto(contacto.id?.toInt() ?: 0)
                                        .enqueue(object : Callback<Void> {
                                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                                if (response.isSuccessful) {
                                                    Toast.makeText(this@ContactListActivity, "Eliminado", Toast.LENGTH_SHORT).show()
                                                    cargarContactos()
                                                } else {
                                                    Toast.makeText(this@ContactListActivity, "Error al eliminar: ${response.code()}", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                                Toast.makeText(this@ContactListActivity, "Fallo: ${t.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        })
                                }
                                .setNegativeButton("Cancelar", null)
                                .show()
                        }

                        listContainer.addView(itemView)
                    }
                } else {
                    Toast.makeText(this@ContactListActivity, "Error al cargar: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ContactoFormularioResponseDTO>>, t: Throwable) {
                Toast.makeText(this@ContactListActivity, "Fallo al cargar: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showEditDialog(contacto: ContactoFormularioResponseDTO) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_contact, null)
        val dialog = AlertDialog.Builder(this).setView(view).create()

        val etNombre = view.findViewById<EditText>(R.id.dialog_txtNombre)
        val etCorreo = view.findViewById<EditText>(R.id.dialog_txtCorreo)
        val etTelefono = view.findViewById<EditText>(R.id.dialog_txtTelefono)
        val etMensaje = view.findViewById<EditText>(R.id.dialog_txtMensaje)
        val etVia = view.findViewById<EditText>(R.id.dialog_txtVia)

        val spinnerEstado = view.findViewById<Spinner>(R.id.spinnerEstadoContacto)
        val etNotas = view.findViewById<EditText>(R.id.dialog_txtNotas)

        val btnActualizar = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.dialog_btnActualizar)
        val btnEliminar = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.dialog_btnEliminar)

        etNombre.setText(contacto.nombre ?: "")
        etCorreo.setText(contacto.correo ?: "")
        etTelefono.setText(contacto.telefono ?: "")
        etMensaje.setText(contacto.mensaje ?: "")
        etVia.setText(contacto.via ?: "")

        etNombre.isEnabled = false
        etCorreo.isEnabled = false
        etTelefono.isEnabled = false
        etMensaje.isEnabled = false
        etVia.isEnabled = false

        val estados = listOf("pendiente", "atendido") // quitamos "finalizado"
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, estados)
        spinnerEstado.adapter = adapter

        val index = estados.indexOf(contacto.estado ?: "pendiente")
        spinnerEstado.setSelection(if (index >= 0) index else 0)

        etNotas.setText(contacto.notas ?: "")

        btnActualizar.setOnClickListener {
            val estadoSeleccionado = spinnerEstado.selectedItem.toString()
            val update = ContactoFormularioUpdateDTO(
                usuarioId = null,
                usuarioIdAdmin = null,
                via = null,
                estado = estadoSeleccionado,
                notas = etNotas.text.toString()
            )

            repository.actualizarContacto(contacto.id?.toInt() ?: 0, update)
                .enqueue(object : Callback<ContactoFormularioResponseDTO> {
                    override fun onResponse(
                        call: Call<ContactoFormularioResponseDTO>,
                        response: Response<ContactoFormularioResponseDTO>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@ContactListActivity, "Actualizado", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                            cargarContactos()
                        } else {
                            val errorBody = response.errorBody()?.string()
                            Toast.makeText(this@ContactListActivity, "Error ${response.code()}: $errorBody", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<ContactoFormularioResponseDTO>, t: Throwable) {
                        Toast.makeText(this@ContactListActivity, "Fallo: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        btnEliminar.setOnClickListener {
            repository.eliminarContacto(contacto.id?.toInt() ?: 0)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@ContactListActivity, "Eliminado", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                            cargarContactos()
                        } else {
                            Toast.makeText(this@ContactListActivity, "Error al eliminar: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(this@ContactListActivity, "Fallo: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        dialog.show()
    }

    override fun isAdmin(): Boolean = true
}
