package com.example.azblob.data.repository

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.documentfile.provider.DocumentFile
import com.example.azblob.data.network.AzureApi
import com.example.azblob.data.network.dto.Blob
import com.example.azblob.domain.model.BlobFinal
import com.example.azblob.domain.repository.AzureRepository
import com.example.azblob.utils.Utils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
class AzureRepositoryImpl @Inject constructor(
    private val api: AzureApi,
    @ApplicationContext private val context: Context
): AzureRepository {

    override suspend fun getSongs(): List<BlobFinal> {
        val authPair = getAuthorizationHeader()
        val authorization = authPair.first
        val date = authPair.second

        try {
            val response = api.getBlobs(
                authorization = authorization,
                date = date
            )
            if (response.isSuccessful) {
                val responseBody = response.body()?.blobs?.blobList
                return if (responseBody != null) {
                    withContext(Dispatchers.IO) { localFileList(responseBody) }
                }
                else emptyList()
            } else {
                return emptyList()
            }
        } catch (e: Exception) {
            Log.e("Exception", e.toString())
            return emptyList()
        }
    }

    private fun getAuthorizationHeader(): Pair<String, String> {
        val date = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US).apply {
            val timeZone = TimeZone.getTimeZone("UTC")
        }.format(Date())

        val authorization = generateAuthorizationHeader(
            verb = "GET",
            accountName = Utils.account,
            accountKey = Utils.accountKey,
            date = date,
            containerName = Utils.container
        )

        return authorization to date
    }

    private fun generateAuthorizationHeader(
        verb: String,
        accountName: String,
        accountKey: String,
        date: String,
        containerName: String
    ): String {
        val stringToSign =
            "$verb\n\n\n\n\n\n\n\n\n\n\n\n\nx-ms-date:$date\nx-ms-version:2015-04-05\n/$accountName/$containerName\ncomp:list\nrestype:container"
        val decodedKey = Base64.getDecoder().decode(accountKey)
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(decodedKey, "HmacSHA256"))
        val signature = Base64.getEncoder()
            .encodeToString(mac.doFinal(stringToSign.toByteArray(Charsets.UTF_8)))
        return "SharedKey $accountName:$signature"
    }

    private fun localFileList(blobs: List<Blob>): List<BlobFinal> {
        var fileList: Set<String> = emptySet()
        val context = context

        //Get folder URI
        val folderUriKey = "folder_uri_key"
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val selectedFolderUri = sharedPreferences?.getString(folderUriKey, null)?.let { Uri.parse(it) }

        //Search the folder
        if (selectedFolderUri != null) {
            val documentFile = DocumentFile.fromTreeUri(context, selectedFolderUri)
            if(documentFile != null && documentFile.canRead()) {
                fileList =  documentFile.listFiles().mapNotNull { it.name }.toSet()
            }
        }

        return blobs.map{blob->
            val color = if(fileList.contains(blob.name)) Color.Green else Color.White
            BlobFinal(
                name = blob.name ?: "",
                url = blob.url ?: "",
                color = color
            )
        }
    }
}