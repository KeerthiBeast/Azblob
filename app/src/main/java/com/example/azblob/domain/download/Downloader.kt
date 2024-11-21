package com.example.azblob.domain.download

import com.example.azblob.domain.model.BlobFinal

interface Downloader {
    fun downloadFile(url: String, name: String)
    fun downloadFileQueue(fileQueue: ArrayDeque<BlobFinal>)
}