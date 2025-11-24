package com.example.appinterface.Api.pedidos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.appinterface.Api.pedidos.data.data.PedidoRepository
import com.example.appinterface.Api.pedidos.model.Pedido
// IMPORTANTE: Agregamos este import que faltaba
import com.example.appinterface.Api.pedidos.model.PedidoRequest
import kotlinx.coroutines.launch

class PedidosViewModel(private val repository: PedidoRepository) : ViewModel() {

    // --- ESTADOS ---
    private val _pedidos = MutableLiveData<Result<List<Pedido>>>()
    val pedidos: LiveData<Result<List<Pedido>>> = _pedidos

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Nuevo estado para mensajes de éxito/error en editar/eliminar
    private val _operacionExitosa = MutableLiveData<String?>()
    val operacionExitosa: LiveData<String?> = _operacionExitosa

    // --- FUNCIONES ---

    fun cargarPedidos() {
        _isLoading.value = true
        viewModelScope.launch {
            val resultado = repository.obtenerPedidos()
            _pedidos.postValue(resultado)
            _isLoading.postValue(false)
        }
    }

    // 👇 AQUÍ ES DONDE DEBEN IR LAS NUEVAS FUNCIONES (Dentro del ViewModel) 👇

    fun actualizarPedido(id: Int, request: PedidoRequest) {
        _isLoading.value = true
        viewModelScope.launch {
            val resultado = repository.actualizarPedido(id, request)
            _isLoading.postValue(false)
            if (resultado.isSuccess) {
                _operacionExitosa.postValue("Pedido actualizado correctamente")
                cargarPedidos() // Recargamos la lista para ver el cambio
            } else {
                _operacionExitosa.postValue("Error: ${resultado.exceptionOrNull()?.message}")
            }
        }
    }

    fun eliminarPedido(id: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            val resultado = repository.eliminarPedido(id)
            _isLoading.postValue(false)
            if (resultado.isSuccess) {
                _operacionExitosa.postValue("Pedido eliminado")
                cargarPedidos() // Recargamos la lista
            } else {
                _operacionExitosa.postValue("Error al eliminar")
            }
        }
    }

    fun limpiarMensaje() {
        _operacionExitosa.value = null
    }

} // <--- AQUÍ SE TERMINA LA CLASE ViewModel

// 👇 LA FÁBRICA VA AFUERA, SEPARADA 👇

class PedidosViewModelFactory(private val repository: PedidoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PedidosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PedidosViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}