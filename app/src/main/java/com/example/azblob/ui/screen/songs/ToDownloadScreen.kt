package com.example.azblob.ui.screen.songs

import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.azblob.R
import com.example.azblob.domain.model.BlobFinal
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ToDownloadScreen(
    viewModel: SongsViewModel = hiltViewModel(),
    paddingValues: PaddingValues,
    activity: ComponentActivity
) {
    val blobs by viewModel.downloadList.collectAsState()
    val searchText by viewModel.searchText.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val downloadSize by viewModel.downloadSize.collectAsState()

    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val pullToRefreshState = rememberPullToRefreshState()

    val searchBarVisibility by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex <= 1 && lazyListState.firstVisibleItemScrollOffset <= 1
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Download List")
                },
            )
        }
    ) { innerPadding->
        Box(
            modifier = Modifier
                .nestedScroll(pullToRefreshState.nestedScrollConnection)
                .padding(innerPadding)
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
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
                        .fillMaxSize()
                        .padding(bottom = paddingValues.calculateBottomPadding()),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
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

                    item {
                        Text("$downloadSize Songs to Download")
                    }

                    items(blobs) { it ->
                        BlobItemDownload(it, viewModel)
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

            if(downloadSize > 0) DownloadAll(activity, blobs, viewModel)
            AnimatedVisibility(
                visible = !searchBarVisibility,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                GoToTopDownload {
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
}

/*Function to get name and download link of the song
* Changes text color to green if song is present in the selected directory*/
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun BlobItemDownload(blob: BlobFinal, viewModel: SongsViewModel) {
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
            IconButton(onClick = { viewModel.downloadFile(blob.url, name) }) {
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
fun DownloadAll(
    activity: ComponentActivity,
    toDownload: List<BlobFinal>,
    viewModel: SongsViewModel
) {
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
                viewModel.downloadBatch(downloadQueue)
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.download_all),
                contentDescription = null
            )
        }
    }
}