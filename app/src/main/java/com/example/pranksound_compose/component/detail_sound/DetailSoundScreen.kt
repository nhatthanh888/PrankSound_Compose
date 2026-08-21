package com.example.pranksound_compose.component.detail_sound

import android.annotation.SuppressLint
import android.content.Context
import android.database.ContentObserver
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.pranksound.data.dto.prank.Sound
import com.example.pranksound.data.dto.prank.SoundFolder
import com.example.pranksound.utils.UtilsKotlin
import com.example.pranksound_compose.R
import com.example.pranksound_compose.component.dialog.DialogPrankSettings
import com.example.pranksound_compose.component.dialog.ShareDialog
import com.example.pranksound_compose.component.toast.showAddFavoriteToastCompose
import com.example.pranksound_compose.component.toast.showDeleteFavoriteToastCompose
import com.example.pranksound_compose.ui.theme.Black
import com.example.pranksound_compose.ui.theme.White
import com.example.pranksoundalpha.viewmodel.DetailSoundViewModel
import com.example.pranksoundalpha.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@SuppressLint("InvalidColorHexValue")
@Composable
fun DetailSoundScreen(
    navController: NavController,
    onAddSong: () -> Unit = {},
    soundFolder: SoundFolder,
    onPause: () -> Unit = {},
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mainViewModel: MainViewModel = hiltViewModel()
    val detailViewModel: DetailSoundViewModel = hiltViewModel()

    val listSoundGroup by mainViewModel.soundList.collectAsState()
    val selectedSound by mainViewModel.selectedSound.collectAsState()

    val delaySound by detailViewModel.delay.collectAsStateWithLifecycle(
        initialValue = 0 to false
    )

    val isLoop by detailViewModel.isLoop.collectAsState()

    /* ================= STATE ================= */

    var showDialog by remember { mutableStateOf(false) }
    var showDialogShare by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(false) }

    // countdown state
    var timerSeconds by remember { mutableIntStateOf(0) }
    var isCounting by remember { mutableStateOf(false) }

    var sound: Sound? by remember { mutableStateOf(null) }

    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentSound by remember { mutableStateOf<Sound?>(null) }
    var nextSound by remember { mutableStateOf(false) }
    var loop by remember { mutableStateOf(false) }
    val audioManager = remember {
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    val maxVolume = remember {
        audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    }

    var volumeState by remember { mutableFloatStateOf(0f) }
    var progressVolume by remember { mutableStateOf(0) }
    var firstPlay by remember { mutableStateOf(true) }

    fun playSound(sound: Sound) {
        Log.e("Audio_Loop", isLoop.toString())
        try {
            // Nếu đang phát bài khác → stop & release
            mediaPlayer?.let {
                it.stop()
                it.reset()
                it.release()
            }

            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, Uri.parse(sound.link))
                isLooping = isLoop
                prepare()
                // start()
                setOnCompletionListener {
                    isPlaying = false
                }
            }

            currentSound = sound
            isPlaying = true

        } catch (e: Exception) {
            e.printStackTrace()
            isPlaying = false
        }
    }

    fun togglePlayPause() {
        val selectedSound = selectedSound ?: return

        // Nếu chưa có MediaPlayer hoặc đổi bài
        if (mediaPlayer == null || currentSound?.link != selectedSound.link) {
            playSound(selectedSound)
            return
        }

        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                isPlaying = false
            } else {
                it.start()
                isPlaying = true
            }
        }
    }

    fun countdown(seconds: Int) {
        if (seconds <= 0) return
        timerSeconds = seconds
        isCounting = true
    }

    DisposableEffect(Unit) {

        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                val currentVolume =
                    audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

                val newProgress =
                    (currentVolume.toFloat() / maxVolume * 100f)

                if (volumeState != newProgress) {
                    volumeState = newProgress
                }
            }
        }

        context.contentResolver.registerContentObserver(
            Settings.System.CONTENT_URI,
            true,
            observer
        )

        onDispose {
            context.contentResolver.unregisterContentObserver(observer)
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            isCounting = false
            timerSeconds = 0
        }
    }

    LaunchedEffect(delaySound.first) {
        if (delaySound.first > 0) {
            countdown(delaySound.first)
            //  timerSeconds = delaySound.first
        } else {
            isCounting = false
            timerSeconds = 0
        }
    }

    LaunchedEffect(soundFolder) {
        mainViewModel.getSoundsByGroup(soundFolder)
    }

    fun formatTime(totalSeconds: Int): String {
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    /* ================= COUNTDOWN LOGIC ================= */

    LaunchedEffect(isCounting) {
        if (!isCounting) return@LaunchedEffect

        while (timerSeconds > 0) {
            delay(1000)
            timerSeconds--
        }

        // Khi countdown xong
        isCounting = false
        timerSeconds = 0

        // play sau countdown
        togglePlayPause()
    }

    fun startCountdown(seconds: Int) {
        timerSeconds = seconds
        isCounting = true
    }

    fun stopCountdown() {
        isCounting = false
        timerSeconds = 0
    }

    if (showDialogShare) {
        ShareDialog(
            onDismiss = { showDialogShare = false }
        )
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            DialogPrankSettings(
                detailViewModel,
                onCancel = { showDialog = false },
                onOk = {
                    showDialog = false
                }
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            /* Toolbar */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_left),
                    contentDescription = "Back",
                    tint = White,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { navController.popBackStack() }
                )

                Text(
                    text = soundFolder.name,
                    color = White,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.inter_bold)),
                    modifier = Modifier.padding(start = 16.dp)
                )

                Spacer(modifier = Modifier.weight(1f))
                selectedSound?.isFavorite?.let {
                    Image(
                        painter = painterResource(
                            if (it) R.drawable.ic_heart_selected
                            else R.drawable.ic_favorite
                        ),
                        contentDescription = "Favorite",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                isFavorite = !isFavorite
                                Log.e("isFavorite_DetailSound", isFavorite.toString())
                                selectedSound?.let { currentSound ->
                                    detailViewModel.updateFavoriteSound(currentSound)
                                }
                                if (isFavorite) {
                                    showAddFavoriteToastCompose(context)
                                } else {
                                    showDeleteFavoriteToastCompose(context)
                                }
                            }
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    painter = painterResource(id = R.drawable.ic_share),
                    contentDescription = "Share",
                    tint = White,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            showDialogShare = true
                        }
                )
            }

            var bgColor: Color
            if (selectedSound != null) {
                bgColor = Color(
                    android.graphics.Color.parseColor(
                        stringResource(id = UtilsKotlin().getColorByPos(selectedSound!!.id))
                    )
                )
            } else {
                bgColor = Color(
                    android.graphics.Color.parseColor(
                        stringResource(id = UtilsKotlin().getColorByPos(soundFolder.id))
                    )
                )
            }

            /* Logo */
            Card(
                modifier = Modifier
                    .size(160.dp)
                    .align(Alignment.CenterHorizontally),
                shape = CircleShape,
                colors = CardDefaults.cardColors(containerColor = bgColor),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(soundFolder.thumb)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                )
            }

            /* Add song / Pause */
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color(0xFFB2FEFA),
                                Color(0xFF0ED2F7)
                            )
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { togglePlayPause() }
                    .padding(horizontal = 25.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(
                        if (isPlaying)
                            R.drawable.ic_play
                        else
                            R.drawable.ic_pause
                    ),
                    contentDescription = null,
                )
            }

            /* Settings */
            Spacer(modifier = Modifier.height(80.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(
                        color = Color(0xFF80626262),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                /* Delay setting */
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(id = R.string.prank_setting),
                            color = Color.White,
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(R.font.outfit_semi_bold))
                        )
                        Text(
                            text = stringResource(id = R.string.set_delay_sound),
                            color = Color(0xFFB4B4B4),
                            fontSize = 12.sp,
                            fontFamily = FontFamily(Font(R.font.outfit_regular))
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        modifier = Modifier.clickable { showDialog = true }
                    ) {
                        Text(
                            text = if (timerSeconds > 0)
                                formatTime(timerSeconds)
                            else
                                stringResource(id = R.string.off),
                            color = Color.White,
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(R.font.inter_bold)),
                            modifier = Modifier.padding(end = 2.dp)
                        )

                        Image(
                            painter = painterResource(id = R.drawable.ic_arrow_right),
                            contentDescription = null
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                /* Volume */
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_loudspeaker),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )

                    VolumeSeekBar(
                        value = volumeState,
                        isEnable = listSoundGroup.isNotEmpty(),
                        onValueChange = { value ->
                            volumeState = value

                            val newVolume =
                                ((value / 100f) * maxVolume).roundToInt()

                            audioManager.setStreamVolume(
                                AudioManager.STREAM_MUSIC,
                                newVolume,
                                0
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.ic_loudspeaker2),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            /* Add more sounds */
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(
                        color = Color(0xFF80626262),
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                ListSoundDetail(
                    listSoundGroup,
                    onItemClick = { sound ->
                        mainViewModel.selectSound(sound)
                        playSound(sound)
                        isPlaying = false
                    })
            }
        }
    }
}

