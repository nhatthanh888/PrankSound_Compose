package com.example.pranksound_compose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.pranksound.data.dto.prank.PrankCall
import com.example.pranksound.data.dto.prank.SoundFolder
import com.example.pranksound_compose.component.custom_sound.CustomSoundScreen
import com.example.pranksound_compose.component.detail_sound.DetailSoundScreen
import com.example.pranksound_compose.component.main.MainScreen
import com.example.pranksound_compose.component.prank_call.PreviewCallScreen
import com.example.pranksound_compose.component.prank_call.VideoCallScreen
import com.example.pranksound_compose.component.setting.SettingScreen
import com.example.pranksound_compose.component.splash.SplashScreen
import com.example.pranksound_compose.component.wellcome.WelcomeScreen
import com.example.pranksoundalpha.viewmodel.PrankCallViewModel

@UnstableApi
@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {

        composable("splash") {
            SplashScreen(navController)
        }

        composable("welcome") {
            WelcomeScreen(navController)
        }

        composable("main_screen") {
            MainScreen(navController)
        }

        composable("custom_sound_screen") {
            CustomSoundScreen(navController)
        }

        composable("detail_sound_screen") {
            val model = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<SoundFolder>("sound_model")

            model?.let { DetailSoundScreen(navController, soundFolder = it) }
        }

        navigation(
            route = "call_graph",
            startDestination = "preview_call_screen"
        ) {
            composable("preview_call_screen") {
                val parentEntry = remember(it) {
                    navController.getBackStackEntry("call_graph")
                }

                val viewModel: PrankCallViewModel =
                    hiltViewModel(parentEntry)

                val model =
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.get<PrankCall>("call_model")

                model?.let {
                    PreviewCallScreen(
                        navController = navController,
                        model = it,
                        viewModel = viewModel
                    )
                }
            }

            composable("video_call_screen") {
                val parentEntry = remember(it) {
                    navController.getBackStackEntry("call_graph")
                }

                val viewModel: PrankCallViewModel =
                    hiltViewModel(parentEntry)

                val model =
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.get<PrankCall>("call_model")

                model?.let {
                    VideoCallScreen(
                        navController = navController,
                        model = it,
                        viewModel = viewModel
                    )
                }
            }
        }

        composable("setting_screen") {
            SettingScreen(navController)
        }
    }
}

sealed class RootRoute(val route: String) {
    data object Main : RootRoute("main")
    data object Setting : RootRoute("setting")
}