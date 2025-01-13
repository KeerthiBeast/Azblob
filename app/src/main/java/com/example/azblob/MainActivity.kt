package com.example.azblob

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.azblob.ui.screen.TopBottom
import com.example.azblob.ui.screen.settings.AuthViewModel
import com.example.azblob.ui.theme.AzblobTheme
import dagger.hilt.android.AndroidEntryPoint

/*Has the scaffold view for the entire app
* Has the bottom navigation bar and top app bar*/

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AzblobTheme {
                viewModel = hiltViewModel()
                val navController = rememberNavController()
                TopBottom(
                    navController = navController,
                    activity = this
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val uri = intent.data
        if(uri != null && uri.scheme == "azblob" && uri.host == "callback") {
            val code = uri.getQueryParameter("code")
            if (code != null) {
                viewModel.getToken(code)
            }
            this.getSharedPreferences("app_prefs", MODE_PRIVATE)
                .edit()
                .putString("code", code)
                .apply()
        }
    }
}

