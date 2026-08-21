package com.example.pranksound_compose.component.wellcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pranksound_compose.R
import com.example.pranksound_compose.ui.theme.Black
import com.example.pranksound_compose.ui.theme.White

@Composable
fun WelcomeScreen(
    navController: NavController,
    onGetStartedClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Image(
            painter = painterResource(R.drawable.welcome),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // ---- Prank Sound (Top) ----
        Text(
            text = stringResource(id = R.string.prank_sound),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 32.dp),
            fontFamily = FontFamily(Font(R.font.electromagnetic_lungs)),
            fontSize = 26.sp,
            style = TextStyle(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFB2FEFA),
                        Color(0xFF0ED2F7)
                    )
                )
            ),
            textAlign = TextAlign.Center
        )

        // ---- Title + Content (Center - Bottom) ----
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = stringResource(id = R.string.welcom_to_prank_sound),
                fontFamily = FontFamily(Font(R.font.electromagnetic_lungs)),
                fontSize = 22.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.explore_hilarious_sounds_for_endless_fun),
                fontFamily = FontFamily(Font(R.font.outfit_regular)),
                fontSize = 12.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(22.dp))

            Button(
                onClick = {
                    navController.navigate("main_screen") {
                        popUpTo("welcome") { inclusive = true }
                    }
                },
                modifier = Modifier
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
                contentPadding = PaddingValues(vertical = 12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
//                elevation = ButtonDefaults.buttonElevation(
//                    defaultElevation = 0.dp,
//                    pressedElevation = 0.dp
//                )
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.get_started),
                        modifier = Modifier.align(alignment = Alignment.Center),
                        color = Black,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.outfit_medium))
                    )
                    Image(
                        painter = painterResource(R.drawable.ic_arrow_btn),
                        contentDescription = null,
                        Modifier.align(alignment = Alignment.CenterEnd)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewWelcomeScreen() {
    //WelcomeScreen()
}