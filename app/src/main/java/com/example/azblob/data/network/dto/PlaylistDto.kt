package com.example.azblob.data.network.dto

import com.example.azblob.domain.model.Playlist

data class PlaylistDto(
    val href: String,
    val items: List<Item>,
    val limit: Int,
    val next: Any,
    val offset: Int,
    val previous: Any,
    val total: Int
) {
    fun toModel(): List<Playlist> = items.map {
        Playlist(
            name = it.name,
            url = it.external_urls.spotify,
            trackCount = it.tracks.total,
            imageUrl = it.images.first().url
        )
    }

    data class Item(
        val collaborative: Boolean,
        val description: String,
        val external_urls: ExternalUrls,
        val href: String,
        val id: String,
        val images: List<Image>,
        val name: String,
        val owner: Owner,
        val primary_color: Any,
        val `public`: Boolean,
        val snapshot_id: String,
        val tracks: Tracks,
        val type: String,
        val uri: String
    ) {
        data class ExternalUrls(
            val spotify: String
        )

        data class Image(
            val height: Int,
            val url: String,
            val width: Int
        )

        data class Owner(
            val display_name: String,
            val external_urls: ExternalUrls,
            val href: String,
            val id: String,
            val type: String,
            val uri: String
        ) {
            data class ExternalUrls(
                val spotify: String
            )
        }

        data class Tracks(
            val href: String,
            val total: Int
        )
    }
}