package com.twugteam.admin.auth.presentation.register

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twugteam.admin.core.designsystem.theme.ChirpTheme
import com.twugteam.admin.core.presentation.util.ObserveAsEvents
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterScreenRoot(
    viewModel: RegisterViewModel = koinViewModel()
){
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events){ event ->
        when(event){
            else -> {}
        }
    }

    RegisterScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun RegisterScreen(
    state: RegisterState,
    onAction: (RegisterAction) -> Unit
){

}

@PreviewScreenSizes
@Composable
private fun RegisterScreenPreview(){
    ChirpTheme(
        isDarkTheme = true
    ) {
        RegisterScreen(
            state = RegisterState(),
            onAction = {

            }
        )
    }
}