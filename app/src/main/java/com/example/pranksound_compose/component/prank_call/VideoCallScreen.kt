@file:JvmName("VideoCallScreenKt")

package com.example.pranksound_compose.component.prank_call

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.example.pranksound.data.dto.prank.PrankCall
import com.example.pranksound_compose.R
import com.example.pranksoundalpha.viewmodel.PrankCallViewModel
import kotlinx.coroutines.delay

@UnstableApi
@Composable
fun VideoCallScreen(
    navController: NavController,
    model: PrankCall,
    viewModel: PrankCallViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var startTime by remember { mutableStateOf<Long?>(null) }
    var cameraSelector by remember {
        mutableStateOf(CameraSelector.DEFAULT_FRONT_CAMERA)
    }
    val isCameraOn by viewModel.isCameraOn.collectAsStateWithLifecycle()
    val isMicOn by viewModel.isMicOn.collectAsStateWithLifecycle()
    val isSpeakerOn by viewModel.isSpeakerOn.collectAsStateWithLifecycle()

    // 🔹 Camera permission
    val (hasCameraPermission, requestCameraPermission) =
        rememberCameraPermissionState()

    /* ======================
     * ExoPlayer
     * ====================== */
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }

    LaunchedEffect(model.videoLink) {
        exoPlayer.setMediaItem(MediaItem.fromUri(model.videoLink))
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    LaunchedEffect(isSpeakerOn) {
        exoPlayer.volume = if (isSpeakerOn) 1f else 0f
    }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                // Video bắt đầu phát lần đầu
                if (isPlaying && startTime == null) {
                    startTime = System.currentTimeMillis()
                }
            }
        }

        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }
    /* ======================
     * UI
     * ====================== */
    Box(modifier.fillMaxSize()) {
        // Video nền
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                PlayerView(it).apply {
                    player = exoPlayer
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                }
            }
        )

        // Camera PiP
        if (isCameraOn) {
            if (hasCameraPermission) {
                Card(
                    modifier = Modifier
                        .size(110.dp, 200.dp)
                        .align(Alignment.TopEnd)
                        .padding(top = 48.dp, end = 16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    CameraPreview(
                        modifier = Modifier.fillMaxSize(),
                        cameraSelector = cameraSelector
                    )
                }
            } else {
                CameraPermissionDialog(
                    onConfirm = { requestCameraPermission() },
                    onDismiss = {
                        viewModel.isCameraOn.value = false
                    }
                )
            }
        }

        // Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color(0x66000000))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            ControlButton(
                icon = if (isCameraOn)
                    R.drawable.ic_camera
                else
                    R.drawable.ic_close_camera
            ) {
                viewModel.isCameraOn.value = !isCameraOn
            }

            ControlButton(
                icon = if (isSpeakerOn)
                    R.drawable.ic_volum
                else
                    R.drawable.ic_volume_close
            ) {
                viewModel.isSpeakerOn.value = !isSpeakerOn
            }

            ControlButton(R.drawable.ic_filip_camera) {
                cameraSelector =
                    if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA)
                        CameraSelector.DEFAULT_BACK_CAMERA
                    else
                        CameraSelector.DEFAULT_FRONT_CAMERA
            }

            ControlButton(
                icon = if (isMicOn)
                    R.drawable.ic_mic
                else
                    R.drawable.ic_mic_close
            ) {
                viewModel.isMicOn.value = !isMicOn
            }

            ControlButton(R.drawable.ic_decline_video) {
                val duration =
                    startTime?.let { (System.currentTimeMillis() - it) / 1000 }
                Log.e("START_Time", duration?.toInt().toString())
                viewModel.endCall(duration)
                viewModel.setStateUI(CallUiState.ENDED)
                navController.popBackStack()
            }
        }
    }
}

@Composable
private fun ControlButton(
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier,
    coverColor: Color = Color(0x33000000),
    onClick: () -> Unit = {}
) {
    var pressed by remember { mutableStateOf(false) }
    LaunchedEffect(pressed) {
        if (pressed) {
            delay(100)
            pressed = false
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(55.dp)
            .clip(CircleShape)
            .clickable {
                pressed = true
                onClick()
            }
    ) {
        // Icon
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(55.dp)
        )

        // Circle cover
        if (pressed) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(coverColor, CircleShape)
            )
        }
    }
}

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    cameraSelector: CameraSelector
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }

    LaunchedEffect(cameraSelector) {
        val cameraProvider = ProcessCameraProvider
            .getInstance(context)
            .get()

        val preview = Preview.Builder().build().apply {
            setSurfaceProvider(previewView.surfaceProvider)
        }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { previewView }
    )
}


@Composable
fun rememberCameraPermissionState(): Pair<Boolean, () -> Unit> {
    val context = LocalContext.current

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    val requestPermission = {
        launcher.launch(Manifest.permission.CAMERA)
    }

    return hasPermission to requestPermission
}

@Composable
fun CameraPermissionDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.allow_camera_access))
        },
        text = {
            Text(text = stringResource(R.string.the_app_needs_camera_permission_to_display_video_calls))
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(R.string.allow))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    )
}

@UnstableApi
@androidx.compose.ui.tooling.preview.Preview
@Composable
fun PreviewVideoCallScreen() {

}