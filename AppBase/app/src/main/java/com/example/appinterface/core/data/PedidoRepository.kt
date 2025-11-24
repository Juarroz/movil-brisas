package com.example.appinterface.core.data

import com.example.appinterface.core.ApiServicesKotlin
import com.example.appinterface.core.model.Pedido
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class PedidoRepository(private val api: ApiServicesKotlin) {

    suspend fun obtenerPedidos(): Result<List<Pedido>> = suspendCoroutine { continuation ->
        api.getPedidos().enqueue(object : Callback<List<Pedido>> {
            override fun onResponse(call: Call<List<Pedido>>, response: Response<List<Pedido>>) {
                if (response.isSuccessful) {
                    val pedidos = response.body() ?: emptyList()
                    continuation.resume(Result.success(pedidos))
                } else {
                    continuation.resume(Result.failure(Exception("Error: ${response.code()}")))
                }
            }

            override fun onFailure(call: Call<List<Pedido>>, t: Throwable) {
                continuation.resume(Result.failure(t))
            }
        })
    }

    // ... código anterior (obtenerPedidos) ...

    // Función para actualizar
    suspend fun actualizarPedido(id: Int, request: com.example.appinterface.core.model.PedidoRequest): Result<Boolean> = suspendCoroutine { continuation ->
        api.actualizarPedido(id, request).enqueue(object : Callback<com.example.appinterface.core.model.Pedido> {
            override fun onResponse(call: Call<com.example.appinterface.core.model.Pedido>, response: Response<com.example.appinterface.core.model.Pedido>) {
                if (response.isSuccessful) {
                    continuation.resume(Result.success(true))
                } else {
                    continuation.resume(Result.failure(Exception("Error al actualizar")))
                }
            }

            override fun onFailure(call: Call<com.example.appinterface.core.model.Pedido>, t: Throwable) {
                continuation.resume(Result.failure(t))
            }
        })
    }

    // Función para eliminar
    suspend fun eliminarPedido(id: Int): Result<Boolean> = suspendCoroutine { continuation ->
        api.eliminarPedido(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    continuation.resume(Result.success(true))
                } else {
                    continuation.resume(Result.failure(Exception("Error al eliminar")))
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                continuation.resume(Result.failure(t))
            }
        })
    }

    // ... código anterior ...

    suspend fun crearPedido(request: com.example.appinterface.core.model.PedidoRequest): Result<Boolean> = suspendCoroutine { continuation ->
        api.crearPedido(request).enqueue(object : Callback<com.example.appinterface.core.model.Pedido> {
            override fun onResponse(call: Call<com.example.appinterface.core.model.Pedido>, response: Response<com.example.appinterface.core.model.Pedido>) {
                if (response.isSuccessful) {
                    continuation.resume(Result.success(true))
                } else {
                    continuation.resume(Result.failure(Exception("Error al crear: ${response.code()}")))
                }
            }

            override fun onFailure(call: Call<com.example.appinterface.core.model.Pedido>, t: Throwable) {
                continuation.resume(Result.failure(t))
            }
        })
    }



}