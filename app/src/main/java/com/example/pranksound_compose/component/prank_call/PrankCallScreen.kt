package com.example.pranksound_compose.component.prank_call

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pranksound.data.dto.prank.PrankCall
import com.example.pranksound_compose.ui.theme.Black
import com.example.pranksoundalpha.viewmodel.PrankCallViewModel

@Composable
fun PrankCallScreen(navController: NavController) {
    val callViewModel: PrankCallViewModel = hiltViewModel()

    // collect StateFlow
    val prankCallList by callViewModel.prankCallList.collectAsState()

    // load data 1 lần
    LaunchedEffect(Unit) {
        callViewModel.getPrankCall()
    }
    Box(
        modifier = Modifier
            .background(color = Black)
            .fillMaxSize()
    ) {
        CallGrid(
            navController,
            items = prankCallList,
        )
    }
}


@Composable
fun CallGrid(
    navController: NavController,
    items: List<PrankCall>,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = items,
            key = { it.id } // nếu có id
        ) { prankCall ->
            ItemCall(
                data = prankCall,
                onClick = {
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("call_model", prankCall)



                    navController.navigate("call_graph") {
                        popUpTo("main_screen") { inclusive = false }
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun PreviewPrankCallScreen() {

}