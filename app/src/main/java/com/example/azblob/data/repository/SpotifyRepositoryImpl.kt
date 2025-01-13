package com.example.azblob.data.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.azblob.data.network.SpotifyApi
import com.example.azblob.domain.model.Playlist
import com.example.azblob.domain.repository.SpotifyAuthRepository
import com.example.azblob.domain.repository.SpotifyRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SpotifyRepositoryImpl @Inject constructor(
    private val api: SpotifyApi,
    private val auth: SpotifyAuthRepository,
    @ApplicationContext private val context: Context
): SpotifyRepository {
    override suspend fun getPlaylists(): List<Playlist> {
        val sharedPref = context
            .getSharedPreferences(
                "app_prefs",
                Context.MODE_PRIVATE
            )

        val expiresAt = sharedPref.getLong("expiresAt", 0)
        if (expiresAt < System.currentTimeMillis()) {
            auth.refreshToken()
        }


        try {
            val savedToken = sharedPref.getString("token", null)
            val authorization = "Bearer $savedToken"
            val response = api.getPlaylists(token = authorization)
            if(response.isSuccessful) {
                val responseBody = response.body()
                val items = responseBody?.toModel()
                return items ?: emptyList()
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Error in API ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Log.d("Error in API", response.toString())
                return emptyList()
            }
        } catch(e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Error in API $e",
                    Toast.LENGTH_SHORT
                ).show()
            }
            Log.d("Error in API", e.toString())
            return emptyList()
        }
    }
}