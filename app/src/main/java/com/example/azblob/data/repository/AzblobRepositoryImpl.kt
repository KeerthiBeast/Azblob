package com.example.azblob.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.documentfile.provider.DocumentFile
import com.example.azblob.data.network.AzblobApi
import com.example.azblob.domain.model.Blobs
import com.example.azblob.domain.model.BlobFinal
import com.example.azblob.domain.repository.AzblobRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class AzblobRepositoryImpl @Inject constructor(
    private val api: AzblobApi,
    @ApplicationContext private val context: Context
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

    override suspend fun getBlobList(): List<BlobFinal> {
        try {
            val response = api.getBlobList()
            if(response.isSuccessful) {
                val responseBody = response.body()?.toModel()
                return if(responseBody != null) {
                    withContext(Dispatchers.IO) { localFileList(responseBody) }
                } else emptyList()
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Error in API: ${response.code()}",
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
                    "Error in API: $e",
                    Toast.LENGTH_SHORT
                ).show()
            }
            Log.d("Error in API", e.toString())
            return emptyList()
        }
    }

    private fun localFileList(blobs: List<Blobs>): List<BlobFinal> {
        var fileList: Set<String> = emptySet()
        val context = context

        //Get folder URI
        val folderUriKey = "folder_uri_key"
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val selectedFolderUri = sharedPreferences?.getString(folderUriKey, null)?.let { Uri.parse(it) }

        //Search the folder
        if (selectedFolderUri != null) {
            val documentFile = DocumentFile.fromTreeUri(context, selectedFolderUri)
            if(documentFile != null && documentFile.canRead()) {
                fileList =  documentFile.listFiles().mapNotNull { it.name }.toSet()
            }
        }

        return blobs.map{blob->
            val color = if(fileList.contains(blob.name)) Color.Green else Color.White
            BlobFinal(
                name = blob.name,
                url = blob.url,
                color = color
            )
        }
    }
}