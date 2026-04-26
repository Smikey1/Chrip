package com.twugteam.admin.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twugteam.admin.auth.domain.EmailValidator
import com.twugteam.admin.auth.presentation.Res
import com.twugteam.admin.auth.presentation.error_invalid_email
import com.twugteam.admin.auth.presentation.error_invalid_password
import com.twugteam.admin.auth.presentation.error_invalid_username
import com.twugteam.admin.core.domain.validation.PasswordValidator
import com.twugteam.admin.core.presentation.util.UiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class RegisterViewModel : ViewModel() {

    private var hasLoadedInitialData: Boolean = false
    private val _state = MutableStateFlow(RegisterState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {

                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = RegisterState()
        )

    private val eventChannel = Channel<RegisterEvent>()
    val events = eventChannel.receiveAsFlow()

    fun onAction(action: RegisterAction) {
        when (action) {
            RegisterAction.OnLoginClick -> validateFormInput()
            else -> Unit
        }
    }

    private fun clearAllTextFieldError() {
        _state.update {
            it.copy(
                usernameError = null,
                emailError = null,
                passwordError = null,
                registrationError = null
            )
        }
    }

    private fun validateFormInput(): Boolean {

        clearAllTextFieldError()

        val currentState = state.value
        val username = currentState.usernameTextState.text.toString()
        val email = currentState.emailTextState.text.toString()
        val password = currentState.passwordTextState.text.toString()

        val isUsernameValid = username.length in 3..20
        val isEmailValid = EmailValidator.validate(email)
        val isPasswordValid = PasswordValidator.validate(password).isValidPassword

        val usernameError = if (!isUsernameValid) {
            UiText.Resource(Res.string.error_invalid_username)
        } else null

        val emailError = if (!isEmailValid) {
            UiText.Resource(Res.string.error_invalid_email)
        } else null

        val passwordError = if (!isPasswordValid) {
            UiText.Resource(Res.string.error_invalid_password)
        } else null

        _state.update {
            it.copy(
                usernameError = usernameError,
                emailError = emailError,
                passwordError = passwordError
            )
        }
        return isUsernameValid && isEmailValid && isPasswordValid
    }
}