package com.example.pranksound_compose.component.favorite

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.pranksound.data.dto.prank.FavoriteSound
import com.example.pranksound.utils.UtilsKotlin
import com.example.pranksound_compose.R
import com.example.pranksound_compose.ui.theme.BGItemTrending
import com.example.pranksound_compose.ui.theme.MainColor

@Composable
fun FavoriteItem(
    data: FavoriteSound,
    position: Int,
    onItemClick: () -> Unit = {},
    onRemoteItem: () -> Unit = {}
) {
    val backgroundColor = Color(
        stringResource(id = UtilsKotlin().getColorByPos(position)).toColorInt()
    )
    Box(
        modifier = Modifier
            .clickable { onItemClick() }
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .background(
                color = BGItemTrending,
                shape = RoundedCornerShape(10.dp)
            )
    ) {


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // LEFT: Card Image
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.wrapContentSize(),
                colors = CardDefaults.cardColors(
                    containerColor = backgroundColor
                ),
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(data.thumb)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // CENTER: Text
            Text(
                text = data.name,
                color = Color.White,
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.outfit_medium)),
                modifier = Modifier.weight(1f)
            )

            // RIGHT: Pause icon
            Image(
                painter = if (data.isFavorite) painterResource(R.drawable.ic_heart_selected) else painterResource(
                    R.drawable.ic_heart
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 8.dp)
                    .clickable { onRemoteItem() }
            )
        }
    }
}

@Preview
@Composable
fun PreviewFavoriteItem() {
   // FavoriteItem()
}
