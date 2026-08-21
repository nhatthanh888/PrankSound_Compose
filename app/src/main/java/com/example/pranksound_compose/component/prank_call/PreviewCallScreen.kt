package com.example.pranksound_compose.component.prank_call

import android.annotation.SuppressLint
import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.pranksound.data.dto.prank.PrankCall
import com.example.pranksound_compose.R
import com.example.pranksound_compose.ui.theme.Black
import com.example.pranksound_compose.ui.theme.White
import com.example.pranksoundalpha.viewmodel.PrankCallViewModel

@SuppressLint("DefaultLocale")
@Composable
fun PreviewCallScreen(
    navController: NavController,
    model: PrankCall,
    viewModel: PrankCallViewModel
) {
    val context = LocalContext.current

    val contact by viewModel.prankCall.collectAsStateWithLifecycle()
    val callDuration by viewModel.callDuration.collectAsStateWithLifecycle()

    // var uiState by remember { mutableStateOf(CallUiState.INCOMING) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var durationText by remember { mutableStateOf("") }
    var isExiting by remember { mutableStateOf(false) }

    /* -------------------- MediaPlayer -------------------- */
    val mediaPlayer = remember {
        MediaPlayer.create(context, R.raw.audio_call_prank).apply {
            isLooping = true
        }
    }

    LaunchedEffect(uiState) {
        if (uiState == CallUiState.INCOMING) {
            if (!mediaPlayer.isPlaying) {
                mediaPlayer.start()
            }
        } else {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            }
        }
    }

    // Release đúng lúc
    DisposableEffect(Unit) {
        onDispose {
            try {
                mediaPlayer.stop()
            } catch (_: IllegalStateException) {
            }
            mediaPlayer.release()
        }
    }
    LaunchedEffect(Unit) {
        viewModel.selectContact(model)
    }

    /* -------------------- Observe call duration -------------------- */
    LaunchedEffect(callDuration) {
        callDuration?.let {
            viewModel.setStateUI(CallUiState.ENDED)
           // uiState = CallUiState.ENDED
            val minutes = it / 60
            val seconds = it % 60
            durationText = String.format("%02d:%02d", minutes, seconds)
            viewModel.clearCallDuration()
        }
    }

    if (!isExiting) {
        contact?.let { model ->
            PreviewCallContent(
                model = model,
                uiState = uiState,
                durationText = durationText,
                onDecline = {
                    mediaPlayer.pause()
                    mediaPlayer.seekTo(0)
                    viewModel.setStateUI(CallUiState.ENDED)
                   // uiState = CallUiState.ENDED
                },
                onAccept = {
                    mediaPlayer.pause()
                    mediaPlayer.seekTo(0)
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("call_model", model)
                    navController.navigate("video_call_screen")
                },
                onReturn = {
                    isExiting = true
                    navController.popBackStack()
                },
                onRecall = {
                    durationText = ""
                   // uiState = CallUiState.INCOMING
                    viewModel.setStateUI(CallUiState.INCOMING)
                }
            )
        }
    }
}

@Composable
fun PreviewCallContent(
    model: PrankCall,
    uiState: CallUiState,
    durationText: String = "",
    onAccept: () -> Unit = {},
    onDecline: () -> Unit = {},
    onReturn: () -> Unit = {},
    onRecall: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AsyncImage(
            model = model.thumb,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {

            Spacer(modifier = Modifier.height(100.dp))

            // Avatar
            Card(
                shape = CircleShape,
                modifier = Modifier.size(150.dp)
            ) {
                AsyncImage(
                    model = model.avatar,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (uiState == CallUiState.INCOMING)
                    model.title else stringResource(R.string.call_end),
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = when {
                    durationText.isNotEmpty() -> durationText
                    uiState == CallUiState.INCOMING -> stringResource(R.string.incoming_call)
                    else -> stringResource(R.string.no_time)
                },
                color = Color.White
            )

            Spacer(modifier = Modifier.weight(1f))

            if (uiState == CallUiState.INCOMING) {
                IncomingActions(onAccept, onDecline)
            } else {
                EndCallActions(onReturn, onRecall)
            }
        }
    }
}

@Composable
fun IncomingActions(
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 48.dp, start = 32.dp, end = 32.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { onDecline() }) {
            Image(
                painter = painterResource(R.drawable.btn_decline),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)

            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.decline),
                color = Color(0xFFFAFAFA),
                fontFamily = FontFamily(Font(R.font.quick_sans)),
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { onAccept() }) {
            Image(
                painter = painterResource(R.drawable.btn_accept),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.accept),
                color = Color(0xFFFAFAFA),
                fontFamily = FontFamily(Font(R.font.quick_sans)),
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun EndCallActions(
    onReturn: () -> Unit,
    onRecall: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 60.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Button(
            onClick = { onReturn() },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .background(
                    color = Color(0x33E2E1E0),
                    shape = RoundedCornerShape(16)
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
        ) {
            Text(
                text = stringResource(R.string.return_),
                fontFamily = FontFamily(Font(R.font.inter_bold)),
                fontSize = 16.sp,
                color = White
            )
        }

        Button(
            onClick = { onRecall() },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFB2FEFA),
                            Color(0xFF0ED2F7)
                        )
                    ),
                    shape = RoundedCornerShape(16)
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
        ) {
            Text(
                text = stringResource(R.string.recall),
                fontFamily = FontFamily(Font(R.font.inter_bold)),
                fontSize = 16.sp,
                color = Black
            )
        }
    }
}

@Preview
@Composable
fun PreView() {
    PreviewCallContent(uiState = CallUiState.INCOMING, model = PrankCall())
}
