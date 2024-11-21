package com.example.azblob.data.repository

import com.example.azblob.data.network.AzblobApi
import com.example.azblob.domain.repository.AzblobRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class AzblobRepositoryImpl @Inject constructor(
    private val api: AzblobApi
): AzblobRepository {

    override suspend fun apiStatus(): String {
        try {
            val response = api.getStatus()
            if(response.isSuccessful) {
                val responseBody = response.body()
                val message = responseBody?.message
                return message.toString()
            } else {
                return response.code().toString()
            }
        } catch(e: Exception) {
            return e.toString()
        }
    }

    override suspend fun putPlaylist(playlistUrl: String): String {
        try {
            val jsonBody = """{
                    "playlist_url": "$playlistUrl"
                    }"""
            val mediaType = "application/json; charset=utf-9".toMediaType()
            val playlistUrlReq: RequestBody = jsonBody.toRequestBody(mediaType)
            val response = api.putPlaylist(playlistUrlReq)
            if(response.isSuccessful) {
                val responseBody = response.body()
                val message = responseBody?.message
                return message.toString()
            } else {
                return response.code().toString()
            }
        } catch(e: Exception) {
            return e.toString()
        }
    }

    override suspend fun putSong(songUrl: String): String {
        try {
            val jsonBody = """{
                    "song_url": "$songUrl"
                    }"""
            val mediaType = "application/json; charset=utf-9".toMediaType()
            val songUrlReq: RequestBody = jsonBody.toRequestBody(mediaType)
            val response = api.putSong(songUrlReq)
            if(response.isSuccessful) {
                val responseBody = response.body()
                val message = responseBody?.message
                return message.toString()
            } else {
                return response.code().toString()
            }
        } catch(e: Exception) {
            return e.toString()
        }
    }
}