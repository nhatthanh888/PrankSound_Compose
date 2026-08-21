package com.example.pranksound_compose.component.dialog

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pranksound_compose.R
import com.example.pranksound_compose.ui.theme.Black
import com.example.pranksound_compose.ui.theme.White
import com.example.pranksoundalpha.viewmodel.DetailSoundViewModel

@Composable
fun DialogPrankSettings(
    viewModel: DetailSoundViewModel,
    modifier: Modifier = Modifier,
    onCancel: () -> Unit = {},
    onOk: (String) -> Unit = {},
    onPauseSound: () -> Unit = {}
) {

    // ===== VM STATE =====
    val isLoop by viewModel.isLoop.collectAsState()
    val delayState by viewModel.delay.collectAsState(
        initial = Pair(0, false)
    )

    // ===== UI STATE =====
    var isCustomDelay by remember { mutableStateOf(delayState.second) }
    var selectedDelay by remember { mutableIntStateOf(delayState.first) }
    var isTimeValid by remember { mutableStateOf(true) }

    var hours by remember { mutableStateOf("") }
    var minutes by remember { mutableStateOf("") }
    var seconds by remember { mutableStateOf("") }

    LaunchedEffect(hours, minutes, seconds, isCustomDelay) {
        if (isCustomDelay) {
            isTimeValid = isCustomDelayValid(hours, minutes, seconds)
        } else {
            isTimeValid = true
        }
    }

    // ===== Sync VM -> UI =====
    LaunchedEffect(delayState) {
        selectedDelay = delayState.first
        isCustomDelay = delayState.second

        if (isCustomDelay) {
            val d = delayState.first
            hours = (d / 3600).takeIf { it > 0 }?.toString() ?: ""
            minutes = ((d % 3600) / 60).takeIf { it > 0 }?.toString() ?: ""
            seconds = (d % 60).takeIf { it > 0 }?.toString() ?: ""
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFF5C5858),
                shape = RoundedCornerShape(16.dp)
            )
    ) {

        // ===== TITLE =====
        Text(
            text = stringResource(R.string.prank_setting),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
            textAlign = TextAlign.Center,
            color = White,
            fontSize = 20.sp,
            fontFamily = FontFamily(Font(R.font.outfit_medium))
        )

        // ===== PRESET HEADER =====
        HeaderRadio(
            title = stringResource(R.string.set_delay),
            selected = !isCustomDelay
        ) {
            isCustomDelay = false
            viewModel.setDelay(0)
        }

        // ===== PRESET BUTTONS =====
        PresetDelayRow(
            selectedDelay = selectedDelay,
            enabled = !isCustomDelay
        ) { delay ->
            selectedDelay = delay
            viewModel.setDelay(delay)
        }

        // ===== CUSTOM HEADER =====
        HeaderRadio(
            title = stringResource(R.string.custom_delay),
            selected = isCustomDelay
        ) {
            isCustomDelay = true
        }

        // ===== CUSTOM INPUT =====
        CustomDelayInput(
            enabled = isCustomDelay,
            hours = hours,
            minutes = minutes,
            seconds = seconds,
            isError = !isTimeValid,
            onHoursChange = { hours = it },
            onMinutesChange = { minutes = it },
            onSecondsChange = { seconds = it }
        )

        // ===== LOOP =====
        LoopRow(
            isLoop = isLoop,
            onToggle = { viewModel.setLoop() }
        )

        // ===== ACTIONS =====
        ActionRow(
            onCancel = onCancel,
            onOk = {
                if (isCustomDelay && !isTimeValid) return@ActionRow

                if (isCustomDelay) {
                    val h = hours.toIntOrNull() ?: 0
                    val m = minutes.toIntOrNull() ?: 0
                    val s = seconds.toIntOrNull() ?: 0

                    val total = h * 3600 + m * 60 + s
                    viewModel.setDelay(total, true)
                    onOk(total.toString())
                    Log.e("Custom_Delay", total.toString())
                } else {
                    viewModel.setDelay(selectedDelay)
                    onOk(if (selectedDelay == 0) "OFF" else selectedDelay.toString())
                    Log.e("Set_Delay", selectedDelay.toString())
                }
                onPauseSound()
            }
        )
    }
}

