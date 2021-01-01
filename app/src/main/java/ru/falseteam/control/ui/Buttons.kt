package ru.falseteam.control.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.CombinedModifier
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    showProgress: Boolean = false,
    enabled: Boolean = true,
) {
    ButtonInternal(
        onClick, text, modifier, showProgress, enabled, 36.dp, 16.dp, MaterialTheme.shapes.small,

        )
}

@Composable
fun PrimaryAccentButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    showProgress: Boolean = false,
    enabled: Boolean = true,
) {
    ButtonInternal(
        onClick, text, modifier, showProgress, enabled, 52.dp, 24.dp, MaterialTheme.shapes.medium,
    )
}

@Composable
private fun ButtonInternal(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier,
    showProgress: Boolean,
    enabled: Boolean,
    btnHeight: Dp,
    progressHeight: Dp,
    shape: Shape,
) {
    Button(
        onClick = onClick,
        modifier = CombinedModifier(
            modifier,
            Modifier
                .height(btnHeight)
        ),
        enabled = enabled,
        shape = shape

    ) {
        if (showProgress) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.dp,
                modifier = Modifier
                    .width(progressHeight)
                    .height(progressHeight)
                    .align(Alignment.CenterVertically)
            )
        } else {
            Text(text = text)
        }
    }
}


