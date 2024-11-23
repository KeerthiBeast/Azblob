package com.example.azblob.ui.screen.sync

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/* Initiate API functions to download and upload songs to Azure blob service */

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun SyncScreen(
    viewModel: SyncViewModel = hiltViewModel(),
    activity: ComponentActivity,
    paddingValues: PaddingValues
) {
    val statusMessage by viewModel.statusMessage.collectAsState()
    val toastTrigger by viewModel.toastTrigger.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Sync Screen")
                },
            )
        }
    ) {innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(25.dp)
                .padding(bottom = paddingValues.calculateBottomPadding()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Songs(viewModel)
            Spacer(modifier = Modifier.size(35.dp))
            Sync(viewModel)
            Spacer(modifier = Modifier.size(35.dp))
            Playlist(viewModel)
            Spacer(modifier = Modifier.size(35.dp))
            SyncPlaylist(activity, viewModel)

            if(toastTrigger) {
                statusMessage.let {
                    Toast.makeText(
                        activity,
                        statusMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}

//Test server status
@Composable
fun Sync(viewModel: SyncViewModel) {
    Button(onClick = { viewModel.getStatus() }) {
       Text("Check Status")
    }
}

//Download individual songs
@Composable
fun Songs(viewModel: SyncViewModel) {
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
            viewModel.putSong(link)
            link = ""
            isEnabled = false
        }) {
        Text("Download Song")
    }
}

//Download playlists
@Composable
fun Playlist(viewModel: SyncViewModel) {
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
            viewModel.putPlaylist(link)
            link = ""
            isEnabled = false
        }) {
        Text("Sync Playlist")
    }
}

//Sync with the default playlist set by the user
@Composable
fun SyncPlaylist(activity: ComponentActivity, viewModel: SyncViewModel) {
    //Retrieve playlist url from sharedPreferences
    val sharedPreferences = activity.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val playlist: String? = sharedPreferences.getString("default_playlist", null)

    Button(onClick = {
        if(playlist == null) {
            activity.runOnUiThread {
                Toast.makeText(activity, "No playlist Selected", Toast.LENGTH_LONG).show()
            }
        } else {
            viewModel.putPlaylist(playlist)
        }
    }) {
        Text("Sync Default Playlist")
    }
}