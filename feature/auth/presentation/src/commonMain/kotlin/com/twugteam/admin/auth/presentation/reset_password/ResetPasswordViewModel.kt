package com.twugteam.admin.auth.presentation.reset_password

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twugteam.admin.auth.presentation.Res
import com.twugteam.admin.auth.presentation.error_reset_password_token_invalid
import com.twugteam.admin.auth.presentation.error_same_password
import com.twugteam.admin.core.domain.auth.AuthService
import com.twugteam.admin.core.domain.utils.DataError
import com.twugteam.admin.core.domain.utils.onFailure
import com.twugteam.admin.core.domain.utils.onSuccess
import com.twugteam.admin.core.domain.validation.PasswordValidator
import com.twugteam.admin.core.presentation.util.UiText
import com.twugteam.admin.core.presentation.util.toUiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ResetPasswordViewModel(
    private val authService: AuthService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val token = savedStateHandle.get<String>("token")
        ?: throw IllegalStateException("Password Reset Token is Missing")

    private var hasLoadedInitialData = false
    private val _state = MutableStateFlow(ResetPasswordState())
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
            initialValue = ResetPasswordState()
        )

    val passwordIsValidFlow = snapshotFlow {
        state.value.passwordTextFieldState.text.toString()
    }.map {
        PasswordValidator.validate(it).isValidPassword
    }.distinctUntilChanged()


    fun onAction(action: ResetPasswordAction) {
        when (action) {
            ResetPasswordAction.OnSubmitClick -> resetPassword()
            ResetPasswordAction.OnTogglePasswordVisibilityClick -> {
                _state.update {
                    it.copy(
                        isPasswordVisible = !it.isPasswordVisible
                    )
                }
            }
        }
    }

    private fun observeValidationState() {
        passwordIsValidFlow.onEach { isPasswordValid ->
            _state.update {
                it.copy(
                    canSubmit = isPasswordValid
                )
            }
        }.launchIn(viewModelScope)

    }

    private fun resetPassword() {
        if (state.value.isLoading || !state.value.canSubmit) {
            return
        }
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isPasswordResetSuccessful = false,
                    isLoading = true
                )
            }
            authService
                .resetPassword(
                    newPassword = state.value.passwordTextFieldState.text.toString(),
                    token = token
                ).onSuccess {
                    _state.update {
                        it.copy(
                            isPasswordResetSuccessful = true,
                            isLoading = false,
                            errorText = null
                        )
                    }
                }.onFailure { error ->
                    val errorText = when (error) {
                        DataError.Remote.UNAUTHORIZED -> UiText.Resource(Res.string.error_reset_password_token_invalid)
                        DataError.Remote.CONFLICT -> UiText.Resource(Res.string.error_same_password)
                        else -> error.toUiText()
                    }
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorText = errorText
                        )
                    }
                }
        }
    }

}