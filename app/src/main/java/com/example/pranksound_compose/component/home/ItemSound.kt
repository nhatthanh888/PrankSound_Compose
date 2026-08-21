package com.example.pranksound_compose.component.home

import android.R.attr.shape
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.pranksound.data.dto.prank.SoundFolder
import com.example.pranksound_compose.R
import com.example.pranksound_compose.ui.theme.ItemSoundColor
import com.example.pranksound_compose.ui.theme.MainColor
import com.example.pranksound_compose.ui.theme.White

@Composable
fun ItemSound(
    data: SoundFolder,
    isSelected: Boolean = true,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) {
            onClick()
        }
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .border(
                    width = if (isSelected) 2.dp else 0.dp,
                    color = if (isSelected) MainColor else Color.Transparent,
                    shape = RoundedCornerShape(16.dp)
                )
                .background(
                    color = ItemSoundColor,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(vertical = 10.dp, horizontal = 10.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(data.thumb)
                    .crossfade(true)
                    .build(),
                contentScale = ContentScale.Fit,
                contentDescription = null,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = data.name,
            textAlign = TextAlign.Center,
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
            fontFamily = FontFamily(Font(R.font.inter_bold)),
            fontSize = 14.sp
        )
    }
}

@Composable
@Preview
fun PreviewItemSound() {
    //ItemSound()
}