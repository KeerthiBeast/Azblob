package com.example.azblob.ui.screen.sync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.azblob.domain.repository.AzblobRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SyncViewModel @Inject constructor(
    private val api: AzblobRepository,
): ViewModel() {
    private val _statusMessage = MutableStateFlow("")
    val statusMessage = _statusMessage.asStateFlow()

    private val _toastTrigger = MutableStateFlow(false)
    val toastTrigger = _toastTrigger.asStateFlow()

    fun putSong(url: String) {
        viewModelScope.launch {
            val message = api.putSong(url)
            _statusMessage.value = message
            _toastTrigger.value = true
            delay(100)
            _toastTrigger.value = false
        }
    }

    fun getStatus() {
        viewModelScope.launch {
            val message = api.apiStatus()
            _statusMessage.value = message
            _toastTrigger.value = true
            delay(100)
            _toastTrigger.value = false
        }
    }

    fun putPlaylist(url: String) {
        viewModelScope.launch {
            val message = api.putPlaylist(url)
            _statusMessage.value = message
            _toastTrigger.value = true
            delay(100)
            _toastTrigger.value = false
        }
    }
}