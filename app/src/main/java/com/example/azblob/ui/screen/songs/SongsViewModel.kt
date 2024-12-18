package com.example.azblob.ui.screen.songs

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.azblob.data.network.AzblobApi
import com.example.azblob.domain.download.Downloader
import com.example.azblob.domain.model.BlobFinal
import com.example.azblob.domain.repository.AzblobRepository
import com.example.azblob.domain.repository.AzureRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SongsViewModel @Inject constructor(
    private val api: AzblobRepository,
    private val downloader: Downloader
): ViewModel() {
    private val _searchBlobs = MutableStateFlow<List<BlobFinal>>(emptyList()) //Saving the list from api

    private val _searchText = MutableStateFlow("") //To search for songs
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false) //To indicate searching state
    val isSearching = _isSearching.asStateFlow()

    private val _toDownload = MutableStateFlow<List<BlobFinal>>(emptyList())
    private val _downloadSize = MutableStateFlow(0)
    val downloadSize = _downloadSize.asStateFlow()

    fun getSongs() {
        Log.d("Message", "Started getting songs")
        viewModelScope.launch {
            _searchBlobs.value = api.getBlobList()
            _toDownload.value = _searchBlobs.value.filter { it.color == Color.White }
            _downloadSize.value = _toDownload.value.size
        }
    }

    /*Search for songs using flow filters and update the blobList value
    * which is exposed to the user side */
    val blobList = searchText
        .onStart {
            _isSearching.update { true }
            getSongs()
        }
        .debounce(1000L)
        .onEach { _isSearching.update { true } }
        .combine(_searchBlobs) { text, blobs ->
            if(text.isBlank()) {
                blobs
            } else {
                delay(200L)
                blobs.filter {
                    it.name.contains(text, ignoreCase = true)
                }
            }
        }
        .onEach { _isSearching.update { false } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _searchBlobs.value
        )

    val downloadList = searchText
        .debounce(1000L)
        .onEach { _isSearching.update { true } }
        .combine(_toDownload) { text, blobs ->
            if(text.isBlank()) {
                blobs
            } else {
                delay(200L)
                blobs.filter {
                    it.name.contains(text, ignoreCase = true)
                }
            }
        }
        .onEach { _isSearching.update { false } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _toDownload.value
        )

    //Function used in the user side to update the searchText to change the values displayed
    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun downloadFile(url: String, name: String) {
        downloader.downloadFile(url, name)
    }

    fun downloadBatch(fileQueue: ArrayDeque<BlobFinal>) {
        downloader.downloadFileQueue(fileQueue)
    }
}