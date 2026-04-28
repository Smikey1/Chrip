package com.twugteam.admin.auth.presentation.register

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.twugteam.admin.auth.presentation.Res
import com.twugteam.admin.auth.presentation.email
import com.twugteam.admin.auth.presentation.email_placeholder
import com.twugteam.admin.auth.presentation.login
import com.twugteam.admin.auth.presentation.password
import com.twugteam.admin.auth.presentation.password_hint
import com.twugteam.admin.auth.presentation.register
import com.twugteam.admin.auth.presentation.username
import com.twugteam.admin.auth.presentation.username_hint
import com.twugteam.admin.auth.presentation.username_placeholder
import com.twugteam.admin.auth.presentation.welcome_to_chirp
import com.twugteam.admin.core.designsystem.components.buttons.ChirpButton
import com.twugteam.admin.core.designsystem.components.buttons.ChripButtonStyle
import com.twugteam.admin.core.designsystem.components.icon.ChirpLogo
import com.twugteam.admin.core.designsystem.components.layout.ChirpAdaptiveFormLayout
import com.twugteam.admin.core.designsystem.components.layout.ChirpScaffoldLayout
import com.twugteam.admin.core.designsystem.components.textfields.ChirpPasswordTextField
import com.twugteam.admin.core.designsystem.components.textfields.ChirpTextField
import com.twugteam.admin.core.designsystem.theme.ChirpTheme
import com.twugteam.admin.core.presentation.util.ObserveAsEvents
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterScreenRoot(
    viewModel: RegisterViewModel = koinViewModel(),
    onRegisterSuccess: (String) -> Unit,
    onLoginClick: () -> Unit
) {
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is RegisterEvent.RegistrationSuccess -> onRegisterSuccess(event.email)
            else -> Unit
        }
    }

    RegisterScreen(
        state = state,
        onAction = {action ->
            when(action){
                is RegisterAction.OnLoginClick -> onLoginClick()
                else -> Unit
            }
            viewModel.onAction(action)
        },
        snackbarHostState = snackbarHostState
    )
}

@Composable
private fun RegisterScreen(
    state: RegisterState,
    onAction: (RegisterAction) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    ChirpScaffoldLayout(
        snackbarHostState = snackbarHostState,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        ChirpAdaptiveFormLayout(
            headerText = stringResource(Res.string.welcome_to_chirp),
            errorText = state.registrationError?.asString(),
            logo = { ChirpLogo() }
        ) {
            ChirpTextField(
                state = state.usernameTextState,
                placeholder = stringResource(Res.string.username_placeholder),
                title = stringResource(Res.string.username),
                supportingText = state.usernameError?.asString()
                    ?: stringResource(Res.string.username_hint),
                isError = state.usernameError != null,
                onFocusChange = {
                    onAction(RegisterAction.OnInputTextFocusGain)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            ChirpTextField(
                state = state.emailTextState,
                placeholder = stringResource(Res.string.email_placeholder),
                title = stringResource(Res.string.email),
                supportingText = state.emailError?.asString(),
                isError = state.emailError != null,
                onFocusChange = {
                    onAction(RegisterAction.OnInputTextFocusGain)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            ChirpPasswordTextField(
                state = state.passwordTextState,
                placeholder = stringResource(Res.string.password),
                title = stringResource(Res.string.password),
                supportingText = state.passwordError?.asString()
                    ?: stringResource(Res.string.password_hint),
                isError = state.passwordError != null,
                isPasswordVisible = state.isPasswordVisible,
                onToggleVisibilityClick = {
                    onAction(RegisterAction.OnTogglePasswordVisibilityClick)
                },
                onFocusChange = {
                    onAction(RegisterAction.OnInputTextFocusGain)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            ChirpButton(
                text = stringResource(Res.string.register),
                onClick = {
                    onAction(RegisterAction.OnRegisterClick)
                },
                enabled = state.canRegister,
                isLoading = state.isRegistering,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            ChirpButton(
                text = stringResource(Res.string.login),
                onClick = {
                    onAction(RegisterAction.OnLoginClick)
                },
                style = ChripButtonStyle.SECONDARY,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@PreviewScreenSizes
@Composable
private fun RegisterScreenPreview() {
    ChirpTheme(
        isDarkTheme = true
    ) {
        RegisterScreen(
            state = RegisterState(),
            onAction = {

            },
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}