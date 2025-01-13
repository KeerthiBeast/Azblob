package com.example.azblob.ui.screen.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.draw.clip
import coil3.compose.AsyncImage

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewmodel: AuthViewModel = hiltViewModel(),
    paddingValues: PaddingValues,
    context: ComponentActivity
) {

    // Key for saving the folder URI in SharedPreferences
    val folderUriKey = "folder_uri_key"
    val defaultPlaylist = "default_playlist"
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    var token by remember {
        mutableStateOf(
            sharedPreferences.getString("token", null)
        )
    }

    // Load saved folder URI from SharedPreferences
    var selectedFolderUri by remember {
        mutableStateOf(
            sharedPreferences.getString(folderUriKey, null)?.let { Uri.parse(it) }
        )
    }

    var selectedFolderPath by remember { mutableStateOf(selectedFolderUri?.let { getFullPathFromTreeUri(it) }) }

    // Remember the launcher for the folder picker activity result
    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        // Handle the folder selection
        uri?.let {
            selectedFolderUri = it
            selectedFolderPath = getFullPathFromTreeUri(uri)

            // Save the selected folder URI to SharedPreferences
            sharedPreferences.edit().putString(folderUriKey, uri.toString()).apply()
            //Save the selected folder URI permissions
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
        }
    }

    var link by remember { mutableStateOf("") }
    var isEnabled by remember { mutableStateOf(false) }
    val defLink by remember {
        mutableStateOf(
            sharedPreferences.getString(defaultPlaylist, null)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Settings")
                },
            )
        }
    ) {innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .padding(bottom = paddingValues.calculateBottomPadding()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = selectedFolderPath ?: "No folder selected",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { folderPickerLauncher.launch(null) }) {
                Text(text = "Select Folder")
            }
            Spacer(modifier = Modifier.height(16.dp))

            //Playlist selector for default playlists
            if(token != null) {
                val playlists by viewmodel.playlists.collectAsState()
                var showSelector by remember {
                    mutableStateOf(false)
                }

                if(playlists.isNotEmpty()) {
                    var defPlaylist by remember {
                        mutableStateOf(
                            if(defLink == null) {
                                playlists.first()
                            } else {
                                playlists.find { it.url == defLink }!!
                            }
                        )
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showSelector = true },
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            AsyncImage(
                                model = defPlaylist.imageUrl,
                                contentDescription = "Playlist Image",
                                modifier = Modifier
                                    .padding(16.dp)
                                    .clip(MaterialTheme.shapes.medium)
                                    .height(65.dp)
                                    .width(65.dp)
                            )
                            Text(
                                text = "${defPlaylist.name}\n\nSong Count: ${defPlaylist.trackCount}",
                                modifier = Modifier
                                    .padding(16.dp)
                                    .weight(1f)
                            )
                        }
                    }
                    if(showSelector) {
                        PlaylistSelector(
                            context = context,
                            update = {
                                showSelector = false
                                defPlaylist = playlists.find{
                                    it.url == sharedPreferences
                                        .getString(
                                            defaultPlaylist,
                                            null
                                        )
                                }!!
                            },
                            viewmodel = viewmodel
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                LogoutBtn(context = context){ token = null }
            } else {
                val scope = rememberCoroutineScope()
                Button(
                    onClick = {
                        customTabRequest(context = context)
                        scope.launch {
                            delay(3000)
                            token = sharedPreferences.getString("token", null)
                            Log.d("Token", sharedPreferences.getString("token", null).toString())
                        }
                    }
                ) {
                    Text("Login")
                }
            }
        }
    }
}

fun getFullPathFromTreeUri(treeUri: Uri): String {
    var fullPath = ""
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val docId = DocumentsContract.getTreeDocumentId(treeUri)
        val split = docId.split(":")
        val type = split[0]
        if (type.equals("primary", true)) {
            fullPath = split[1]
        } else {
            fullPath = "/storage/${split[0]}/${split[1]}"
        }
    }
    return fullPath
}