package com.example.pranksound_compose.component.setting

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pranksound_compose.R
import com.example.pranksound_compose.ui.theme.Black
import com.example.pranksound_compose.utils.AppUtils
import androidx.core.net.toUri
import com.example.pranksound_compose.utils.openInBrowser
import com.example.pranksound_compose.utils.shareToOtherApps

@Composable
fun SettingScreen(
    navController: NavController
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column {

            // ===== Toolbar =====
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 32.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "Close",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable {
                            // == ivClose click ==
                            navController.popBackStack()
                        }
                )

                Text(
                    text = stringResource(id = R.string.setting),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font(R.font.outfit_semi_bold)),
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // ===== Rate App =====
            SettingItem(
                icon = R.drawable.ic_rate_app,
                title = R.string.rate_app,
                onClick = {
                    // == layoutRateApp ==
                    AppUtils.linkToStore(context)
                }
            )

            // ===== Share App =====
            SettingItem(
                icon = R.drawable.ic_share_app,
                title = R.string.share_app,
                onClick = {
                    // == layoutShare ==
                    context.shareToOtherApps()
                }
            )

            // ===== Privacy Policy =====
            SettingItem(
                icon = R.drawable.ic_privacy_policy,
                title = R.string.privacy_policy,
                onClick = {
                    // == layoutPrivacy ==
                    val urlPolicy =
                        "https://sites.google.com/view/nowtech/privacy-policy"
                    urlPolicy.toUri().openInBrowser(context)
                }
            )
        }
    }
}

@Composable
fun SettingItem(
    @DrawableRes icon: Int,
    @StringRes title: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = stringResource(id = title),
            color = Color.White,
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(R.font.outfit_medium))
        )
    }
}


@Composable
@Preview
fun PreviewSettingScreen() {
    SettingItem(
        icon = R.drawable.ic_privacy_policy,
        title = R.string.privacy_policy,
        onClick = {}
    )
}