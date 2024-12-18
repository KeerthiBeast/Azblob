package com.example.azblob.data.network.dto

import androidx.compose.ui.graphics.Color
import com.example.azblob.domain.model.BlobFinal
import com.example.azblob.domain.model.Blobs

data class BucketModel(
    val items: List<Item>,
    val kind: String
) {
    fun toModel(): List<Blobs> = items.map {
            Blobs(
                it.name,
                it.mediaLink
            )
    }

    data class Item(
        val bucket: String,
        val contentType: String,
        val crc32c: String,
        val etag: String,
        val generation: String,
        val id: String,
        val kind: String,
        val md5Hash: String,
        val mediaLink: String,
        val metageneration: String,
        val name: String,
        val selfLink: String,
        val size: String,
        val storageClass: String,
        val timeCreated: String,
        val timeFinalized: String,
        val timeStorageClassUpdated: String,
        val updated: String
    )
}