// /java/com/example/appinterface/Api/pedidos/PedidosViewModel.kt

package com.example.appinterface.Api.pedidos

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appinterface.Api.pedidos.data.PedidoRepository
import com.example.appinterface.Api.pedidos.data.StatusDTO
import com.example.appinterface.Api.pedidos.data.PedidoDTO
import com.example.appinterface.Api.pedidos.data.PedidoRequestDTO
import com.example.appinterface.Api.usuarios.data.EmpleadoDTO
import kotlinx.coroutines.launch

class PedidosViewModel(private val repository: PedidoRepository) : ViewModel() {

    // --- ESTADOS ---
    // 🔥 CAMBIO: LiveData ahora solo maneja la lista, los errores se manejan por separado
    private val _pedidos = MutableLiveData<List<PedidoDTO>>()
    val pedidos: LiveData<List<PedidoDTO>> = _pedidos

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _operacionExitosa = MutableLiveData<String?>()
    val operacionExitosa: LiveData<String?> = _operacionExitosa

    private val _estadosDisponibles = MutableLiveData<List<StatusDTO>>()
    val estadosDisponibles: LiveData<List<StatusDTO>> = _estadosDisponibles

    private val _disenadores = MutableLiveData<List<EmpleadoDTO>>()
    val disenadores: LiveData<List<EmpleadoDTO>> = _disenadores

    init {
        cargarEstados()
        cargarDisenadores()
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

    fun actualizarPedido(id: Int, request: PedidoRequestDTO) {
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

    private fun cargarEstados() {
        // Implementar la llamada a la API que devuelva la lista de StatusDTO
        // Por ahora, usaremos datos quemados (Hardcoded) si no tienes el endpoint de API.
        // Si tu API no los proporciona, usa esta lista temporal:
        _estadosDisponibles.value = listOf(
            StatusDTO(1, "1. Cotización Pendiente"),
            StatusDTO(2, "2. Pago Diseño Pendiente"),
            StatusDTO(3, "3. Diseño en Proceso"),
            StatusDTO(4, "4. Producción (Tallado)"),
            StatusDTO(5, "5. Control de Calidad"),
            StatusDTO(6, "6. Empaque"),
            StatusDTO(7, "7. En Tránsito"),
            StatusDTO(8, "8. Revisión Final"),
            StatusDTO(10, "10. Cancelado")
        )
    }

    private fun cargarDisenadores() {
        // Implementar la llamada a la API que devuelva la lista de EmpleadoDTO (GET /usuarios/empleados)
        // Por ahora, datos quemados:
        _disenadores.value = listOf(
            EmpleadoDTO(6, "Doña Doloritas", "doloritas@brisas.com"),
            EmpleadoDTO(7, "Miguel Paramo", "miguel@brisas.com"),
            EmpleadoDTO(8, "Eduviges Dyada", "eduviges@brisas.com")
        )
    }

    // Método de acción (llamado desde el Dialog)
    fun actualizarEstado(pedidoId: Int, nuevoEstadoId: Int, comentarios: String) {
        // ... Lógica para establecer _isLoading.value = true

        repository.actualizarEstado(pedidoId, nuevoEstadoId, comentarios)
            .enqueue(object : Callback<PedidoDTO> {
                override fun onResponse(call: Call<PedidoDTO>, response: Response<PedidoDTO>) {
                    if (response.isSuccessful) {
                        // Notificar éxito
                        // 🔥 NOTA: Aquí deberías actualizar la lista de pedidos y/o la UI
                        _operacionExitosa.value = "Estado de Pedido ${response.body()?.pedCodigo} actualizado."
                        cargarPedidos() // Recargar la lista completa
                    } else {
                        _errorMessage.value = "Error al actualizar estado: ${response.code()}"
                    }
                }
                override fun onFailure(call: Call<PedidoDTO>, t: Throwable) {
                    _errorMessage.value = "Fallo de red al cambiar estado: ${t.message}"
                }
            })
    }

    fun asignarDisenador(pedidoId: Int, usuIdEmpleado: Int) {
        // ... Lógica para establecer _isLoading.value = true

        repository.asignarDisenador(pedidoId, usuIdEmpleado)
            .enqueue(object : Callback<PedidoDTO> {
                override fun onResponse(call: Call<PedidoDTO>, response: Response<PedidoDTO>) {
                    if (response.isSuccessful) {
                        // Notificar éxito
                        _operacionExitosa.value = "Diseñador asignado al Pedido ${response.body()?.pedCodigo}."
                        cargarPedidos() // Recargar la lista completa para reflejar el cambio
                    } else {
                        _errorMessage.value = "Error al asignar diseñador: ${response.code()}"
                    }
                }
                override fun onFailure(call: Call<PedidoDTO>, t: Throwable) {
                    _errorMessage.value = "Fallo de red al asignar diseñador: ${t.message}"
                }
            })
    }

}