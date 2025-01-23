package com.example.azblob.ui.screen.songs

import android.annotation.SuppressLint
import android.graphics.Bitmap
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
import androidx.compose.material3.IconToggleButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SongsScreen(
    viewModel: SongsViewModel = hiltViewModel(),
    paddingValues: PaddingValues,
    activity: ComponentActivity,
) {
    //StartUp Loading Animation
    val blobs by viewModel.blobList.collectAsState()
    val toDownload by viewModel.downloadList.collectAsState()
    val downloadSize by viewModel.downloadSize.collectAsState()

    val searchText by viewModel.searchText.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()

    var changeScreen by remember { mutableStateOf(false) }

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
                    if(changeScreen)Text("To Download")
                    else Text("Songs")
                },
                actions = {
                    IconToggleButton(
                        checked = changeScreen,
                        onCheckedChange = { changeScreen = it }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.download_all),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .nestedScroll(pullToRefreshState.nestedScrollConnection)
                .padding(innerPadding)
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

                    if(changeScreen) {
                        item {
                            Text("$downloadSize Songs to Download")
                        }

                        items(toDownload) {
                            BlobItem(it, viewModel, activity)
                        }
                    }

                    else {
                        items(blobs) {
                            BlobItem(it, viewModel, activity)
                        }
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

            if(changeScreen && downloadSize>0) {
                DownloadAllSongs(activity, toDownload, paddingValues, viewModel)
            }
            AnimatedVisibility(
                visible = !searchBarVisibility,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                GoToTop(
                    paddingValues = paddingValues,
                    goToTop = {
                        scope.launch {
                            lazyListState.scrollToItem(0)
                        }
                    }
                )
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
@Composable
fun BlobItem(
    blob: BlobFinal,
    viewModel: SongsViewModel,
    activity: ComponentActivity,
) {
    val name = blob.name
    val fname = if(name != "Unknown Blob") {
        name.substring(0, name.length-4)
    } else {
        name
    }

    //Get the cover art of the song
    val scope = rememberCoroutineScope()
    var cover: Bitmap? by remember {
        mutableStateOf(null)
    }
    //Launch the function in IO thread to reduce main thread overhead
   LaunchedEffect(blob.uri != null) {
       scope.launch {
           withContext(Dispatchers.IO) {
               cover = extractAlbumArt(activity, blob.uri)
           }
       }
   }

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        if(cover != null) {
            Image(
                bitmap = cover!!.asImageBitmap(),
                contentDescription = "Something",
                modifier = Modifier
                    .height(50.dp)
                    .width(50.dp)
                    .clip(RoundedCornerShape(15))
            )
        }
        else {
            Image(
                painter = painterResource(R.drawable.aziconart),
                contentDescription = "Something",
                modifier = Modifier
                    .height(50.dp)
                    .width(50.dp)
                    .clip(RoundedCornerShape(15))
            )
        }
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
fun GoToTop(
    goToTop: () -> Unit,
    paddingValues: PaddingValues
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(bottom = paddingValues.calculateBottomPadding())
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
fun DownloadAllSongs(
    activity: ComponentActivity,
    toDownload: List<BlobFinal>,
    paddingValues: PaddingValues,
    viewModel: SongsViewModel
) {
    val downloadQueue = ArrayDeque(toDownload)
    Box(modifier = Modifier
        .fillMaxSize()
    ) {
        FloatingActionButton(
            modifier = Modifier
                .padding(bottom = paddingValues.calculateBottomPadding())
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