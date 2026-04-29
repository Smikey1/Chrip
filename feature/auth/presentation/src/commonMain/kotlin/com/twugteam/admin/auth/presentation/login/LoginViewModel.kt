package com.twugteam.admin.auth.presentation.login

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twugteam.admin.auth.domain.EmailValidator
import com.twugteam.admin.auth.presentation.Res
import com.twugteam.admin.auth.presentation.error_email_not_verified
import com.twugteam.admin.auth.presentation.error_invalid_credentials
import com.twugteam.admin.core.domain.auth.AuthService
import com.twugteam.admin.core.domain.utils.DataError
import com.twugteam.admin.core.domain.utils.onFailure
import com.twugteam.admin.core.domain.utils.onSuccess
import com.twugteam.admin.core.presentation.util.UiText
import com.twugteam.admin.core.presentation.util.toUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authService: AuthService
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val eventChannel = Channel<LoginEvent>()
    val events = eventChannel.receiveAsFlow()


    private val _state = MutableStateFlow(LoginState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                observeInputFields()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = LoginState()
        )

    private val isEmailValidFlow = snapshotFlow {
        state.value.emailTextFieldState.text.toString()
    }.map { email ->
        EmailValidator.validate(email)
    }.distinctUntilChanged()

    private val isPasswordNotBlankFlow = snapshotFlow {
        state.value.passwordTextFieldState.text.toString()
    }.map { password ->
        password.isNotBlank()
    }.distinctUntilChanged()

    private val isLoggingInFlow = state
        .map {
            it.isLoggingIn
        }.distinctUntilChanged()

    private fun observeInputFields() {
        combine(
            isEmailValidFlow,
            isPasswordNotBlankFlow,
            isLoggingInFlow ){ isEmailValid, isPasswordValid, isLogging ->
            _state.update {
                it.copy(
                    canLogin = !isLogging && isEmailValid && isPasswordValid
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onAction(action: LoginAction) {
        when (action) {
            LoginAction.OnLoginClick -> login()
            LoginAction.OnTogglePasswordVisibility -> {
                _state.update {
                    it.copy(
                        isPasswordVisible = !it.isPasswordVisible
                    )
                }
            }
            else -> Unit
        }
    }

    private fun login() {
        if(!state.value.canLogin){
            return
        }
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoggingIn = true
                )
            }
            val email = state.value.emailTextFieldState.text.toString()
            val password = state.value.passwordTextFieldState.text.toString()
            authService.login(
                email = email,
                password = password
            ).onSuccess { authInfo ->
                eventChannel.send(LoginEvent.Success)
            }.onFailure { error ->
                val errorMessage = when (error){
                    DataError.Remote.UNAUTHORIZED -> UiText.Resource(Res.string.error_invalid_credentials)
                    DataError.Remote.FORBIDDEN -> UiText.Resource(Res.string.error_email_not_verified)
                    else -> error.toUiText()
                }
                _state.update {
                    it.copy(
                        isLoggingIn = false,
                        error = errorMessage
                    )
                }
            }
        }
    }

}