package com.example.pranksound_compose.component.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pranksound_compose.R

@Composable
fun SplashScreen(
    navController: NavController
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 3000,
                easing = LinearEasing
            )
        )

        // Sau 3s chuyển màn
        navController.navigate("welcome") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.bg_splash),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(id = R.drawable.icon_app),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(id = R.string.prank_sound),
                fontSize = 26.sp,
                fontFamily = FontFamily(Font(R.font.electromagnetic_lungs)),
                style = TextStyle(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFB2FEFA),
                            Color(0xFF0ED2F7)
                        )
                    )
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(id = R.string.this_action_can_contain_ads),
                modifier = Modifier.padding(horizontal = 32.dp),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(R.font.oufit)),
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.color_text)
            )

            Spacer(modifier = Modifier.height(16.dp))

            GradientProgressBar(
                progress = progress.value,
                modifier = Modifier.padding(horizontal = 70.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun GradientProgressBar(
    progress: Float, // 0f..1f
    modifier: Modifier = Modifier,
    height: Dp = 5.dp,
    cornerRadius: Dp = 8.dp
) {
    val backgroundColor = Color(0xFF192C2F)
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFB2FEFA),
            Color(0xFF0ED2F7)
        )
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val corner = cornerRadius.toPx()

        // Background
        drawRoundRect(
            color = backgroundColor,
            size = size,
            cornerRadius = CornerRadius(corner, corner)
        )

        // Progress
        drawRoundRect(
            brush = gradientBrush,
            size = Size(
                width = size.width * progress.coerceIn(0f, 1f),
                height = size.height
            ),
            cornerRadius = CornerRadius(corner, corner)
        )
    }
}

@Preview
@Composable
fun PreviewSplashScreen() {

}