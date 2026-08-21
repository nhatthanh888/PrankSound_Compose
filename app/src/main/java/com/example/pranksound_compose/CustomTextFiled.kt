package com.example.pranksound_compose

import android.R.attr.text
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CustomTextField(
    text: String="",
    onValueChange: (String) -> Unit = {},
    hint: String = "00"
) {
    BasicTextField(
        value = text,
        onValueChange = onValueChange,
        textStyle = TextStyle(
            textAlign = TextAlign.Center,
            color = Color.Black
        ),
        modifier = Modifier
            .background(
                color = Color(0xFF33E2E1E0),
                shape = RoundedCornerShape(8.dp)
            )
            .size(width = 40.dp, height = 36.dp),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (text.isEmpty()) {
                    Text(
                        text = hint,
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
                innerTextField()
            }
        }
    )
}

@Preview
@Composable
fun PreviewCustomTextField() {
    CustomTextField("")
}