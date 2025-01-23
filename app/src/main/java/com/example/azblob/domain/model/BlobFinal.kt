package com.example.azblob.domain.model

import android.net.Uri
import androidx.compose.ui.graphics.Color

data class BlobFinal(
    val name: String,
    val url: String,
    val color: Color,
    val uri: Uri? = null
)
