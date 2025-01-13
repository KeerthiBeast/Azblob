package com.example.azblob.domain.repository

import com.example.azblob.domain.model.Playlist

interface SpotifyRepository {
    suspend fun getPlaylists(): List<Playlist>
}