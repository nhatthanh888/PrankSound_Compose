package com.example.pranksound_compose.component.trending

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pranksound.data.dto.prank.Sound
import com.example.pranksound_compose.component.prank_call.ItemCall
import com.example.pranksound_compose.ui.theme.Black
import com.example.pranksound_compose.ui.theme.MainColor
import com.example.pranksoundalpha.viewmodel.MainViewModel

@Composable
fun TrendingScreen(navControllerApp: NavController) {

    val viewModel: MainViewModel = hiltViewModel()
    val trendingList by viewModel.trendingSoundList.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getTrendingSoundsOnce()
    }

    Box(
        modifier = Modifier
            .background(color = Black)
            .fillMaxSize()
    ) {
        TrendingList(navControllerApp, trendingList)
    }
}

@Composable
fun TrendingList(navControllerApp: NavController, items: List<Sound>) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(items) { index, sound ->
            TrendingItem(
                data = sound,
                position = index,
                onItemClick = {
                    navControllerApp.navigate("detail_sound_screen") {
                        popUpTo("main_screen") { inclusive = false }
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun PreviewTrendingScreen() {
    //TrendingScreen()
}