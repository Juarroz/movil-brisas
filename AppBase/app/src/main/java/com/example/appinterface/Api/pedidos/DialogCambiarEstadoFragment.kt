// /java/com/example/appinterface/Api/pedidos/DialogCambiarEstadoFragment.kt

package com.example.appinterface.Api.pedidos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.appinterface.Api.pedidos.data.StatusDTO
import com.example.appinterface.R


class DialogCambiarEstadoFragment : DialogFragment() {

    private lateinit var viewModel: PedidosViewModel
    private var pedidoId: Int = 0

    // Componentes del diálogo
    private lateinit var spinnerEstado: Spinner
    private lateinit var etComentarios: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnCancelar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Usar estilo de diálogo más amplio
        setStyle(STYLE_NORMAL, R.style.CustomAlertDialogStyle)

        // Obtener ID del pedido
        pedidoId = arguments?.getInt("PEDIDO_ID") ?: 0
        if (pedidoId == 0) {
            Toast.makeText(context, "Error: ID de pedido no proporcionado.", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflar el layout que te daré luego (dialog_cambiar_estado.xml)
        return inflater.inflate(R.layout.dialog_cambiar_estado, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Asignar el ViewModel de la Activity (PedidosActivity)
        viewModel = ViewModelProvider(requireActivity()).get(PedidosViewModel::class.java)

        // Vincular vistas
        spinnerEstado = view.findViewById(R.id.spinnerNuevoEstado)
        etComentarios = view.findViewById(R.id.etComentariosEstado)
        btnGuardar = view.findViewById(R.id.btnGuardarEstado)
        btnCancelar = view.findViewById(R.id.btnCancelarEstado)

        // Cargar datos en el Spinner y observar
        viewModel.estadosDisponibles.observe(viewLifecycleOwner) {
            setupSpinner(it)
        }

        btnCancelar.setOnClickListener { dismiss() }

        btnGuardar.setOnClickListener {
            val selectedStatus = spinnerEstado.selectedItem as StatusDTO
            val comentarios = etComentarios.text.toString().trim()

            if (selectedStatus.id == 0) {
                Toast.makeText(context, "Selecciona un estado válido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Llamada al ViewModel para ejecutar la acción
            viewModel.actualizarEstado(
                pedidoId = pedidoId,
                nuevoEstadoId = selectedStatus.id,
                comentarios = comentarios
            )
            dismiss()
        }
    }

    private fun setupSpinner(estados: List<StatusDTO>) {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            estados.map { it.nombre } // Mostrar solo el nombre
        )
        spinnerEstado.adapter = adapter

        // Si quieres preseleccionar el estado actual, necesitas pasarlo en el Bundle
    }
}