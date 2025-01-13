package com.example.azblob.ui.screen.settings

import android.content.Context
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun LogoutBtn(
    context: Context,
    function: () -> Unit,
) {
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    Button(
        onClick = {
            with(sharedPreferences.edit()) {
                putString("code", null)
                putString("token", null)
                putString("refresh", null)
                putString("default_playlist", null)
                apply()
            }
            function()
        },
        colors = ButtonColors(
            containerColor = Color.Red,
            disabledContentColor = Color.White,
            contentColor = Color.White,
            disabledContainerColor = Color.Red
        )
    ) {
        Text(
            text = "Logout",
        )
    }
}