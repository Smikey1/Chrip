package com.twugteam.admin.auth.presentation.forgot_password

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twugteam.admin.auth.presentation.Res
import com.twugteam.admin.auth.presentation.email
import com.twugteam.admin.auth.presentation.forgot_password
import com.twugteam.admin.auth.presentation.forgot_password_email_sent_successfully
import com.twugteam.admin.auth.presentation.submit
import com.twugteam.admin.core.designsystem.components.buttons.ChirpButton
import com.twugteam.admin.core.designsystem.components.buttons.ChripButtonStyle
import com.twugteam.admin.core.designsystem.components.icon.ChirpLogo
import com.twugteam.admin.core.designsystem.components.layout.ChirpAdaptiveFormLayout
import com.twugteam.admin.core.designsystem.components.textfields.ChirpTextField
import com.twugteam.admin.core.designsystem.theme.ChirpTheme
import com.twugteam.admin.core.designsystem.theme.extended
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ForgotPasswordScreenRoot(
    viewModel: ForgotPasswordViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ForgotPasswordScreen(
        state = state,
        onAction = viewModel::onAction
    )
}


@Composable
private fun ForgotPasswordScreen(
    state: ForgotPasswordState,
    onAction: (ForgotPasswordAction) -> Unit
) {
    ChirpAdaptiveFormLayout(
        headerText = stringResource(Res.string.forgot_password),
        errorText = state.errorText?.asString(),
        logo = {
            ChirpLogo()
        }
    ) {
        ChirpTextField(
            state = state.emailTextFieldState,
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = stringResource(Res.string.email),
            isError = state.errorText != null,
            supportingText = state.errorText?.asString(),
            singleLine = true,
            keyboardType = KeyboardType.Email
        )
        Spacer(
            modifier = Modifier.height(16.dp)
        )
        ChirpButton(
            text = stringResource(Res.string.submit),
            style = ChripButtonStyle.PRIMARY,
            onClick = {
                onAction(ForgotPasswordAction.OnSubmitClick)
            },
            isLoading = state.isLoading,
            enabled = !state.isLoading && state.canSubmit,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(
            modifier = Modifier.height(8.dp)
        )
        if (state.isEmailSentSuccessfully) {
            Text(
                text = stringResource(Res.string.forgot_password_email_sent_successfully),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.extended.success,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable

private fun ForgotPasswordScreenPreview() {
    ChirpTheme {
        ForgotPasswordScreen(
            state = ForgotPasswordState(),
            onAction = {

            }
        )
    }
}
