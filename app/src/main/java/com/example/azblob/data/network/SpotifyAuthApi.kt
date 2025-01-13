package com.example.azblob.data.network

import com.example.azblob.data.network.dto.AuthDto
import com.example.azblob.utils.Utils
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SpotifyAuthApi {
    @FormUrlEncoded
    @POST("token")
    suspend fun getAuthToken(
        @Field("grant_type") grantType: String = "authorization_code",
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String = Utils.redirect_uri,
        @Field("client_id") clientId: String = Utils.client_id,
        @Field("client_secret") clientSecret: String = Utils.client_secret
    ): Response<AuthDto>

    @FormUrlEncoded
    @POST("token")
    suspend fun refreshAuthToken(
        @Field("grant_type") grandType: String = "refresh_token",
        @Field("refresh_token") refreshToken: String,
        @Field("client_id") clientId: String = Utils.client_id,
        @Field("client_secret") clientSecret: String = Utils.client_secret
    ): Response<AuthDto>
}