package com.example.azblob.domain.repository

interface SpotifyAuthRepository {
    suspend fun getAuthToken(code: String)
    suspend fun refreshToken()
}