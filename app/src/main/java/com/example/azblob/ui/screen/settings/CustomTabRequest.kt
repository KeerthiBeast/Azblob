package com.example.azblob.ui.screen.settings

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.example.azblob.utils.Utils

fun customTabRequest(context: Context) {
    val customTabIntent = CustomTabsIntent.Builder().build()
    val url = "https://accounts.spotify.com/authorize"
    val scope = "user-read-private user-read-email user-top-read user-read-recently-played playlist-modify-public playlist-modify-private"

    val urlConstructor = Uri.parse(url)
        .buildUpon()
        .appendQueryParameter("client_id", Utils.client_id)
        .appendQueryParameter("response_type", "code")
        .appendQueryParameter("scope", scope)
        .appendQueryParameter("redirect_uri", Utils.redirect_uri)

    customTabIntent.launchUrl(
        context,
        Uri.parse(urlConstructor.toString())
    )
}