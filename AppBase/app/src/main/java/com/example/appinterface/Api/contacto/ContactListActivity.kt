package com.example.appinterface.Api.contacto

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appinterface.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContactListActivity : AppCompatActivity() {

    private val repository = ContactoRepository()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ContactAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_list)

        recyclerView = findViewById(R.id.recyclerViewContacts)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ContactAdapter(listOf(),
            onEditClick = { contacto -> editarContacto(contacto) },
            onDeleteClick = { contacto -> eliminarContacto(contacto.id ?: 0L) }
        )
        recyclerView.adapter = adapter

        cargarContactos()
    }

    private fun cargarContactos() {
        repository.listarContactos().enqueue(object : Callback<List<ContactoFormularioResponseDTO>> {
            override fun onResponse(
                call: Call<List<ContactoFormularioResponseDTO>>,
                response: Response<List<ContactoFormularioResponseDTO>>
            ) {
                if (response.isSuccessful) {
                    adapter.actualizarLista(response.body() ?: listOf())
                } else {
                    Toast.makeText(this@ContactListActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ContactoFormularioResponseDTO>>, t: Throwable) {
                Toast.makeText(this@ContactListActivity, "Fallo: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun editarContacto(contacto: ContactoFormularioResponseDTO) {
        val intent = Intent(this, ContactEditActivity::class.java)
        intent.putExtra("contactoId", contacto.id)
        startActivity(intent)
    }

    private fun eliminarContacto(id: Long) {
        repository.eliminarContacto(id.toInt()).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ContactListActivity, "Eliminado correctamente", Toast.LENGTH_SHORT).show()
                    cargarContactos()
                } else {
                    Toast.makeText(this@ContactListActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@ContactListActivity, "Fallo: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
