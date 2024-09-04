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

/*POST request to the api to send song url and start downloading songs
* Has a lambda function to render status to the MainUi thread*/

@OptIn(DelicateCoroutinesApi::class)
fun postSong(songUrl: String, callback: (String) -> Unit) {
    GlobalScope.launch(Dispatchers.IO) {
        try {
            val jsonBody = """{
                    "song_url": "$songUrl"
                    }"""
            val mediaType = "application/json; charset=utf-9".toMediaType()
            val songUrlReq: RequestBody = jsonBody.toRequestBody(mediaType)
            val response = RetrofitInstance.api.putSong(songUrlReq)
            if(response.isSuccessful) {
                val responseBody = response.body()
                val message = responseBody?.message
                Log.d("Response", responseBody.toString())
                callback(message.toString())
            } else {
                Log.e("Response", response.code().toString())
                callback(response.code().toString())
            }
        } catch(e: HttpException) {
            callback(e.toString())
            return@launch
        } catch(e: Exception) {
            callback(e.toString())
            return@launch
        }
    }
}