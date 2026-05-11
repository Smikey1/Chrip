package com.twugteam.admin.auth.presentation.forgot_password

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twugteam.admin.auth.domain.EmailValidator
import com.twugteam.admin.core.domain.auth.AuthService
import com.twugteam.admin.core.domain.utils.onFailure
import com.twugteam.admin.core.domain.utils.onSuccess
import com.twugteam.admin.core.presentation.util.toUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val authService: AuthService
) : ViewModel() {

    private val eventChannel = Channel<ForgotPasswordEvent>()
    val events = eventChannel.receiveAsFlow()

    private var hasLoadedInitialData = false
    private val _state = MutableStateFlow(ForgotPasswordState())
    val state = _state
        .onEach {
            if (!hasLoadedInitialData) {
                observeValidationState()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ForgotPasswordState()
        )

    val emailIsValidFlow = snapshotFlow {
        state.value.emailTextFieldState.text.toString()
    }.map {
        EmailValidator.validate(it)
    }.distinctUntilChanged()


    fun onAction(action: ForgotPasswordAction) {
        when (action) {
            ForgotPasswordAction.OnSubmitClick -> submitForgotPasswordRequest()
        }
    }

    private fun observeValidationState() {
        emailIsValidFlow.onEach { isEmailValid ->
            _state.update {
                it.copy(
                    canSubmit = isEmailValid
                )
            }
        }.launchIn(viewModelScope)

    }

    private fun submitForgotPasswordRequest() {
        if (state.value.isLoading || !state.value.canSubmit) {
            return
        }
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isEmailSentSuccessfully = false,
                    isLoading = true
                )
            }
            authService
                .forgotPassword(
                    email = state.value.emailTextFieldState.text.toString()
                ).onSuccess {
                    _state.update {
                        it.copy(
                            isEmailSentSuccessfully = true,
                            isLoading = false
                        )
                    }
                    eventChannel.send(ForgotPasswordEvent.Success)
                }.onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorText = error.toUiText()
                        )
                    }
                }
        }
    }

}