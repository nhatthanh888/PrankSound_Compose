@file:Suppress("DEPRECATION")

package com.example.pranksound_compose.component.custom_sound

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pranksound.data.dto.prank.CustomSound
import com.example.pranksound.utils.UtilsKotlin
import com.example.pranksound_compose.R
import com.example.pranksound_compose.component.dialog.DialogPrankSettings
import com.example.pranksound_compose.component.dialog.ShareDialog
import com.example.pranksound_compose.component.toast.showAddFavoriteToastCompose
import com.example.pranksound_compose.component.toast.showDeleteFavoriteToastCompose
import com.example.pranksound_compose.ui.theme.MainColor
import com.example.pranksound_compose.ui.theme.White
import com.example.pranksoundalpha.viewmodel.DetailSoundViewModel
import com.example.pranksoundalpha.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import androidx.core.graphics.toColorInt

@Composable
fun CustomSoundScreen(
    navController: NavController,
    onAddSong: () -> Unit = {},
    onPause: () -> Unit = {},
    volume: Float = 0.5f,
    onVolumeChange: (Float) -> Unit = {}
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mainViewModel: MainViewModel = hiltViewModel()
    val detailViewModel: DetailSoundViewModel = hiltViewModel()

    val delaySound by detailViewModel.delay.collectAsStateWithLifecycle(
        initialValue = 0 to false
    )

    val isLoop by detailViewModel.isLoop.collectAsState()
    var selectedIndex by remember { mutableIntStateOf(-1) }

    /* ================= STATE ================= */

    var showDialog by remember { mutableStateOf(false) }
    var showDialogShare by remember { mutableStateOf(false) }

    // countdown state
    var timerSeconds by remember { mutableIntStateOf(0) }
    var isCounting by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(false) }
    var isAudioSetup by remember { mutableStateOf(false) }


    val customSoundList by mainViewModel.customSoundList.collectAsState()
    val selectedCustomSound by mainViewModel.selectedCustomSound.collectAsState()

    //var volumeObserver: VolumeContentObserver by remember { mutableStateOf(false) }

    var sound: CustomSound? by remember { mutableStateOf(null) }

    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentSound by remember { mutableStateOf<CustomSound?>(null) }
    var nextSound by remember { mutableStateOf(false) }

    val audioManager = remember {
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    val maxVolume = remember {
        audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    }

    var volumeState by remember { mutableFloatStateOf(0f) }
    var progressVolume by remember { mutableStateOf(0) }
    var firstPlay by remember { mutableStateOf(true) }

    /* ================= FORMAT TIME ================= */

    fun playSound(sound: CustomSound) {
        Log.e("Audio_Loop", isLoop.toString())
        try {
            // Nếu đang phát bài khác → stop & release
            mediaPlayer?.let {
                it.stop()
                it.reset()
                it.release()
            }

            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, Uri.parse(sound.uri))
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
        val selectedSound = selectedCustomSound ?: return

        Log.e("Audio_Current", selectedSound.id.toString() )

        // Nếu chưa có MediaPlayer hoặc đổi bài
        if (mediaPlayer == null || currentSound?.uri != selectedSound.uri) {
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

    LaunchedEffect(customSoundList) {
        if (
            customSoundList.isNotEmpty() &&
            !isAudioSetup
        ) {
            val sound = customSoundList[0]

            mainViewModel.selectCustomSound(sound)
//            playSound(sound)
//            isPlaying = true

            selectedIndex = 1
            Log.e("SetUp_Audio", "SetUp_Audio")

            isAudioSetup = true
        }
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
    LaunchedEffect(delaySound.first) {
        if (delaySound.first > 0) {
            countdown(delaySound.first)
        } else {
            isCounting = false
            timerSeconds = 0
        }
    }

    LaunchedEffect(isCounting) {
        if (!isCounting) return@LaunchedEffect

        while (timerSeconds > 0) {
            delay(1000)
            timerSeconds--
        }

        // Khi countdown xong
        isCounting = false
        timerSeconds = 0

        // play sound sau countdown
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

    val ringtonePickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri =
                    result.data?.getParcelableExtra<Uri>(
                        RingtoneManager.EXTRA_RINGTONE_PICKED_URI
                    )

                uri?.let {
                    lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                        val id = mainViewModel.getMaxCustomSoundID()
                        val title =
                            "${context.getString(R.string.add_custom)} ${id + 1}"

                        val customSound =
                            CustomSound(
                                uri = it.toString(),
                                title = title
                            )

                        mainViewModel.insertCustomSound(customSound)

                        delay(100)

                        val currentList =
                            mainViewModel.customSoundList.value
                        if (currentList.isNotEmpty()) {
                            mainViewModel.selectCustomSound(currentList[0])
                        }
                    }
                }
            }
        }

    fun openRingtonePicker() {
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
            putExtra(
                RingtoneManager.EXTRA_RINGTONE_TYPE,
                RingtoneManager.TYPE_RINGTONE
            )
            putExtra(
                RingtoneManager.EXTRA_RINGTONE_TITLE,
                context.getString(R.string.add_song)
            )
            putExtra(
                RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT,
                true
            )
            putExtra(
                RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT,
                false
            )
        }
        ringtonePickerLauncher.launch(intent)
    }

    /* ================= INIT ================= */

    LaunchedEffect(Unit) {
        mainViewModel.getAllCustomSound()
        val currentVolume =
            audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

        volumeState = (currentVolume.toFloat() / maxVolume * 100f)
    }

    /* ================= DIALOG ================= */

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

    if (showDialogShare) {
        ShareDialog(
            onDismiss = { showDialogShare = false }
        )
    }

    /* ================= UI ================= */

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        Column(modifier = Modifier.fillMaxSize()) {

            /* -------- Toolbar -------- */

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_left),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { navController.popBackStack() }
                )

                Text(
                    text = stringResource(id = R.string.add_custom),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.inter_bold)),
                    modifier = Modifier.padding(start = 16.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                if (customSoundList.isNotEmpty()) {
                    selectedCustomSound?.isFavorite?.let {
                        Image(
                            painter = painterResource(
                                if (it) R.drawable.ic_heart_selected
                                else R.drawable.ic_favorite
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    isFavorite = !isFavorite
                                    Log.e("isFavorite_CustomSound", isFavorite.toString())
                                    selectedCustomSound?.let { currentSound ->
                                        detailViewModel.updateFavoriteCustomSound(currentSound)
                                        mainViewModel.selectCustomSound(currentSound.copy(isFavorite = !currentSound.isFavorite))
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
            }

            /* -------- Logo -------- */
            var bgColor = MainColor
            selectedCustomSound?.id?.let { id ->
                bgColor = Color(
                    stringResource(id = UtilsKotlin().getColorByPos(id + 1)).toColorInt()
                )
            }

            Card(
                modifier = Modifier
                    .size(160.dp)
                    .align(Alignment.CenterHorizontally),
                shape = CircleShape,
                colors = CardDefaults.cardColors(containerColor = bgColor),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_add_cutom_funny),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            /* -------- Add Song -------- */


            if (customSoundList.isNotEmpty()) {
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
            } else {
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(Color(0xFFB2FEFA), Color(0xFF0ED2F7))
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable(onClick = { openRingtonePicker() })
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        painter = painterResource(id = R.drawable.ic_add_circle),
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = stringResource(id = R.string.add_song),
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.outfit_medium)),
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            /* -------- Settings -------- */

            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                /* ====== SETTINGS CONTENT ====== */
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .background(
                            Color(0xFF80626262),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp)
                ) {

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
                            isEnable = customSoundList.isNotEmpty(),
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

                /* ====== OVERLAY CHẶN TƯƠNG TÁC ====== */
                if (customSoundList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Color.Black.copy(alpha = 0.5f)
                            )
                            .pointerInput(Unit) {}
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            /* -------- Custom Sound List -------- */

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(Color(0xFF80626262), RoundedCornerShape(16.dp))
            ) {
                if (customSoundList.isNotEmpty()) {
                    if (selectedCustomSound?.id==0){
                        selectedIndex = 1
                    }
                    AddMoreCustomSound(
                        items = customSoundList,
                        selectedIndex = selectedIndex,
                        onSelectedIndexChange = { selectedIndex = it },
                        onAddMoreClick = { openRingtonePicker() },
                        onItemClick = { soundSelected ->
                            Log.e("ItemSelected", soundSelected.id.toString() )
                            mainViewModel.selectCustomSound(soundSelected)
                            playSound(soundSelected)
                            isPlaying = false
                        }
                    )
                }
            }
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
fun AddMoreCustomSound(
    items: List<CustomSound>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    onAddMoreClick: () -> Unit,
    onItemClick: (CustomSound) -> Unit = {}
) {

    val fullItems = remember(items) {
        listOf(
            CustomSound(
                id = -1,
                uri = "",
                title = "Add more",
            )
        ) + items
    }

    LazyRow(
        modifier = Modifier.padding(16.dp)
    ) {
        itemsIndexed(fullItems) { index, item ->
            val isSelected = index == selectedIndex
            Log.e("CustomSound_SelectedIndex", selectedIndex.toString())
            ItemAddCustomSound(
                data = item,
                position = index,
                isSelected = isSelected,
                onClick = {
                    if (item.id == -1) {
                        onAddMoreClick()
                    } else {
                        onSelectedIndexChange(
                            index
                        )
                        onItemClick(item)
                    }
                }
            )
        }
    }
}

@Composable
fun ItemAddCustomSound(
    data: CustomSound,
    position: Int,
    isSelected: Boolean = true,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val backgroundColor = Color(
        stringResource(id = UtilsKotlin().getColorByPos(position)).toColorInt()
    )
    val iconRes =
        if (data.id == -1) R.drawable.ic_add_more_custom else R.drawable.ic_add_cutom_funny
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
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = data.title,
            color = Color.White,
            fontSize = 11.sp,
            maxLines = 3,
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
            textAlign = TextAlign.Center,
            fontFamily = FontFamily(Font(R.font.inter_bold)),
            modifier = Modifier.width(60.dp)
        )
    }
}

@Preview
@Composable
fun PreviewCustomSoundScreen() {
    CustomSoundScreen(rememberNavController())
}