package com.example.azblob.ui.screen.sync

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.azblob.domain.repository.AzblobRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SyncViewModel @Inject constructor(
    private val api: AzblobRepository,
    @ApplicationContext private val context: Context
): ViewModel() {
    fun putSong(url: String) {
        viewModelScope.launch {
            val message = api.putSong(url)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }
   }
    fun getStatus() {
        viewModelScope.launch {
            val message = api.apiStatus()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }
   }
    fun putPlaylist(url: String) {
        viewModelScope.launch {
            val message = api.putPlaylist(url)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }
    }
}