package com.example.azblob.ui.screen

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.azblob.ui.screen.settings.SettingsScreen
import com.example.azblob.ui.screen.songs.SongsScreen
import com.example.azblob.ui.screen.sync.SyncScreen

/*Manages navigation of the application using NavHostController
* PaddingValue is passed to the activity to apply padding value of the scaffold view to the screen*/

//Navigation routes
object NavName {
    const val home = "Blob"
    const val sync = "Sync"
    const val about = "Settings"
}

//Main navigation function
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun Navigation(activity: ComponentActivity, navController: NavHostController, startDest: String, paddingValues: PaddingValues) {
    NavHost(navController = navController, startDestination = startDest) {
        composable(NavName.sync) {
            SyncScreen(activity = activity, paddingValues =  paddingValues)
        }
        composable(NavName.home) {
            SongsScreen(
                paddingValues = paddingValues,
                activity = activity,
            )
        }
        composable(NavName.about) {
            SettingsScreen(paddingValues = paddingValues, context = activity)
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

