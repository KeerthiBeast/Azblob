package com.example.azblob.utils

import android.util.Log
import com.example.azblob.api.RetrofitInstance
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

/*POST request to the api to send playlist url and start downloading songs
* Takes a callback lambda to return status to the MainUi thread*/

@OptIn(DelicateCoroutinesApi::class)
fun postPlaylist(playlistUrl: String, callback: (String) -> Unit) {
    GlobalScope.launch(Dispatchers.IO) {
        try {
            val jsonBody = """{
                    "playlist_url": "$playlistUrl"
                    }"""
            val mediaType = "application/json; charset=utf-9".toMediaType()
            val playlistUrlReq: RequestBody = jsonBody.toRequestBody(mediaType)
            val response = RetrofitInstance.api.putPlaylist(playlistUrlReq)
            if(response.isSuccessful) {
                val responseBody = response.body()
                val message = responseBody?.message
                Log.d("Response", responseBody.toString())
                callback(message.toString())
            } else {
                callback(response.code().toString())
                Log.e("Response", response.code().toString())
            }
        } catch(e: HttpException) {
            callback(e.toString())
            Log.e("Exception", e.toString())
        } catch(e: Exception) {
            callback(e.toString())
            Log.e("Exception", e.toString())
        }
    }
}