package com.example.azblob.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.azblob.domain.model.Playlist
import com.example.azblob.domain.repository.SpotifyAuthRepository
import com.example.azblob.domain.repository.SpotifyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: SpotifyAuthRepository,
    private val api: SpotifyRepository
): ViewModel() {
    private val _playlist = MutableStateFlow<List<Playlist>>(emptyList())
//    val playlists = _playlist.asStateFlow()

    fun getToken(code: String) {
        viewModelScope.launch {
            auth.getAuthToken(code)
        }
    }

    private fun getPlaylists() {
        viewModelScope.launch {
            _playlist.value = api.getPlaylists()
        }
    }

    val playlists = _playlist
        .onStart { getPlaylists() }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = _playlist.value
        )
}