@Composable
private fun HeaderRadio(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            modifier = Modifier.weight(1f),
            color = White,
            fontSize = 12.sp,
            fontFamily = FontFamily(Font(R.font.outfit_medium))
        )
        Image(
            painter = painterResource(
                if (selected) R.drawable.ic_radio_selected
                else R.drawable.ic_radio
            ),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun PresetDelayRow(
    selectedDelay: Int,
    enabled: Boolean,
    onSelect: (Int) -> Unit
) {
    val presets = listOf(
        0 to "Off",
        5 to "05s",
        15 to "15s",
        30 to "30s",
        60 to "60s",
        120 to "2m"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        presets.forEach { (value, label) ->
            val isSelected = enabled && value == selectedDelay

            Text(
                text = label,
                modifier = Modifier
                    .alpha(if (enabled) 1f else 0.4f)
                    .background(
                        if (isSelected)
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFFB2FEFA),
                                    Color(0xFF0ED2F7)
                                )
                            )
                        else SolidColor(Color(0xFF33E2E1E0)),
                        RoundedCornerShape(8.dp)
                    )
                    .clickable(enabled) {
                        onSelect(value)
                    }
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                color = if (isSelected) Black else White,
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(R.font.outfit_semi_bold))
            )
        }
    }
}

@Composable
private fun CustomDelayInput(
    enabled: Boolean,
    hours: String,
    minutes: String,
    seconds: String,
    isError: Boolean,
    onHoursChange: (String) -> Unit,
    onMinutesChange: (String) -> Unit,
    onSecondsChange: (String) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TimeInput(
                value = hours,
                isEnable = enabled,
                isError = isError,
                onValueChange = onHoursChange,
                label = "hrs",
                maxValue = 23
            )

            Spacer(Modifier.width(24.dp))

            TimeInput(
                value = minutes,
                isEnable = enabled,
                isError = isError,
                onValueChange = onMinutesChange,
                label = "min",
                maxValue = 59
            )

            Spacer(Modifier.width(24.dp))

            TimeInput(
                value = seconds,
                isEnable = enabled,
                isError = isError,
                onValueChange = onSecondsChange,
                label = "sec",
                maxValue = 59
            )
        }

        if (enabled && isError) {
            Text(
                text = stringResource(R.string.notification_invalid),
                color = Color(0xFFFF6B6B),
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp)
            )
        }
    }
}

@Composable
private fun LoopRow(
    isLoop: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.loop),
                color = White,
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.outfit_medium))
            )
            Text(
                text = stringResource(R.string.play_sound_in_repeating_mode),
                color = Color(0xFFB4B4B4),
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(R.font.outfit_medium))
            )
        }
        Image(
            modifier = Modifier.clickable { onToggle() },
            painter = painterResource(
                if (isLoop) R.drawable.ic_enable
                else R.drawable.ic_unenable
            ),
            contentDescription = null
        )
    }
}

@Composable
private fun ActionRow(
    onCancel: () -> Unit,
    onOk: () -> Unit
) {
    Row(
        modifier = Modifier.padding(16.dp)
    ) {
        Button(
            onClick = onCancel,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            modifier = Modifier
                .weight(1f)
                .background(
                    color = Color(0xFF33E2E1E0),
                    shape = RoundedCornerShape(8.dp)
                )
                .height(42.dp)
        ) {
            Text(
                text = stringResource(R.string.cancel),
                textAlign = TextAlign.Center,
                color = Color.White,
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.outfit_medium))
            )
        }

        Spacer(Modifier.width(16.dp))

        Button(
            onClick = onOk,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            modifier = Modifier
                .weight(1f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFB2FEFA),
                            Color(0xFF0ED2F7)
                        )
                    ),
                    shape = RoundedCornerShape(12)
                )
                .height(42.dp)
        ) {
            Text(
                text = stringResource(R.string.okay),
                textAlign = TextAlign.Center,
                color = Color(0xFF1D1B1B),
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.outfit_medium))
            )
        }
    }
}


