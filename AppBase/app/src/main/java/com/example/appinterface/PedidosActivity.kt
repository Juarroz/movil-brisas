package com.example.appinterface

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appinterface.Adapter.PedidoAdapter
import com.example.appinterface.Api.PedidoResponseDTO
import com.example.appinterface.Api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PedidosActivity : AppCompatActivity() {

    private lateinit var rvPedidos: RecyclerView
    private lateinit var progressPedidos: ProgressBar
    private lateinit var tvEmptyPedidos: TextView
    private lateinit var adapter: PedidoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedidos)

        rvPedidos = findViewById(R.id.rvPedidos)
        progressPedidos = findViewById(R.id.progressPedidos)
        tvEmptyPedidos = findViewById(R.id.tvEmptyPedidos)

        adapter = PedidoAdapter()
        rvPedidos.layoutManager = LinearLayoutManager(this)
        rvPedidos.adapter = adapter

        fetchPedidos()
    }

    private fun showLoading(show: Boolean) {
        progressPedidos.visibility = if (show) View.VISIBLE else View.GONE
        rvPedidos.visibility = if (show) View.GONE else View.VISIBLE
        if (show) tvEmptyPedidos.visibility = View.GONE
    }

    private fun fetchPedidos() {
        showLoading(true)
        val call: Call<List<PedidoResponseDTO>> = RetrofitInstance.api2kotlin.getPedidos()
        call.enqueue(object : Callback<List<PedidoResponseDTO>> {
            override fun onResponse(call: Call<List<PedidoResponseDTO>>, response: Response<List<PedidoResponseDTO>>) {
                showLoading(false)
                if (response.isSuccessful) {
                    val lista = response.body().orEmpty()
                    if (lista.isEmpty()) {
                        tvEmptyPedidos.visibility = View.VISIBLE
                        adapter.updateData(emptyList())
                        Toast.makeText(this@PedidosActivity, "No hay pedidos", Toast.LENGTH_SHORT).show()
                    } else {
                        tvEmptyPedidos.visibility = View.GONE
                        adapter.updateData(lista)
                        Toast.makeText(this@PedidosActivity, "Pedidos cargados: ${lista.size}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    tvEmptyPedidos.visibility = View.VISIBLE
                    Toast.makeText(this@PedidosActivity, "Error servidor: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<PedidoResponseDTO>>, t: Throwable) {
                showLoading(false)
                tvEmptyPedidos.visibility = View.VISIBLE
                Toast.makeText(this@PedidosActivity, "Fallo: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
