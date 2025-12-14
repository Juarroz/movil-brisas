package com.example.appinterface.Api.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.appinterface.Api.usuarios.UsuarioRequestDTO
import com.example.appinterface.Api.usuarios.UsuarioResponseDTO
import com.example.appinterface.R
import com.example.appinterface.core.RetrofitInstance
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * RegisterBottomSheetFragment - Bottom Sheet para registro de usuario
 *
 * Características:
 * - Validación exhaustiva de campos
 * - Campos opcionales (teléfono, documento)
 * - Loading state durante registro
 * - Login automático después del registro
 * - Opción para volver al login
 */
class RegisterBottomSheetFragment : BottomSheetDialogFragment() {

    // Views
    private lateinit var tilCorreo: TextInputLayout
    private lateinit var tilNombre: TextInputLayout
    private lateinit var tilTelefono: TextInputLayout
    private lateinit var tilDocnum: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var tilConfirmPassword: TextInputLayout

    private lateinit var etCorreo: TextInputEditText
    private lateinit var etNombre: TextInputEditText
    private lateinit var etTelefono: TextInputEditText
    private lateinit var etDocnum: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText

    private lateinit var btnRegister: MaterialButton
    private lateinit var tvLoginPrompt: TextView

    private lateinit var authRepository: AuthRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar vistas
        initViews(view)

        // Inicializar repositorio
        val sessionManager = RetrofitInstance.getSessionManager()
        authRepository = AuthRepository(RetrofitInstance.api2kotlin, sessionManager)

