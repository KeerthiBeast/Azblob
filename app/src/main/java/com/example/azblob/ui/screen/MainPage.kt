package com.example.azblob.ui.screen

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

//Function for the Top and Bottom bar
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun TopBottom(navController: NavHostController, activity: ComponentActivity) {
    //Backstack navigation to get current destination and save state of the screen
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    var currItem by rememberSaveable {
        mutableIntStateOf(0)
    }
    val items = listOf( //Navigation items
        Screens.Home,
        Screens.Sync,
        Screens.About
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = currItem == index,
                        onClick = {
                            if (item.route != currentDestination?.route) {
                                currItem = index
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