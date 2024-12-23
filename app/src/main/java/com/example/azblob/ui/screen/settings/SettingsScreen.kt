package com.example.azblob.ui.screen.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    paddingValues: PaddingValues,
    context: ComponentActivity
) {

    // Key for saving the folder URI in SharedPreferences
    val folderUriKey = "folder_uri_key"
    val defaultPlaylist = "default_playlist"
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

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
    var defLink by remember {
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

            //Get playlist link and save to SharedPreferences
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
                    sharedPreferences.edit().putString(defaultPlaylist, link).apply()
                    defLink = link
                    link = ""
                    isEnabled = false
                }) {
                Text("Default Playlist")
            }
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = defLink ?: "No default selected",
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.size(16.dp))
            Button(
                onClick = {
                    if (defLink != null) {
                        sharedPreferences.edit().remove(defaultPlaylist).apply()
                        defLink = null
                    } else {
                        context.runOnUiThread {
                            Toast.makeText(context, "No default selected", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            ) {
                Text("Remove selection")
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