@Composable
private fun TimeInput(
    value: String = "",
    isEnable: Boolean = true,
    isError: Boolean = false,
    onValueChange: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    label: String,
    maxValue: Int
) {
    val background = when {
        isError -> Modifier.background(
            color = Color(0x33FF6B6B),
            shape = RoundedCornerShape(8.dp)
        )

        isEnable -> Modifier.background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFB2FEFA).copy(alpha = 0.2f),
                    Color(0xFF0ED2F7).copy(alpha = 0.2f)
                )
            ),
            shape = RoundedCornerShape(8.dp)
        )

        else -> Modifier.background(
            color = Color(0xFF33E2E1E0),
            shape = RoundedCornerShape(8.dp)
        )
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        BasicTextField(
            value = value,
            enabled = isEnable,
            onValueChange = { newValue ->
                if (newValue.isEmpty()) {
                    onValueChange("")
                    return@BasicTextField
                }

                if (newValue.all { it.isDigit() }) {
                    val number = newValue.toIntOrNull()
                    if (number != null && number <= maxValue) {
                        onValueChange(newValue)
                    }
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            singleLine = true,
            cursorBrush = Brush.verticalGradient(
                listOf(
                    Color(0xFFB2FEFA),
                    Color(0xFF0ED2F7)
                )
            ),
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.outfit_semi_bold)),
                brush = if (isError) {
                    SolidColor(Color(0xFFFF6B6B))
                } else {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFB2FEFA),
                            Color(0xFF0ED2F7)
                        )
                    )
                }
            ),
            modifier = modifier
                .then(background)
                .border(
                    width = if (isError) 1.dp else 0.dp,
                    color = if (isError) Color(0xFFFF6B6B) else Color.Transparent,
                    shape = RoundedCornerShape(8.dp)
                )
                .size(width = 40.dp, height = 36.dp),
            decorationBox = { inner ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = "00",
                            color = Color(0xFFACACAC),
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(R.font.outfit_medium))
                        )
                    }
                    inner()
                }
            }
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = label,
            color = Color(0xFFACACAC),
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(R.font.outfit_medium))
        )
    }
}
//@Composable
//private fun TimeInput(
//    value: String = "",
//    isEnable: Boolean = true,
//    isError: Boolean = false,
//    onValueChange: (String) -> Unit = {},
//    modifier: Modifier = Modifier,
//    label: String
//) {
//
//    var background: Modifier
//
//    if (isError) {
//        background = Modifier.background(
//            color = Color(0x33FF6B6B),
//            shape = RoundedCornerShape(8.dp)
//        )
//    } else {
//        if (isEnable) {
//            background = Modifier.background(
//                brush = Brush.verticalGradient(
//                    colors = listOf(
//                        Color(0xFFB2FEFA).copy(alpha = 0.2f),
//                        Color(0xFF0ED2F7).copy(alpha = 0.2f)
//                    )
//                ),
//                shape = RoundedCornerShape(8.dp)
//            )
//        } else {
//            background = Modifier.background(
//                color = Color(0xFF33E2E1E0),
//                shape = RoundedCornerShape(8.dp)
//            )
//        }
//    }
//    Row(verticalAlignment = Alignment.CenterVertically) {
//        BasicTextField(
//            value = value,
//            enabled = isEnable,
//            onValueChange = { newValue ->
//                if (newValue.all { it.isDigit() }) {
//                    onValueChange(newValue)
//                }
//            },
//            keyboardOptions = KeyboardOptions(
//                keyboardType = KeyboardType.Number
//            ),
//            singleLine = true,
//            cursorBrush = Brush.verticalGradient(
//                colors = listOf(
//                    Color(0xFFB2FEFA),
//                    Color(0xFF0ED2F7)
//                )
//            ),
//            textStyle = TextStyle(
//                textAlign = TextAlign.Center,
//                fontSize = 14.sp,
//                fontFamily = FontFamily(Font(R.font.outfit_semi_bold)),
//                brush = if (isError) {
//                    SolidColor(Color(0xFFFF6B6B))
//                } else {
//                    Brush.verticalGradient(
//                        colors = listOf(
//                            Color(0xFFB2FEFA),
//                            Color(0xFF0ED2F7)
//                        )
//                    )
//                }
//            ),
//            modifier = Modifier
//                .then(background)
//                .border(
//                    width = if (isError) 1.dp else 0.dp,
//                    color = if (isError) Color(0xFFFF6B6B) else Color.Transparent,
//                    shape = RoundedCornerShape(8.dp)
//                )
//                .size(width = 40.dp, height = 36.dp),
//            decorationBox = { inner ->
//                Box(
//                    modifier = Modifier.fillMaxSize(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    if (value.isEmpty()) {
//                        Text(
//                            text = "00",
//                            color = Color(0xFFACACAC),
//                            fontSize = 14.sp,
//                            fontFamily = FontFamily(Font(R.font.outfit_medium))
//                        )
//                    }
//                    inner()
//                }
//            }
//        )
//
//        Spacer(Modifier.width(8.dp))
//
//        Text(
//            text = label,
//            color = Color(0xFFACACAC),
//            fontSize = 14.sp,
//            fontFamily = FontFamily(Font(R.font.outfit_medium))
//        )
//    }
//}

private fun isCustomDelayValid(
    hours: String,
    minutes: String,
    seconds: String
): Boolean {
    val h = hours.toIntOrNull() ?: 0
    val m = minutes.toIntOrNull() ?: 0
    val s = seconds.toIntOrNull() ?: 0

    val isHourValid = h in 1..23
    val isMinuteValid = m in 1..59
    val isSecondValid = s in 1..59

    return isHourValid || isMinuteValid || isSecondValid
}

@Preview
@Composable
fun PreviewDialogPrankSettings() {

}