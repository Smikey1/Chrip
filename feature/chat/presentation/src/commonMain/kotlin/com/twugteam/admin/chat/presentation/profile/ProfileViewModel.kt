package com.twugteam.admin.chat.presentation.profile

import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twugteam.admin.core.domain.auth.AuthService
import com.twugteam.admin.core.domain.utils.DataError
import com.twugteam.admin.core.domain.utils.onFailure
import com.twugteam.admin.core.domain.utils.onSuccess
import com.twugteam.admin.core.domain.validation.PasswordValidator
import com.twugteam.admin.core.presentation.util.UiText
import com.twugteam.admin.core.presentation.util.toUiText
import com.twugteam.admin.feature.chat.presentation.Res
import com.twugteam.admin.feature.chat.presentation.error_current_password_equal_to_new_one
import com.twugteam.admin.feature.chat.presentation.error_current_password_incorrect
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

class ProfileViewModel(
    private val authService: AuthService
) : ViewModel() {
    var hasAlreadyLoadedInitialData = false
    private val _state = MutableStateFlow(ProfileState())
    val state = _state
        .onStart {
            if (!hasAlreadyLoadedInitialData) {
                observeCanChangePassword()
                hasAlreadyLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = ProfileState()
        )

    private val eventChannel = Channel<ProfileEvent>()
    val events = eventChannel.receiveAsFlow()

    fun onAction(action: ProfileAction) {
        when (action) {
            ProfileAction.OnChangePasswordClick -> changePassword()
            ProfileAction.OnToggleCurrentPasswordVisibilityClick -> {
                _state.update {
                    it.copy(
                        isCurrentPasswordVisible = !it.isCurrentPasswordVisible
                    )
                }
            }
            ProfileAction.OnToggleNewPasswordVisibilityClick -> {
                _state.update {
                    it.copy(
                        isNewPasswordVisible = !it.isNewPasswordVisible
                    )
                }
            }
            else -> {}
        }
    }

    private fun observeCanChangePassword() {
        val oldPasswordValidFlow =
            snapshotFlow { _state.value.currentPasswordTextState.text.toString() }
                .map { it.isNotBlank() }
                .distinctUntilChanged()
        val newPasswordValidFlow =
            snapshotFlow { _state.value.newPasswordTextState.text.toString() }
                .map {
                    PasswordValidator.validate(it).isValidPassword
                }
                .distinctUntilChanged()
        combine(
            oldPasswordValidFlow,
            newPasswordValidFlow
        ) { oldPasswordValid, newPasswordValid ->
            _state.update {
                it.copy(
                    canChangePassword = oldPasswordValid && newPasswordValid
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun changePassword() {
        if (!state.value.canChangePassword && state.value.isChangingPassword) {
            return
        }
        _state.update {
            it.copy(
                isChangingPassword = true,
                isChangePasswordSuccessful = false
            )
        }
        viewModelScope.launch {
            authService
                .changePassword(
                    oldPassword = _state.value.currentPasswordTextState.text.toString(),
                    newPassword = _state.value.newPasswordTextState.text.toString(),
                )
                .onSuccess {
                    _state.value.currentPasswordTextState.clearText()
                    _state.value.newPasswordTextState.clearText()
                    _state.update {
                        it.copy(
                            isChangingPassword = false,
                            isNewPasswordVisible = false,
                            newPasswordError = null,
                            isCurrentPasswordVisible = false,
                            isChangePasswordSuccessful = true
                        )
                    }
                }
                .onFailure { error ->
                    val errorText = when (error) {
                        DataError.Remote.CONFLICT -> UiText.Resource(Res.string.error_current_password_equal_to_new_one)
                        DataError.Remote.UNAUTHORIZED -> UiText.Resource(Res.string.error_current_password_incorrect)
                        else -> error.toUiText()
                    }
                    _state.update {
                        it.copy(
                            isChangingPassword = false,
                            newPasswordError = errorText
                        )
                    }
                }
        }
    }
}