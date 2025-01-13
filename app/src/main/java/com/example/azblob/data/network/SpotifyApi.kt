package com.example.azblob.data.network

import com.example.azblob.data.network.dto.PlaylistDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface SpotifyApi {
    @GET("me/playlists")
    suspend fun getPlaylists(
        @Header("Authorization") token: String
    ): Response<PlaylistDto>
}