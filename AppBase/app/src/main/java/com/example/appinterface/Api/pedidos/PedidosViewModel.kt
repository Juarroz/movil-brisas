// /java/com/example/appinterface/Api/pedidos/PedidosViewModel.kt

package com.example.appinterface.Api.pedidos

import android.util.Log
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
            StatusDTO(4, "4. Diseño Aprobado"),
            StatusDTO(5, "5. Tallado (Producción)"),
            StatusDTO(6, "6. Engaste (Producción)"),
            StatusDTO(7, "7. Pulido (Producción)"),
            StatusDTO(8, "8. Inspección de Calidad"),
            StatusDTO(10, "10. Cancelado")
        )
    }

    private fun cargarDisenadores() {
        repository.getDisenadores(
            onSuccess = { listaCompleta ->

                // FILTRADO CRÍTICO: Solo usuarios cuyo rol sea "diseñador"
                val soloDisenadores = listaCompleta.filter {
                    it.rolNombre?.equals("diseñador", ignoreCase = true) == true
                }

                _disenadores.value = soloDisenadores // Guardar lista filtrada

            },
            onError = { error ->
                Log.e("PedidosViewModel", "Error al cargar diseñadores: $error")
                _disenadores.value = emptyList()
            }
        )
    }

    // Método de acción (llamado desde el Dialog)
    fun actualizarEstado(pedidoId: Int, nuevoEstadoId: Int, comentarios: String) {
        _isLoading.value = true

        repository.actualizarEstado(pedidoId, nuevoEstadoId, comentarios)
            .enqueue(object : Callback<PedidoDTO> {
                override fun onResponse(call: Call<PedidoDTO>, response: Response<PedidoDTO>) {
                    _isLoading.value = false

                    if (response.isSuccessful) {
                        _operacionExitosa.value = "Estado actualizado correctamente"
                        cargarPedidos()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        _errorMessage.value = "Error al actualizar: ${errorBody ?: "Sin detalles"}"
                    }
                }

                override fun onFailure(call: Call<PedidoDTO>, t: Throwable) {
                    _isLoading.value = false
                    _errorMessage.value = "Error de conexión: ${t.message}"
                }
            })
    }

    fun asignarDisenador(pedidoId: Int, usuIdEmpleado: Int) {
        _isLoading.value = true

        repository.asignarDisenador(pedidoId, usuIdEmpleado)
            .enqueue(object : Callback<PedidoDTO> {
                override fun onResponse(call: Call<PedidoDTO>, response: Response<PedidoDTO>) {
                    _isLoading.value = false

                    if (response.isSuccessful) {
                        _operacionExitosa.value = "Diseñador asignado correctamente"
                        cargarPedidos()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        _errorMessage.value = "Error al asignar: ${errorBody ?: "Sin detalles"}"
                    }
                }

                override fun onFailure(call: Call<PedidoDTO>, t: Throwable) {
                    _isLoading.value = false
                    _errorMessage.value = "Error de conexión: ${t.message}"
                }
            })
    }

}