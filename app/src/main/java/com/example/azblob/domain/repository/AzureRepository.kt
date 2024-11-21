package com.example.azblob.domain.repository

import com.example.azblob.domain.model.BlobFinal

interface AzureRepository {
    suspend fun getSongs(): List<BlobFinal>
}