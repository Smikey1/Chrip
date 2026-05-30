package com.twugteam.admin.chat.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn

class ProfileViewModel() : ViewModel() {
    var hasAlreadyLoadedInitialData = false
    private val _state = MutableStateFlow(ProfileState())
    val state = _state
        .onStart {
            if (!hasAlreadyLoadedInitialData) {

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

            else -> {}
        }
    }
}