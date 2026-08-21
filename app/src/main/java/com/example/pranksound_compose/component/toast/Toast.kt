package com.example.pranksound_compose.component.toast

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.pranksound_compose.R
import com.example.pranksound_compose.utils.ContextExt.findActivity

fun showAddFavoriteToastCompose(context: Context) {

    val activity = context.findActivity()
        ?: return // tránh crash nếu context không phải Activity

    val toast = Toast(activity)

    val composeView = ComposeView(activity).apply {
        
        setViewTreeLifecycleOwner(activity)
        setViewTreeViewModelStoreOwner(activity)
        setViewTreeSavedStateRegistryOwner(activity)

        setContent {
            AddFavoriteToastContent()
        }
    }

    toast.view = composeView
    toast.duration = Toast.LENGTH_SHORT
    toast.setGravity(
        Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL,
        0,
        100
    )
    toast.show()
}
@Composable
fun AddFavoriteToastContent(

) {
    Box(
        modifier = Modifier
            .background(
                color = Color.White.copy(alpha = 0.5f),
                shape = RoundedCornerShape(20.5.dp)
            )
            .padding(
                horizontal = 16.dp,
                vertical = 10.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.add_to_favorite),
            color = Color.White,
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(R.font.inter_regular))
        )
    }
}


fun showDeleteFavoriteToastCompose(context: Context) {

    val activity = context.findActivity()
        ?: return // tránh crash nếu context không phải Activity

    val toast = Toast(activity)

    val composeView = ComposeView(activity).apply {

        setViewTreeLifecycleOwner(activity)
        setViewTreeViewModelStoreOwner(activity)
        setViewTreeSavedStateRegistryOwner(activity)

        setContent {
            DeleteFavoriteToastContent()
        }
    }

    toast.view = composeView
    toast.duration = Toast.LENGTH_SHORT
    toast.setGravity(
        Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL,
        0,
        100
    )
    toast.show()
}
@Composable
fun DeleteFavoriteToastContent(

) {
    Box(
        modifier = Modifier
            .background(
                color = Color.White.copy(alpha = 0.5f),
                shape = RoundedCornerShape(20.5.dp)
            )
            .padding(
                horizontal = 16.dp,
                vertical = 10.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.delete_to_favorite),
            color = Color.White,
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(R.font.inter_regular))
        )
    }
}

