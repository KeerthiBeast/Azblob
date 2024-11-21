package com.example.azblob.domain.repository

interface AzblobRepository {
    suspend fun putSong(songUrl: String): String
    suspend fun putPlaylist(playlistUrl: String): String
    suspend fun apiStatus(): String
}