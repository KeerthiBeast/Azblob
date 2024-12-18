package com.example.azblob.domain.repository

import com.example.azblob.domain.model.BlobFinal

interface AzblobRepository {
    suspend fun putSong(songUrl: String): String
    suspend fun putPlaylist(playlistUrl: String): String
    suspend fun apiStatus(): String
    suspend fun getBlobList(): List<BlobFinal>
}