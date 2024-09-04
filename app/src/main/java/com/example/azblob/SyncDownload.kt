package com.example.azblob

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.azblob.utils.Utils
import com.example.azblob.utils.apiStatus
import com.example.azblob.utils.postPlaylist
import com.example.azblob.utils.postSong
import com.example.azblob.utils.syncFolder

/* Initiate API functions to download and upload songs to Azure blob service */

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun SyncDownload(activity: ComponentActivity, paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .padding(25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Songs(activity)
        Spacer(modifier = Modifier.size(35.dp))
        Sync(activity)
        Spacer(modifier = Modifier.size(35.dp))
        Playlist(activity)
        Spacer(modifier = Modifier.size(35.dp))
        SyncPlaylist(activity)
    }
}

//Test server status
@Composable
fun Sync(activity: ComponentActivity) {
    Button(onClick = { apiStatus() {
            message -> activity.runOnUiThread {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }
    }
    }) {
        Text("Check Status")
    }
}

//Download individual songs
@Composable
fun Songs(activity: ComponentActivity) {
    var link by remember { mutableStateOf("") }
    var isEnabled by remember { mutableStateOf(false) }

    //Get song url from the user
    OutlinedTextField(
        value = link,
        onValueChange = { text ->
            link = text
            if (text.isNotEmpty()) isEnabled = true
            else isEnabled = false
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = MaterialTheme.shapes.extraLarge,
        label = { Text("Song Url") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null
            )
        }
    )
    Spacer(modifier = Modifier.size(16.dp))
    Button(
        enabled = isEnabled,
        onClick = {
            postSong(link) { message ->
                activity.runOnUiThread {
                    Toast.makeText(
                        activity,
                        message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                link = ""
                isEnabled = false
            }
        }) {
        Text("Download Song")
    }
}

//Download playlists
@Composable
fun Playlist(activity: ComponentActivity) {
    var link by remember { mutableStateOf("") }
    var isEnabled by remember { mutableStateOf(false) }

    //Get playlist form the user
    OutlinedTextField(
        value = link,
        onValueChange = { text ->
            link = text
            if (text.isNotEmpty()) isEnabled = true
            else isEnabled = false
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = MaterialTheme.shapes.extraLarge,
        label = { Text("Playlist Url") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = null
            )
        }
    )
    Spacer(modifier = Modifier.size(16.dp))
    Button(
        enabled = isEnabled,
        onClick = {
            postPlaylist(link) { message ->
                activity.runOnUiThread {
                    Toast.makeText(
                        activity,
                        message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                link = ""
                isEnabled = false
            }
        }) {
        Text("Sync Playlist")
    }
}

//Sync with the default playlist set by the user
@Composable
fun SyncPlaylist(activity: ComponentActivity) {
    //Retrieve playlist url from sharedPreferences
    val sharedPreferences = activity.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val playlist: String? = sharedPreferences.getString("default_playlist", null)

    Button(onClick = {
        if(playlist == null) {
            activity.runOnUiThread {
                Toast.makeText(activity, "No playlist Selected", Toast.LENGTH_LONG).show()
            }
        } else {
            postPlaylist(playlist) { message ->
               activity.runOnUiThread {
                   Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
               }
            }
        }
    }) {
        Text("Sync Default Playlist")
    }
}