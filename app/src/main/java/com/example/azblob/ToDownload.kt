package com.example.azblob

import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.azblob.utils.BlobViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import com.example.azblob.model.BlobFinal
import com.example.azblob.utils.syncFolder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/*Download remaining songs no in the Folder
Almost everything is same as CloudStorage.kt*/

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ToDownload(viewModel: BlobViewModel = viewModel(), paddingValues: PaddingValues, activity: ComponentActivity) {
    val blobs = viewModel.toDownload.collectAsState()
    val searchText by viewModel.searchText.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val downloadSize by viewModel.downloadSize.collectAsState()
    val blobList by viewModel.toDownload.collectAsState()

    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val pullToRefreshState = rememberPullToRefreshState()

    val searchBarVisibility by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex <= 1 && lazyListState.firstVisibleItemScrollOffset <= 1
        }
    }
    val downloader = DownloaderImp(activity)

    Box(
        modifier = Modifier
            .nestedScroll(pullToRefreshState.nestedScrollConnection)
            .padding(paddingValues)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            //Search bar disappear animation
            AnimatedVisibility(
                visible = searchBarVisibility,
                enter = fadeIn() + slideInVertically(initialOffsetY = {-it}),
                exit = fadeOut() + slideOutVertically(targetOffsetY = {-it}),
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = viewModel::onSearchTextChange,
                    modifier = Modifier
                        .widthIn(310.dp),
                    placeholder = { Text(text = "Search") },
                    shape = MaterialTheme.shapes.extraLarge,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("$downloadSize Songs to Download")
            Spacer(modifier = Modifier.height(20.dp))

            if (isSearching) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            } else {
                LazyColumn(
                    state = lazyListState,
                    contentPadding = PaddingValues(8.dp),
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(blobs.value) { it->
                        BlobItemDownload(it, downloader)
                    }
                }


                //Pull refresh actions
                if (pullToRefreshState.isRefreshing) {
                    LaunchedEffect(true) {
                        scope.launch {
                            pullToRefreshState.startRefresh()
                            delay(1000)
                            viewModel.getSongs()
                            pullToRefreshState.endRefresh()
                        }
                    }
                }
            }
        }
        DownloadAll(activity, downloader, blobList)
        AnimatedVisibility(
            visible = !searchBarVisibility,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            GoToTopDownload() {
                scope.launch {
                    lazyListState.scrollToItem(0)
                }
            }
        }
        PullToRefreshContainer(
            state = pullToRefreshState,
            modifier = Modifier
                .align(Alignment.TopCenter),
        )
    }

}

/*Function to get name and download link of the song
* Changes text color to green if song is present in the selected directory*/
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun BlobItemDownload(blob: BlobFinal, downloader: DownloaderImp) {
    val name = blob.name
    val fname = if(name != "Unknown Blob") {
        name.substring(0, name.length-4)
    } else {
        name
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = fname,
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            color = blob.color
        )
        if(blob.color == Color.White) {
            IconButton(onClick = { downloader.downloadFile(blob.url, name) }) {
                Icon(
                    painter = painterResource(R.drawable.cloud_download),
                    contentDescription = null
                )
            }
        }
    }
}

//Button to Go to top
@Composable
fun GoToTopDownload(goToTop: () -> Unit) {
    Box(modifier = Modifier
        .fillMaxSize()
    ) {
        FloatingActionButton(
            modifier = Modifier
                .padding(16.dp)
                .size(50.dp)
                .align(Alignment.BottomEnd),
            onClick = goToTop,
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = "go to top"
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun DownloadAll(activity: ComponentActivity, downloader: DownloaderImp, toDownload: MutableList<BlobFinal>) {
    val downloadQueue = ArrayDeque(toDownload)
    Box(modifier = Modifier
        .fillMaxSize()
    ) {
        FloatingActionButton(
            modifier = Modifier
                .padding(16.dp)
                .size(50.dp)
                .align(Alignment.BottomCenter),
            onClick = {
               activity.runOnUiThread {
                   Toast.makeText(
                       activity,
                       "Download Started",
                       Toast.LENGTH_SHORT
                   ).show()
               }
                downloader.downloadFileQueue(downloadQueue)
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.download_all),
                contentDescription = null
            )
        }
    }
}