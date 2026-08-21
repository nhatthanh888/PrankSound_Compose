package com.example.pranksound_compose.component.dialog

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pranksound_compose.R
import com.example.pranksound_compose.utils.shareToApp
import com.example.pranksound_compose.utils.shareToOtherApps

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareDialog(
    onDismiss: () -> Unit,
    context: Context = LocalContext.current
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.Transparent,
        dragHandle = null
    ) {
        ShareDialogContent(
            onFacebookClick = { context.shareToApp("com.facebook.katana") },
            onInstagramClick = { context.shareToApp("com.instagram.android") },
            onWhatsAppClick = { context.shareToApp("com.whatsapp") },
            onMessengerClick = { context.shareToApp("com.facebook.orca") },
            onOtherClick = { context.shareToOtherApps() }
        )
    }
}

@Composable
private fun ShareDialogContent(
    onFacebookClick: () -> Unit,
    onInstagramClick: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onMessengerClick: () -> Unit,
    onOtherClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp
                )
            )
            .padding(bottom = 16.dp)
    ) {

        Text(
            text = stringResource(id = R.string.share_to),
            fontSize = 16.sp,
            color = Color(0xFF1B1C20),
            fontFamily = FontFamily(Font(R.font.inter_bold)),
            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            ShareItem(
                icon = R.drawable.ic_facebook,
                text = "Facebook",
                onClick = onFacebookClick
            )

            ShareItem(
                icon = R.drawable.ic_instagram,
                text = "Instagram",
                onClick = onInstagramClick
            )

            ShareItem(
                icon = R.drawable.ic_whatsapp,
                text = "WhatsApp",
                onClick = onWhatsAppClick
            )

            ShareItem(
                icon = R.drawable.ic_messenger,
                text = "Messenger",
                onClick = onMessengerClick
            )

            ShareItem(
                icon = R.drawable.ic_other,
                text = "Other",
                onClick = onOtherClick
            )
        }
    }
}

@Composable
private fun ShareItem(
    @DrawableRes icon: Int,
    text: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = text,
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = text,
            fontSize = 10.sp,
            color = Color(0xFF1B1C20),
            fontFamily = FontFamily(Font(R.font.outfit_medium))
        )
    }
}

@Composable
fun Painter.toBrush(): Brush {
    return Brush.verticalGradient(
        colors = listOf(
            Color.Transparent,
            Color.Transparent
        )
    )
}