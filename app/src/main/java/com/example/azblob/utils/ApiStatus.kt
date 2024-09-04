package com.example.azblob.utils

import android.util.Log
import com.example.azblob.api.RetrofitInstance
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.HttpException

/*GET request to the api to check status of the server
* Has a lambda function to render status to the MainUi thread*/

@OptIn(DelicateCoroutinesApi::class)
fun apiStatus(callback: (String) -> Unit) {
    GlobalScope.launch(Dispatchers.IO) {
        try {
            val response = RetrofitInstance.api.getStatus()
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