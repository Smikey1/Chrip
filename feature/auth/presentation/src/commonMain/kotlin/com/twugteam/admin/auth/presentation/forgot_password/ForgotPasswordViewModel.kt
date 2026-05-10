package com.twugteam.admin.auth.presentation.forgot_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ForgotPasswordViewModel : ViewModel() {

    private val eventChannel = Channel<ForgotPasswordEvent>()
    val events = eventChannel.receiveAsFlow()

    private var hasLoadedInitialData = false
    private val _state = MutableStateFlow(ForgotPasswordState())
    val state = _state
        .onEach {
            if (!hasLoadedInitialData) {
                // Load Initial Data
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ForgotPasswordState()
        )

    fun onAction(action: ForgotPasswordAction){
        when(action){
            ForgotPasswordAction.OnSubmitClick -> {

            }
        }
    }

}