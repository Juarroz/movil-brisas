package com.example.appinterface.Api.pedidos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.appinterface.Api.pedidos.data.HistorialDTO
import com.example.appinterface.Api.pedidos.data.PedidoDTO
import com.example.appinterface.Api.pedidos.data.PedidoRepository
import com.example.appinterface.Api.pedidos.data.PedidoRequestDTO
import kotlinx.coroutines.launch

class PedidoDetailViewModel(private val repository: PedidoRepository) : ViewModel() {

    // Datos Principales del Pedido
    private val _pedido = MutableLiveData<PedidoDTO?>()
    val pedido: LiveData<PedidoDTO?> = _pedido

    // Historial del Pedido (Timeline)
    private val _historial = MutableLiveData<List<HistorialDTO>>()
    val historial: LiveData<List<HistorialDTO>> = _historial

    // Estado y Mensajes
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _operacionExitosa = MutableLiveData<String?>()
    val operacionExitosa: LiveData<String?> = _operacionExitosa

    private var currentPedidoId: Int = 0

    fun start(pedidoId: Int) {
        if (currentPedidoId != pedidoId) {
            currentPedidoId = pedidoId
            cargarDetalles()
        }
    }

    private fun cargarDetalles() {
        if (currentPedidoId == 0) return

        _isLoading.value = true
        viewModelScope.launch {
            // 1. Cargar Pedido
            val pedidoResult = repository.getPedidoById(currentPedidoId)

            // 2. Cargar Historial (Se hace en paralelo o secuencialmente si es necesario)
            val historialResult = repository.getHistorial(currentPedidoId)

            _isLoading.postValue(false)

            if (pedidoResult.isSuccess) {
                _pedido.postValue(pedidoResult.getOrNull())
            } else {
                _errorMessage.postValue("Error al cargar detalles: ${pedidoResult.exceptionOrNull()?.message}")
                _pedido.postValue(null)
            }

            if (historialResult.isSuccess) {
                _historial.postValue(historialResult.getOrNull() ?: emptyList())
            } else {
                // Si el historial falla, el pedido aún se puede mostrar
                _historial.postValue(emptyList())
            }
        }
    }

    fun guardarCambios(request: PedidoRequestDTO) {
        if (currentPedidoId == 0) return

        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            val resultado = repository.actualizarPedido(currentPedidoId, request)
            _isLoading.postValue(false)

            if (resultado.isSuccess) {
                _operacionExitosa.postValue("Pedido actualizado correctamente.")
                cargarDetalles() // Recarga los detalles y el historial
            } else {
                _errorMessage.postValue("Error al guardar: ${resultado.exceptionOrNull()?.message}")
            }
        }
    }

    fun eliminarPedido() {
        if (currentPedidoId == 0) return

        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            val resultado = repository.eliminarPedido(currentPedidoId)
            _isLoading.postValue(false)

            if (resultado.isSuccess) {
                _operacionExitosa.postValue("Pedido eliminado.")
                // No cargamos detalles, la Activity debe cerrarse
            } else {
                _errorMessage.postValue("Error al eliminar: ${resultado.exceptionOrNull()?.message}")
            }
        }
    }

    fun clearMessages() {
        _operacionExitosa.value = null
        _errorMessage.value = null
    }
}

// Factoría para instanciar el ViewModel
class PedidoDetailViewModelFactory(private val repository: PedidoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PedidoDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PedidoDetailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}