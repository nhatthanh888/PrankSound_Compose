package com.example.pranksound_compose.component.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pranksound_compose.R
import com.example.pranksound_compose.component.favorite.FavoriteScreen
import com.example.pranksound_compose.component.home.HomeScreen
import com.example.pranksound_compose.component.prank_call.PrankCallScreen
import com.example.pranksound_compose.component.trending.TrendingScreen
import com.example.pranksound_compose.ui.theme.Black

@Composable
fun MainScreen(navControllerApp: NavController) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()

    val currentRoute =
        currentBackStack?.destination?.route ?: MainRoute.Home.route

    val currentScreen = when (currentRoute) {
        MainRoute.PrankCall.route -> MainRoute.PrankCall
        MainRoute.Trending.route -> MainRoute.Trending
        MainRoute.Favorite.route -> MainRoute.Favorite
        else -> MainRoute.Home
    }

    Scaffold(
        containerColor = Black,
        topBar = {
            MainTopBar(
                titleRes = currentScreen.title,
                onSettingClick = {
                    navControllerApp.navigate("setting_screen") {
                        popUpTo("main_screen")
                    }
                }
            )
        },
        bottomBar = {
            MainBottomBar(
                currentRoute = currentRoute,
                onTabSelected = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = MainRoute.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(MainRoute.Home.route) { HomeScreen(navControllerApp) }
            composable(MainRoute.PrankCall.route) { PrankCallScreen(navControllerApp) }
            composable(MainRoute.Trending.route) { TrendingScreen(navControllerApp) }
            composable(MainRoute.Favorite.route) { FavoriteScreen(navControllerApp) }
        }
    }
}

@Composable
fun MainTopBar(
    @StringRes titleRes: Int,
    onSettingClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = 40.dp,
                bottom = 16.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        GradientText(
            text = stringResource(id = titleRes),
            fontSize = 22.sp,
            modifier = Modifier.weight(1f)
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_setting),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .size(24.dp)
                .clickable { onSettingClick() }
        )
    }
}

@Composable
fun MainBottomBar(
    currentRoute: String,
    onTabSelected: (String) -> Unit = {}
) {

    Box {
        Column() {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(Color(0xFFB2FEFA))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(color = Black),
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            BottomBarItem(
                modifier = Modifier.weight(1f),
                selected = currentRoute == MainRoute.Home.route,
                icon = R.drawable.ic_home,
                selectedIcon = R.drawable.ic_home_selected,
                label = R.string.home
            ) { onTabSelected(MainRoute.Home.route) }

            BottomBarItem(
                modifier = Modifier.weight(1f),
                selected = currentRoute == MainRoute.PrankCall.route,
                icon = R.drawable.ic_prank_call,
                selectedIcon = R.drawable.ic_prank_call_selected,
                label = R.string.prank_call
            ) { onTabSelected(MainRoute.PrankCall.route) }

            BottomBarItem(
                modifier = Modifier.weight(1f),
                selected = currentRoute == MainRoute.Trending.route,
                icon = R.drawable.ic_trending,
                selectedIcon = R.drawable.ic_trending_slected,
                label = R.string.trending
            ) { onTabSelected(MainRoute.Trending.route) }

            BottomBarItem(
                modifier = Modifier.weight(1f),
                selected = currentRoute == MainRoute.Favorite.route,
                icon = R.drawable.ic_favorite,
                selectedIcon = R.drawable.ic_favorite_selected,
                label = R.string.favorite
            ) { onTabSelected(MainRoute.Favorite.route) }
        }
    }
}

@Composable
fun BottomBarItem(
    modifier: Modifier,
    selected: Boolean,
    @DrawableRes icon: Int,
    @DrawableRes selectedIcon: Int,
    @StringRes label: Int,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(
                id = if (selected) selectedIcon else icon
            ),
            contentDescription = null,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(id = label),
            fontSize = 12.sp,
            style = TextStyle(
                brush = Brush.verticalGradient(
                    colors = if (selected) {
                        listOf(
                            Color(0xFFB2FEFA),
                            Color(0xFF0ED2F7)
                        )
                    } else {
                        listOf(
                            Color(0xFFD1D2DC),
                            Color(0xFFD1D2DC)
                        )
                    }
                )
            ),
            color = Color(0xFFD1D2DC),
            fontFamily = FontFamily(Font(R.font.outfit_medium))
        )

    }
}

@Composable
fun GradientText(
    text: String,
    fontSize: TextUnit,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontSize = fontSize,
        fontFamily = FontFamily(Font(R.font.outfit_medium)),
        style = TextStyle(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFB2FEFA),
                    Color(0xFF0ED2F7)
                )
            )
        ),
        modifier = modifier
    )
}

sealed class MainRoute(
    val route: String,
    @StringRes val title: Int
) {

    object Home : MainRoute(
        route = "home",
        title = R.string.prank_sound
    )

    object PrankCall : MainRoute(
        route = "prank_call",
        title = R.string.prank_call2
    )

    object Trending : MainRoute(
        route = "trending",
        title = R.string.prank_sound
    )

    object Favorite : MainRoute(
        route = "favorite",
        title = R.string.prank_sound
    )
}

@Preview
@Composable
fun PreviewMainScreen() {
    MainBottomBar("")
}