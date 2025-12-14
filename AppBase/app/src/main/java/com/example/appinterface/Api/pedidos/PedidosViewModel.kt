// /java/com/example/appinterface/Api/pedidos/PedidosViewModel.kt

package com.example.appinterface.Api.pedidos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.appinterface.Api.pedidos.data.data.PedidoRepository
import com.example.appinterface.Api.pedidos.model.Pedido
import com.example.appinterface.Api.pedidos.model.PedidoRequest
import kotlinx.coroutines.launch

class PedidosViewModel(private val repository: PedidoRepository) : ViewModel() {

    // --- ESTADOS ---
    // 🔥 CAMBIO: LiveData ahora solo maneja la lista, los errores se manejan por separado
    private val _pedidos = MutableLiveData<List<Pedido>>()
    val pedidos: LiveData<List<Pedido>> = _pedidos

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _operacionExitosa = MutableLiveData<String?>()
    val operacionExitosa: LiveData<String?> = _operacionExitosa

    init {
        // La carga inicial se mantiene
        cargarPedidos()
    }

    // --- FUNCIONES ---

    fun cargarPedidos() {
        _isLoading.value = true
        _errorMessage.value = null // Limpiar errores anteriores

        // 🔥 CRÍTICO: Usar el método centralizado y segmentado
        repository.getPedidosByRole(
            onSuccess = { listaPedidos ->
                _pedidos.value = listaPedidos
                _isLoading.value = false
            },
            onError = { error ->
                _errorMessage.value = error
                _pedidos.value = emptyList() // Limpiar lista en caso de error
                _isLoading.value = false
            }
        )
    }

    // Funciones CRUD mantenidas (usan Coroutines y suspends)

    fun actualizarPedido(id: Int, request: PedidoRequest) {
        _isLoading.value = true
        viewModelScope.launch {
            val resultado = repository.actualizarPedido(id, request)
            _isLoading.postValue(false)
            if (resultado.isSuccess) {
                _operacionExitosa.postValue("Pedido actualizado correctamente")
                cargarPedidos() // Recargamos la lista con la nueva información
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

}