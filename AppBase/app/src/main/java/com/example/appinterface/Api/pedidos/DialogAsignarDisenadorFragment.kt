package com.example.appinterface.Api.pedidos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.appinterface.R
import com.example.appinterface.Api.usuarios.data.EmpleadoDTO

class DialogAsignarDisenadorFragment : DialogFragment() {

    private lateinit var viewModel: PedidosViewModel
    private var pedidoId: Int = 0

    // Componentes del diálogo
    private lateinit var spinnerDisenador: Spinner
    private lateinit var btnAsignar: Button
    private lateinit var btnCancelar: Button

    // Lista para el Adapter, para obtener el ID real
    private var listaDisenadores: List<EmpleadoDTO> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomAlertDialogStyle)

        // Obtener ID del pedido
        pedidoId = arguments?.getInt("PEDIDO_ID") ?: 0
        if (pedidoId == 0) {
            Toast.makeText(context, "Error: ID de pedido no proporcionado.", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflar el layout (dialog_asignar_disenador.xml)
        return inflater.inflate(R.layout.dialog_asignar_disenador, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Asignar el ViewModel de la Activity
        viewModel = ViewModelProvider(requireActivity()).get(PedidosViewModel::class.java)

        // Vincular vistas
        spinnerDisenador = view.findViewById(R.id.spinnerDisenador)
        btnAsignar = view.findViewById(R.id.btnAsignarDisenador)
        btnCancelar = view.findViewById(R.id.btnCancelarAsignacion)

        // Cargar datos en el Spinner y observar
        viewModel.disenadores.observe(viewLifecycleOwner) {
            listaDisenadores = it
            setupSpinner(it)
        }

        btnCancelar.setOnClickListener { dismiss() }

        btnAsignar.setOnClickListener {
            val selectedIndex = spinnerDisenador.selectedItemPosition

            // Asumiendo que el Index 0 es "Seleccionar un diseñador..." o similar
            if (selectedIndex <= 0 || selectedIndex > listaDisenadores.size) {
                Toast.makeText(context, "Selecciona un diseñador.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔥 Obtenemos el ID del diseñador (el índice es +1 por el primer item 'Seleccionar')
            val diseñadorSeleccionado = listaDisenadores[selectedIndex - 1]
            val nuevoEmpleadoId = diseñadorSeleccionado.id

            // Llamada al ViewModel para ejecutar la acción
            viewModel.asignarDisenador(
                pedidoId = pedidoId,
                usuIdEmpleado = nuevoEmpleadoId
            )
            dismiss()
        }
    }

    private fun setupSpinner(disenadores: List<EmpleadoDTO>) {
        // Creamos la lista para el ArrayAdapter, incluyendo la opción de placeholder
        val nombres = mutableListOf("Seleccionar un diseñador...")
        nombres.addAll(disenadores.map { "${it.nombre} (${it.correo})" })

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            nombres
        )
        spinnerDisenador.adapter = adapter
    }
}