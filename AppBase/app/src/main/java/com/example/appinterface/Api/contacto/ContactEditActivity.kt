package com.example.appinterface.Api.contacto

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appinterface.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContactEditActivity : AppCompatActivity() {

    private val repository = ContactoRepository()
    private var contactoId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_edit)

        contactoId = intent.getLongExtra("contactoId", 0).toInt()

        val txtNotas = findViewById<EditText>(R.id.txtNotas)
        val txtEstado = findViewById<EditText>(R.id.txtEstado)
        val btnActualizar = findViewById<Button>(R.id.btnActualizar)

        btnActualizar.setOnClickListener {
            val datos = mapOf(
                "notas" to txtNotas.text.toString(),
                "estado" to txtEstado.text.toString()
            )

            repository.actualizarContacto(contactoId, datos)
                .enqueue(object : Callback<ContactoFormularioResponseDTO> {
                    override fun onResponse(
                        call: Call<ContactoFormularioResponseDTO>,
                        response: Response<ContactoFormularioResponseDTO>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@ContactEditActivity, "Actualizado correctamente", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@ContactEditActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ContactoFormularioResponseDTO>, t: Throwable) {
                        Toast.makeText(this@ContactEditActivity, "Fallo: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}
