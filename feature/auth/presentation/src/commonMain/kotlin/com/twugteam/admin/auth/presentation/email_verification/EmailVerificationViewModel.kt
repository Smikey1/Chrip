package com.twugteam.admin.auth.presentation.email_verification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class EmailVerificationViewModel() : ViewModel() {
    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(EmailVerificationState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {

                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = EmailVerificationState()
        )

    // NO-OP: Actions are purely for navigation
    fun onAction(action: EmailVerificationAction) = Unit

}