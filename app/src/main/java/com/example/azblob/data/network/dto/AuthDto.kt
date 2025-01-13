package com.example.azblob.data.network.dto

data class AuthDto(
    val access_token: String,
    val token_type: String,
    val scope: String,
    val expires_in: Int,
    val refresh_token: String
)