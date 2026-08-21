package com.example.pranksound_compose

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.util.UnstableApi
import com.example.pranksound_compose.navigation.AppNavHost
import com.example.pranksound_compose.ui.theme.Black
import com.example.pranksound_compose.ui.theme.PrankSound_ComposeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Cho phép vẽ full screen
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        // Ẩn status bar + navigation bar
        controller.hide(WindowInsetsCompat.Type.systemBars())
        // Vuốt từ cạnh để hiện tạm thời
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        setContent {
            PrankSound_ComposeTheme {
                Scaffold(
                    containerColor = Black,
                    modifier = Modifier.fillMaxSize(),
                    // RẤT QUAN TRỌNG để không chừa padding cho system bar
                    contentWindowInsets = WindowInsets(0)
                ) {
                    AppNavHost()
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            // Đảm bảo bar không tự hiện lại khi resume
            WindowInsetsControllerCompat(window, window.decorView)
                .hide(WindowInsetsCompat.Type.systemBars())
        }
    }

}
