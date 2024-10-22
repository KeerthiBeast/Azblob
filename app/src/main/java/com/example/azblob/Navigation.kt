package com.example.azblob

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

/*Manages navigation of the application using NavHostController
* PaddingValue is passed to the activity to apply padding value of the scaffold view to the screen*/

//Navigation routes
object NavName {
    const val home = "Blob"
    const val sync = "Sync"
    const val about = "Settings"
    const val download = "Download"
}

//Main navigation function
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun Navigation(activity: ComponentActivity, navController: NavHostController, startDest: String, paddingValues: PaddingValues) {
    NavHost(navController = navController, startDestination = startDest) {
        composable(NavName.sync) {
            SyncDownload(activity = activity, paddingValues)
        }
        composable(NavName.home) {
            CloudStorage(paddingValues = paddingValues, activity = activity)
        }
        composable(NavName.about) {
            FolderPickerScreen(paddingValues = paddingValues, context = activity)
        }
        composable(NavName.download) {
            ToDownload(paddingValues = paddingValues, activity = activity)
        }
    }
}

//Sealed class to create navigation routes with objects of the class type Screens
sealed class Screens(val route: String,
              val selected: ImageVector,
              val unselected: ImageVector,
              val label: String) {

    data object Home: Screens(
        route = NavName.home,
        selected = Icons.Default.Favorite,
        unselected = Icons.Default.FavoriteBorder,
        label = NavName.home
    )
    data object Sync: Screens(
        route = NavName.sync,
        selected = Icons.Default.Add,
        unselected = Icons.Default.AddCircle,
        label = NavName.sync
    )
    data object About: Screens(
        route = NavName.about,
        selected = Icons.Default.Settings,
        unselected = Icons.Default.Settings,
        label = NavName.about
    )
}

