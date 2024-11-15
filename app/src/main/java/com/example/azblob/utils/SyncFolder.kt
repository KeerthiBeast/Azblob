package com.example.azblob.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.util.Log
import androidx.documentfile.provider.DocumentFile

/*Reads the selected folder and returns a list of files in it*/

suspend fun syncFolder(): Set<String> {
    val context = Utils.context ?: return emptySet()

    //Get folder URI
    val folderUriKey = "folder_uri_key"
    val sharedPreferences = context?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val selectedFolderUri = sharedPreferences?.getString(folderUriKey, null)?.let { Uri.parse(it) }

    //Search the folder
    if (selectedFolderUri != null) {
        val documentFile = DocumentFile.fromTreeUri(context, selectedFolderUri)
        if(documentFile != null && documentFile.canRead()) {
            return documentFile.listFiles().mapNotNull { it.name }.toSet()
        }
    }

    return emptySet()
}