        // Configurar listeners
        setupListeners()
    }

    private fun initViews(view: View) {
        // TextInputLayouts
        tilCorreo = view.findViewById(R.id.til_register_email)
        tilNombre = view.findViewById(R.id.til_register_nombre)
        tilTelefono = view.findViewById(R.id.til_register_telefono)
        tilDocnum = view.findViewById(R.id.til_register_docnum)
        tilPassword = view.findViewById(R.id.til_register_password)
        tilConfirmPassword = view.findViewById(R.id.til_register_confirm_password)

        // EditTexts
        etCorreo = view.findViewById(R.id.et_register_email)
        etNombre = view.findViewById(R.id.et_register_nombre)
        etTelefono = view.findViewById(R.id.et_register_telefono)
        etDocnum = view.findViewById(R.id.et_register_docnum)
        etPassword = view.findViewById(R.id.et_register_password)
        etConfirmPassword = view.findViewById(R.id.et_register_confirm_password)

        // Botones y TextViews
        btnRegister = view.findViewById(R.id.btn_register)
        tvLoginPrompt = view.findViewById(R.id.tv_login_prompt)
    }

    private fun setupListeners() {
        // Botón de registro
        btnRegister.setOnClickListener {
            handleRegister()
        }

        // Link para volver al login
        tvLoginPrompt.setOnClickListener {
            dismiss()
            // Abrir LoginBottomSheet
            val loginSheet = LoginBottomSheetFragment()
            loginSheet.show(parentFragmentManager, "LoginBottomSheet")
        }

        // Limpiar errores al escribir
        setupErrorClearListeners()
    }

    private fun setupErrorClearListeners() {
        etCorreo.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) tilCorreo.error = null
        }
        etNombre.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) tilNombre.error = null
        }
        etPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) tilPassword.error = null
        }
        etConfirmPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) tilConfirmPassword.error = null
        }
    }

    private fun handleRegister() {
        // Obtener valores
        val correo = etCorreo.text.toString().trim()
        val nombre = etNombre.text.toString().trim()
        val telefono = etTelefono.text.toString().trim()
        val docnum = etDocnum.text.toString().trim()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        // Validar campos
        if (!validateInputs(correo, nombre, password, confirmPassword)) {
            return
        }

        // Mostrar loading
        setLoadingState(true)

        // Crear DTO para el registro
        val registerRequest = UsuarioRequestDTO(
            nombre = nombre,
            correo = correo,
            telefono = telefono.ifBlank { null },
            password = password,
            docnum = docnum.ifBlank { null },
            rolId = 1,  // Siempre usuario normal en registro
            tipdocId = null,
            origen = "registro",
            activo = true
        )

        // Realizar registro
        authRepository.register(
            registerRequest = registerRequest,
            onSuccess = { response ->
                setLoadingState(false)
                handleRegisterSuccess(correo, password, response)
            },
            onError = { errorMsg ->
                setLoadingState(false)
                handleRegisterError(errorMsg)
            }
        )
    }

    private fun validateInputs(
        correo: String,
        nombre: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        var isValid = true

        // Validar correo
        if (correo.isBlank()) {
            tilCorreo.error = "El correo es obligatorio"
            etCorreo.requestFocus()
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            tilCorreo.error = "Formato de correo inválido"
            etCorreo.requestFocus()
            isValid = false
        }

        // Validar nombre
        if (nombre.isBlank()) {
            tilNombre.error = "El nombre es obligatorio"
            if (isValid) etNombre.requestFocus()
            isValid = false
        } else if (nombre.length < 3) {
            tilNombre.error = "El nombre debe tener al menos 3 caracteres"
            if (isValid) etNombre.requestFocus()
            isValid = false
        }

        // Validar contraseña
        if (password.isBlank()) {
            tilPassword.error = "La contraseña es obligatoria"
            if (isValid) etPassword.requestFocus()
            isValid = false
        } else if (password.length < 8) {
            tilPassword.error = "Mínimo 8 caracteres"
            if (isValid) etPassword.requestFocus()
            isValid = false
        }

        // Validar confirmación de contraseña
        if (confirmPassword.isBlank()) {
            tilConfirmPassword.error = "Confirma tu contraseña"
            if (isValid) etConfirmPassword.requestFocus()
            isValid = false
        } else if (password != confirmPassword) {
            tilConfirmPassword.error = "Las contraseñas no coinciden"
            tilPassword.error = "Las contraseñas no coinciden"
            if (isValid) etConfirmPassword.requestFocus()
            isValid = false
        }

        return isValid
    }

    private fun setLoadingState(isLoading: Boolean) {
        btnRegister.isEnabled = !isLoading
        etCorreo.isEnabled = !isLoading
        etNombre.isEnabled = !isLoading
        etTelefono.isEnabled = !isLoading
        etDocnum.isEnabled = !isLoading
        etPassword.isEnabled = !isLoading
        etConfirmPassword.isEnabled = !isLoading

        btnRegister.text = if (isLoading) {
            "Registrando..."
        } else {
            getString(R.string.action_register)
        }
    }

    private fun handleRegisterSuccess(
        correo: String,
        password: String,
        response: UsuarioResponseDTO
    ) {
        // Mostrar mensaje de éxito
        Toast.makeText(
            requireContext(),
            "¡Cuenta creada exitosamente! Ahora inicia sesión",
            Toast.LENGTH_LONG
        ).show()

        // Abrir el login ANTES de cerrar este sheet
        openLoginSheet(correo)

        // Cerrar el bottom sheet de registro después
        dismiss()
    }

    private fun openLoginSheet(correo: String) {
        // Abrir directamente sin delay ya que dismiss() será después
        try {
            val loginSheet = LoginBottomSheetFragment.newInstance(correo)
            loginSheet.show(parentFragmentManager, "LoginBottomSheet")
        } catch (e: Exception) {
            // Log del error para debug
            e.printStackTrace()
        }
    }

    private fun handleRegisterError(errorMsg: String) {
        // Mostrar error específico
        when {
            errorMsg.contains("correo", ignoreCase = true) ||
                    errorMsg.contains("email", ignoreCase = true) -> {
                tilCorreo.error = "Este correo ya está registrado"
                etCorreo.requestFocus()
            }
            errorMsg.contains("usuario", ignoreCase = true) -> {
                tilCorreo.error = "Usuario ya existe"
                etCorreo.requestFocus()
            }
            else -> {
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        fun newInstance(correo: String? = null): RegisterBottomSheetFragment {
            val fragment = RegisterBottomSheetFragment()
            correo?.let {
                val bundle = Bundle()
                bundle.putString("pre_filled_correo", it)
                fragment.arguments = bundle
            }
            return fragment
        }
    }
}