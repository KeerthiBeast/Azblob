package com.example.azblob.utils

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.azblob.api.AzureRetrofitInstance
import com.example.azblob.model.Blob
import com.example.azblob.model.BlobFinal
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/* View Model to get songs from the api
* Also includes searching for songs using flow filters
* Gets the songs from the api and saves it in a MutableStateFlow saving a MutableStateList
* Calls getSongs functions when initiated */

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(FlowPreview::class)
class BlobViewModel(): ViewModel() {

    private val _inProgress = MutableStateFlow(true)
    val inProgress = _inProgress.asStateFlow()

    private val _blobs = mutableListOf<Blob>() //Saving from the api
    private val _searchBlobs = MutableStateFlow(mutableListOf<BlobFinal>()) //Saving the list from api

    private val _searchText = MutableStateFlow("") //To search for songs
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false) //To indicate searching state
    val isSearching = _isSearching.asStateFlow()

    private val _toDownload = MutableStateFlow(mutableListOf<BlobFinal>())
    val toDownload = _toDownload.asStateFlow()
    private val _downloadSize = MutableStateFlow(0)
    val downloadSize = _downloadSize.asStateFlow()

    init {
        getSongs()
    }

    /* Creates a authorization header for the api request to Azure Rest API
    *  For more info refer: https://learn.microsoft.com/en-us/rest/api/storageservices/authorize-with-shared-key */
    @RequiresApi(Build.VERSION_CODES.O)
    fun generateAuthorizationHeader(
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun getSongs() {
        //Date Time and header for authorization purpose
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

        //Get songs from the api
        viewModelScope.launch() {
            try {
                val response = AzureRetrofitInstance.azapi.listBlobs(
                    authorization = authorization,
                    date = date
                )
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("Success", "Success")
                    responseBody?.blobs?.blobList?.let {
                        _blobs.clear()
                        _blobs.addAll(it)
                        _searchBlobs.value = localFileList(_blobs)
                        _inProgress.value = false
                    }
                } else {
                    Log.e("Response", response.code().toString())
                }
            } catch (e: HttpException) {
                Log.e("Exception", e.toString())
            } catch (e: Exception) {
                Log.e("Exception", e.toString())
            }
        }
    }

    //Mark files if they are present in the local folder with Green or leave it as White
    fun localFileList(blobs: List<Blob>): MutableList<BlobFinal> {
        var fileList: List<String>?
        val blobFileList = mutableListOf<BlobFinal>()
        val toDownloadList = mutableListOf<BlobFinal>()
        viewModelScope.launch() {
            fileList = syncFolder()
            blobs.forEach{it->
                if(fileList?.contains(it.name) == true) {
                    it.name?.let { it1 -> it.url?.let { it2 -> BlobFinal(it1, it2, Color.Green) } }
                        ?.let { it2 -> blobFileList.add(it2) }

                } else {
                    it.name?.let { it1 -> it.url?.let {it2 -> BlobFinal(it1, it2, Color.White)}}
                        ?.let { it2 -> blobFileList.add(it2)}
                    it.name?.let { it1 -> it.url?.let { it2 -> BlobFinal(it1, it2, Color.White) } }
                        ?.let { it2 -> toDownloadList.add(it2) }
                }
            }
            _toDownload.value = toDownloadList
            _downloadSize.value = toDownloadList.size
        }
        return blobFileList
    }

    /*Search for songs using flow filters and update the blobList value
    * which is exposed to the user side */
    val blobList = searchText
        .debounce(1000L)
        .onEach { _isSearching.update { true } }
        .combine(_searchBlobs) { text, blobs ->
            if(text.isBlank()) {
                blobs
            } else {
                delay(200L)
                blobs.filter {
                    it.name.contains(text, ignoreCase = true)
                }
            }
        }
        .onEach { _isSearching.update { false } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _searchBlobs.value
        )

    //Function used in the user side to update the searchText to change the values displayed
    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

}