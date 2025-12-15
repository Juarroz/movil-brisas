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

    private var listaEstados: List<StatusDTO> = emptyList()

    // Componentes del diálogo
    private lateinit var spinnerEstado: Spinner
    private lateinit var etComentarios: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnCancelar: Button

    // 🔥 COMPANION OBJECT PARA CREAR EL FRAGMENTO DE FORMA SEGURA
    companion object {
        private const val ARG_PEDIDO_ID = "PEDIDO_ID"

        fun newInstance(pedidoId: Int): DialogCambiarEstadoFragment {
            val fragment = DialogCambiarEstadoFragment()
            val args = Bundle()
            args.putInt(ARG_PEDIDO_ID, pedidoId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomAlertDialogStyle)

        // LECTURA SIMPLE DE ARGUMENTOS (Asumiendo que newInstance garantiza el Bundle)
        pedidoId = arguments?.getInt(ARG_PEDIDO_ID) ?: 0
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
            listaEstados = it
            setupSpinner(it)
        }

        btnCancelar.setOnClickListener { dismiss() }

        btnGuardar.setOnClickListener {
            val selectedIndex = spinnerEstado.selectedItemPosition
            val comentarios = etComentarios.text.toString().trim()

            // Validar la selección del índice
            if (selectedIndex < 0 || selectedIndex >= listaEstados.size) {
                Toast.makeText(context, "Selecciona un estado válido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Obtener el DTO real por índice
            val selectedStatus = listaEstados[selectedIndex]

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
            estados.map { it.nombre }
        )
        spinnerEstado.adapter = adapter
    }
}