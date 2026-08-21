package com.example.pranksound_compose.component.favorite

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pranksound.data.dto.prank.FavoriteSound
import com.example.pranksound.data.dto.prank.Sound
import com.example.pranksound_compose.R
import com.example.pranksound_compose.component.trending.TrendingItem
import com.example.pranksound_compose.ui.theme.Black
import com.example.pranksoundalpha.viewmodel.MainViewModel

@Composable
fun FavoriteScreen(navControllerApp: NavController) {

    val mainViewModel: MainViewModel = hiltViewModel()
    val listFavoriteSound by mainViewModel.favoriteSoundList.collectAsState()

    LaunchedEffect(Unit) {
        mainViewModel.getAllFavoriteSound()
    }

    Box(
        modifier = Modifier
            .background(color = Black)
            .fillMaxSize()
    ) {
        if (listFavoriteSound.isNotEmpty()){
            FavoriteList(navControllerApp, listFavoriteSound, mainViewModel)
        }else{
            Image(
                modifier = Modifier.align(Alignment.Center).padding(horizontal = 32.dp),
                contentScale = ContentScale.Fit,
                painter = painterResource(id = R.drawable.ic_no_data_prank),
                contentDescription = null,
            )
        }
    }
}

@Composable
fun FavoriteList(
    navControllerApp: NavController,
    items: List<FavoriteSound>,
    mainViewModel: MainViewModel
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(items) { index, model ->
            FavoriteItem(
                data = model,
                position = index,
                onItemClick = {
                    navControllerApp.navigate("detail_sound_screen") {
                        popUpTo("main_screen") { inclusive = false }
                    }
                },
                onRemoteItem = {
                    mainViewModel.deleteFavoriteSound(model)
                }
            )
        }
    }
}

@Preview
@Composable
fun PreviewFavoriteScreen() {
    FavoriteScreen(rememberNavController())
}