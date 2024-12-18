package com.example.azblob.data.network

import com.example.azblob.data.network.dto.BucketModel
import com.example.azblob.data.network.dto.LogModel
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

//Different Endpoints of the API
interface AzblobApi {
    @POST("/song")
    suspend fun putSong(
        @Body requestBody: RequestBody
    ): Response<LogModel>

    @POST("/playlist")
    suspend fun putPlaylist(
        @Body requestBody: RequestBody
    ): Response<LogModel>

    @GET("/api")
    suspend fun getStatus(): Response<LogModel>

    @GET("/bloblist")
    suspend fun getBlobList(): Response<BucketModel>
}