@Composable
fun ListSoundDetail(
    items: List<Sound>,
    onItemClick: (Sound) -> Unit = {}
) {
    var selectedIndex by remember { mutableIntStateOf(-1) }

    LazyRow(
        modifier = Modifier.padding(16.dp)
    ) {
        itemsIndexed(items) { index, item ->
            ItemSoundDetail(
                data = item,
                position = index,
                isSelected = selectedIndex == index,
                onClick = {
                    onItemClick(item)
                    selectedIndex = if (selectedIndex == index) -1 else index
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VolumeSeekBar(
    value: Float,
    isEnable: Boolean = true,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {

    val progress = (value / 100f).coerceIn(0f, 1f)

    Slider(
        value = value,
        enabled = isEnable,
        onValueChange = onValueChange,
        valueRange = 0f..100f,
        modifier = modifier.height(14.dp),
        colors = SliderDefaults.colors(
            activeTrackColor = Color.Transparent,
            inactiveTrackColor = Color.Transparent,
            thumbColor = Color.Transparent
        ),
        thumb = {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFB2FEFA),
                                Color(0xFF0ED2F7)
                            )
                        ),
                        shape = CircleShape
                    )
            )
        },
        track = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color.White)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFB2FEFA),
                                    Color(0xFF0ED2F7)
                                )
                            )
                        )
                )
            }
        }
    )
}

@Composable
fun ItemSoundDetail(
    data: Sound,
    position: Int,
    isSelected: Boolean = true,
    onClick: () -> Unit = {},
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {

    val backgroundColor = Color(
        stringResource(id = UtilsKotlin().getColorByPos(position)).toColorInt()
    )

    Column(
        modifier = modifier
            .clickable { onClick() }
            .wrapContentSize()
            .padding(end = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(data.thumb)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = data.name,
            style = if (isSelected) {
                TextStyle(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFB2FEFA),
                            Color(0xFF0ED2F7)
                        )
                    )
                )
            } else {
                TextStyle(
                    color = Color.White
                )
            },
            fontSize = 11.sp,
            maxLines = 3,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily(Font(R.font.inter_bold)),
            modifier = Modifier.width(60.dp)
        )
    }
}

