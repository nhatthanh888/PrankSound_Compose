package com.example.pranksound_compose.component.home

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pranksound.data.Resource
import com.example.pranksound.data.dto.prank.SoundFolder
import com.example.pranksound_compose.R
import com.example.pranksound_compose.component.dialog.DialogPrankSettings
import com.example.pranksound_compose.component.dialog.NoInternetDialog
import com.example.pranksound_compose.ui.theme.Black
import com.example.pranksound_compose.ui.theme.ColorText
import com.example.pranksound_compose.ui.theme.MainColor
import com.example.pranksound_compose.ui.theme.White
import com.example.pranksoundalpha.viewmodel.MainViewModel

@Composable
fun HomeScreen(
    navControllerApp: NavController,
) {
    val viewModel: MainViewModel = hiltViewModel()
    val soundFolderState by viewModel.soundFolderList.observeAsState()
    val isConnected = rememberNetworkStatus()

    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(isConnected) {
        if (isConnected) {
            if (soundFolderState is Resource.Success)
                return@LaunchedEffect
            viewModel.getSoundFolders()
        }
    }


    if (!isConnected) {
        Dialog(onDismissRequest = {}) {
            NoInternetDialog()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
    ) {

        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_bg_banner_home),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .matchParentSize()
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                            text = stringResource(R.string.upload_any_sound_prank_in_action),
                            color = White,
                            fontFamily = FontFamily(Font(R.font.outfit_regular)),
                            fontSize = 16.sp
                        )
                        Text(
                            modifier = Modifier.padding(start = 16.dp, top = 10.dp, bottom = 10.dp),
                            text = stringResource(R.string.upload_a_sound_well_amplify_the_fun),
                            color = White,
                            fontFamily = FontFamily(Font(R.font.outfit_light)),
                            fontSize = 12.sp
                        )

                        Row(
                            modifier = Modifier
                                .padding(start = 16.dp, bottom = 16.dp)
                                .background(
                                    brush = Brush.verticalGradient(
                                        listOf(Color(0xFFB2FEFA), Color(0xFF0ED2F7))
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable(onClick = {
                                    navControllerApp.navigate("custom_sound_screen") {
                                        popUpTo("main_screen")
                                    }
                                }),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Image(
                                modifier = Modifier.padding(
                                    start = 10.dp,
                                    top = 8.dp,
                                    bottom = 8.dp
                                ),
                                painter = painterResource(R.drawable.ic_add_circle),
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                modifier = Modifier.padding(end = 10.dp, top = 8.dp, bottom = 8.dp),
                                text = stringResource(R.string.add_custom),
                                fontFamily = FontFamily(Font(R.font.outfit_medium)),
                                fontSize = 14.sp,
                            )
                        }

                    }

                    Image(
                        modifier = Modifier.weight(1f),
                        painter = painterResource(R.drawable.img_banner_home),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
            }

            when (val state = soundFolderState) {
                is Resource.Loading -> {
                    isLoading = true
                }

                is Resource.Success -> {
                    isLoading = false
                    SoundGrid(
                        navControllerApp,
                        items = state.data ?: emptyList()
                    )
                }

                is Resource.DataError -> {
                    isLoading = false
                }

                null -> Unit
            }

        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MainColor
            )
        }
    }
}

@Composable
fun SoundGrid(
    navControllerApp: NavController,
    items: List<SoundFolder>
) {
    val context = LocalContext.current

    val fullItems = remember(items) {
        listOf(
            SoundFolder(
                id = 0,
                name = context.getString(R.string.add_custom),
                group = "custom",
                thumb = "file:///android_asset/ic_sound_error.png"
            )
        ) + items
    }

    var selectedIndex by remember { mutableIntStateOf(-1) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(fullItems) { index, folder ->
            ItemSound(
                data = folder,
                isSelected = selectedIndex == index,
                onClick = {
                    if (folder.group == "custom") {
                        navControllerApp.navigate("custom_sound_screen") {
                            popUpTo("main_screen") { inclusive = false }
                        }
                    } else {
                        navControllerApp.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("sound_model", folder)

                        navControllerApp.navigate("detail_sound_screen") {
                            popUpTo("main_screen") { inclusive = false }
                        }
                    }
                    selectedIndex = if (selectedIndex == index) -1 else index
                }
            )
        }
    }
}

@Composable
fun rememberNetworkStatus(): Boolean {
    val context = LocalContext.current
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    var isConnected by remember {
        mutableStateOf(checkNetwork(connectivityManager))
    }

    DisposableEffect(connectivityManager) {

        val callback = object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                isConnected = checkNetwork(connectivityManager)
            }

            override fun onLost(network: Network) {
                isConnected = checkNetwork(connectivityManager)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                isConnected = networkCapabilities.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_INTERNET
                )
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)

        onDispose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }

    return isConnected
}

private fun checkNetwork(
    connectivityManager: ConnectivityManager
): Boolean {
    val network = connectivityManager.activeNetwork ?: return false
    val caps = connectivityManager.getNetworkCapabilities(network) ?: return false
    return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

@Preview
@Composable
fun PreviewHomeScreen() {
    HomeScreen(rememberNavController())
}