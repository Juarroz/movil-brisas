package com.example.appinterface.Api.auth


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.appinterface.Api.usuarios.UsuarioActivity
import com.example.appinterface.MainActivity
import com.example.appinterface.R
import com.example.appinterface.core.RetrofitInstance
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

/**
 * LoginBottomSheetFragment - Bottom Sheet para iniciar sesión
 *
 * Características:
 * - Validación de campos
 * - Loading state durante login
 * - Manejo de errores del backend
 * - Navegación automática según rol del usuario
 * - Opción para registrarse
 * - Opción para recuperar contraseña
 * - Soporte para pre-llenar correo (cuando viene de registro)
 */
class LoginBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var tvRegisterPrompt: TextView
    private lateinit var tvForgotPassword: TextView
    private lateinit var progressBar: ProgressBar

    private lateinit var authRepository: AuthRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar vistas
        etUsername = view.findViewById(R.id.et_username_sheet)
        etPassword = view.findViewById(R.id.et_password_sheet)
        btnLogin = view.findViewById(R.id.btn_login_sheet)
        tvRegisterPrompt = view.findViewById(R.id.tv_register_prompt)
        tvForgotPassword = view.findViewById(R.id.tv_forgot_password)

        // 🆕 Pre-llenar el correo si viene de registro
        arguments?.getString("pre_filled_email")?.let { correo ->
            etUsername.setText(correo)
            etPassword.requestFocus()  // Enfocar en password para que usuario solo escriba eso

            // Mostrar un hint visual (opcional)
            Toast.makeText(
                requireContext(),
                "Ahora ingresa tu contraseña",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Crear ProgressBar programáticamente (o añádelo al layout)
        progressBar = ProgressBar(requireContext()).apply {
            visibility = View.GONE
            isIndeterminate = true
        }

        // Inicializar repositorio
        val sessionManager = RetrofitInstance.getSessionManager()
        authRepository = AuthRepository(RetrofitInstance.api2kotlin, sessionManager)

        // Configurar listeners
        setupListeners()
    }

    private fun setupListeners() {
        // Botón de login
        btnLogin.setOnClickListener {
            handleLogin()
        }

        // Link de registro - Cerrar este sheet y abrir el de registro
        tvRegisterPrompt.setOnClickListener {
            dismiss()

            // Abrir el registro sheet
            val registerSheet = RegisterBottomSheetFragment()
            registerSheet.show(parentFragmentManager, "RegisterBottomSheet")
        }

        // Link de recuperar contraseña
        tvForgotPassword.setOnClickListener {
            // TODO: Abrir ForgotPasswordBottomSheetFragment (FASE 3)
            Toast.makeText(requireContext(), "Recuperación disponible pronto", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleLogin() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // Validar campos
        if (!validateInputs(username, password)) {
            return
        }

        // Mostrar loading
        setLoadingState(true)

        // Realizar login
        authRepository.login(
            username = username,
            password = password,
            onSuccess = { response ->
                setLoadingState(false)
                handleLoginSuccess(response)
            },
            onError = { errorMsg ->
                setLoadingState(false)
                handleLoginError(errorMsg)
            }
        )
    }

    private fun validateInputs(username: String, password: String): Boolean {
        // Validar email (ahora es obligatorio formato email)
        if (username.isBlank()) {
            etUsername.error = "Ingresa tu correo electrónico"
            etUsername.requestFocus()
            return false
        }

        // Validar formato de email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            etUsername.error = "Formato de correo inválido"
            etUsername.requestFocus()
            return false
        }

        // Validar password
        if (password.isBlank()) {
            etPassword.error = "Ingresa tu contraseña"
            etPassword.requestFocus()
            return false
        }

        if (password.length < 8) {
            etPassword.error = "La contraseña debe tener al menos 8 caracteres"
            etPassword.requestFocus()
            return false
        }

        return true
    }

    private fun setLoadingState(isLoading: Boolean) {
        btnLogin.isEnabled = !isLoading
        etUsername.isEnabled = !isLoading
        etPassword.isEnabled = !isLoading

        if (isLoading) {
            btnLogin.text = "Iniciando sesión..."
            progressBar.visibility = View.VISIBLE
        } else {
            btnLogin.text = getString(R.string.action_login)
            progressBar.visibility = View.GONE
        }
    }

    private fun handleLoginSuccess(response: LoginResponseDTO) {
        val username = response.userName ?: "Usuario"
        val roles = response.roles ?: emptyList()

        // Mostrar mensaje de bienvenida
        Toast.makeText(
            requireContext(),
            "¡Bienvenido, $username!",
            Toast.LENGTH_SHORT
        ).show()

        // Cerrar el bottom sheet
        dismiss()

        // Navegar según el rol del usuario
        navigateBasedOnRole(roles)
    }

    private fun navigateBasedOnRole(roles: List<String>) {
        val intent = if (roles.contains("ROLE_ADMINISTRADOR")) {
            // ADMIN → Ir a UsuarioActivity (muestra top_admin_bar.xml)
            Intent(requireActivity(), UsuarioActivity::class.java)
        } else {
            // USER → Ir a MainActivity (muestra top_user_bar.xml)
            Intent(requireActivity(), MainActivity::class.java)
        }

        // Limpiar el backstack y crear nueva tarea
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)

        // Finalizar la actividad actual si es necesario
        requireActivity().finish()
    }

    private fun handleLoginError(errorMsg: String) {
        // Mostrar error al usuario
        Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()

        // Opcional: Mostrar error en los campos
        etPassword.error = "Verifica tus credenciales"
        etPassword.requestFocus()
    }

    companion object {
        /**
         * 🆕 Crea una instancia con el correo pre-llenado
         * Útil cuando el usuario viene del registro exitoso
         */
        fun newInstance(correo: String? = null): LoginBottomSheetFragment {
            val fragment = LoginBottomSheetFragment()
            correo?.let {
                val bundle = Bundle()
                bundle.putString("pre_filled_email", it)
                fragment.arguments = bundle
            }
            return fragment
        }
    }
}