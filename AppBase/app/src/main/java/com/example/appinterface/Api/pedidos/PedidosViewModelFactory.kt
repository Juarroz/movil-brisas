package com.example.appinterface.Api.pedidos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appinterface.Api.pedidos.data.data.PedidoRepository // Necesita esta importación

class PedidosViewModelFactory(private val repository: PedidoRepository) : ViewModelProvider.Factory {

    // Sobreescribe el método 'create'
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Verifica si la clase solicitada es PedidosViewModel
        if (modelClass.isAssignableFrom(PedidosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            // Retorna una nueva instancia de PedidosViewModel con el repositorio inyectado
            return PedidosViewModel(repository) as T
        }
        // Si se solicita otra clase, lanza un error
        throw IllegalArgumentException("ViewModel desconocido solicitado")
    }
}