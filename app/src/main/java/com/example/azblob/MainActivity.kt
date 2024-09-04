package com.example.azblob

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.azblob.ui.theme.AzblobTheme

/*Has the scaffold view for the entire app
* Has the bottom navigation bar and top app bar*/

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AzblobTheme {
                val  navController = rememberNavController()
                TopBottom(navController = navController, activity = this)
            }
        }
    }

}

//Function for the Top and Bottom bar
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBottom(navController: NavHostController, activity: ComponentActivity) {
    //Backstack navigation to get current destination and save state of the screen
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    var currItem by rememberSaveable {
        mutableStateOf(0)
    }
    var title by rememberSaveable { //Title change with the navigation
        mutableStateOf(Screens.Home.label)
    }
    val Items = listOf( //Navigation items
        Screens.Home,
        Screens.Sync,
        Screens.About
    )

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(title)
                },
            )
        },
        bottomBar = {
            NavigationBar {
                Items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = currItem == index,
                        onClick = {
                            if (item.route != currentDestination?.route) {
                                currItem = index
                                title = item.label
                                //Pops the backstack to the start destination and save the state of the screen
                                navController.navigate(item.route) {
                                    if(currentDestination != null) {
                                        popUpTo(currentDestination.id) {
                                            saveState = true
                                            inclusive = true
                                        }
                                    } else {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                            inclusive = true
                                        }
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        label = {
                            Text(text = item.label)
                        },
                        icon = {
                            Icon(
                                imageVector = if (currItem == index) {
                                    item.selected
                                } else item.unselected,
                                contentDescription = item.label
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Navigation(activity = activity, navController = navController, startDest = Screens.Home.route, paddingValues = innerPadding)
    }
}