package com.example.azblob.ui.screen.settings

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage

@Composable
fun PlaylistSelector(
    update: ()->Unit,
    context: Context,
    viewmodel: AuthViewModel
) {
    Dialog(
        onDismissRequest = { update() },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        val playlists by viewmodel.playlists.collectAsState()

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(playlists) { it->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                                .edit()
                                .putString("default_playlist", it.url)
                                .apply()
                            update()
                        }
                    ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        AsyncImage(
                            model = it.imageUrl,
                            contentDescription = "Playlist Image",
                            modifier = Modifier
                                .padding(16.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .height(65.dp)
                                .width(65.dp)
                        )
                        Text(
                            text = it.name,
                            modifier = Modifier
                                .padding(16.dp)
                                .weight(1f)
                        )
                    }
                }
            }
        }
    }
}