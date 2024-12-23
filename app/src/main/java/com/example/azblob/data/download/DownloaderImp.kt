package com.example.azblob.data.download

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.os.Build
import android.os.Environment
import androidx.core.net.toUri
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.documentfile.provider.DocumentFile
import com.example.azblob.domain.download.Downloader
import com.example.azblob.domain.model.BlobFinal
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

/*Downloader to download songs using Android native Download manager
* Downloads songs to the default download folder and then moves it to the selected folder
selected by the user saved in the sharedPreferences*/

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class DownloaderImp @Inject constructor(
    @ApplicationContext private val context: Context
): Downloader {
    private val downloadManager =  context.getSystemService(DownloadManager::class.java)

    override fun downloadFile(url: String, name: String) {
        val request = DownloadManager.Request(url.toUri())
            .setMimeType("audio/mp3")
            .setTitle(name)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name)
        val result = downloadManager.enqueue(request)

        //Receives a broadcast when the download is complete to move files
        context.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == result) {
                    // Move the file to the custom directory after the download completes
                    moveFileToAzblobFolder(result, name)
                    context.unregisterReceiver(this) // Unregister receiver after the download is complete
                }
            }
        }, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            Context.RECEIVER_EXPORTED)
    }

    override fun downloadFileQueue(fileQueue: ArrayDeque<BlobFinal>) {
        if (fileQueue.isEmpty()) return

        val downloadNext = object {
            fun startDownload() {
                if (fileQueue.isEmpty()) return

                val file = fileQueue.removeFirst()
                val url = file.url
                val name = file.name

                val request = DownloadManager.Request(url.toUri())
                    .setMimeType("audio/mp3")
                    .setTitle(name)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name)

                val result = downloadManager.enqueue(request)

                // Register a broadcast receiver to handle download completion
                context.registerReceiver(object : BroadcastReceiver() {
                    override fun onReceive(ctx: Context, intent: Intent) {
                        val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                        if (id == result) {
                            // Move the file to the custom directory after the download completes
                            moveFileToAzblobFolder(result, name)

                            // Unregister receiver to avoid memory leaks
                            context.unregisterReceiver(this)

                            // Start the next download in the queue
                            startDownload()
                        }
                    }
                }, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                    Context.RECEIVER_EXPORTED)
            }
        }

        downloadNext.startDownload()
    }

    @SuppressLint("Range")
    //Function to move files from default download folder to the selected folder
    private fun moveFileToAzblobFolder(downloadId: Long, fileName: String) {
        val downloadManager = context.getSystemService(DownloadManager::class.java)
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor: Cursor = downloadManager.query(query)

        if (cursor.moveToFirst()) {
            val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                val downloadFilePath = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))?.toUri()

                // Load saved folder URI from SharedPreferences
                val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val folderUriString = sharedPreferences.getString("folder_uri_key", null)
                val azblobFolderUri = folderUriString?.toUri()

                if (azblobFolderUri != null) {
                    val azblobFolderDocument = DocumentFile.fromTreeUri(context, azblobFolderUri)

                    // Check if the folder is accessible
                    if (azblobFolderDocument != null && azblobFolderDocument.canWrite()) {
                        try {
                            downloadFilePath?.let { uri ->
                                val sourceFile = File(uri.path ?: "")

                                // Verify source file exists
                                if (sourceFile.exists()) {
                                    // Create a new file in the Azblob folder using DocumentFile
                                    val destinationDocumentFile = azblobFolderDocument.createFile("audio/mp3", fileName)

                                    if (destinationDocumentFile != null) {
                                        // Use streams to move the file content
                                        context.contentResolver.openInputStream(uri)?.use { inputStream ->
                                            context.contentResolver.openOutputStream(destinationDocumentFile.uri)?.use { outputStream ->
                                                inputStream.copyTo(outputStream)
                                                Log.d("DownloaderImp", "File moved to: ${destinationDocumentFile.uri}")
                                            }
                                        }
                                        // Delete the original file after moving
                                        sourceFile.delete()
                                    } else {
                                        Log.e("DownloaderImp", "Failed to create file in Azblob folder.")
                                    }
                                } else {
                                    Log.e("DownloaderImp", "Source file does not exist.")
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("DownloaderImp", "Error moving file: ${e.message}")
                        }
                    } else {
                        Log.e("DownloaderImp", "Cannot write to the Azblob folder.")
                    }
                } else {
                    Log.e("DownloaderImp", "No folder URI available or folder is not accessible.")
                }
            }
        } else {
            Log.e("DownloaderImp", "Failed to retrieve download status.")
        }
        cursor.close()
    }
}