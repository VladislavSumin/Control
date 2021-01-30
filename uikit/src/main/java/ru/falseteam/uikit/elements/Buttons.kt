package ru.falseteam.uikit.elements

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.CombinedModifier
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun UiKitPrimaryButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    showProgress: Boolean = false,
    enabled: Boolean = true,
) {
    ButtonInternal(
        onClick,
        text,
        modifier,
        showProgress,
        enabled,
        36.dp,
        16.dp,
        MaterialTheme.shapes.small,
    )
}

@Composable
fun UiKitPrimaryAccentButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    showProgress: Boolean = false,
    enabled: Boolean = true,
) {
    ButtonInternal(
        onClick,
        text,
        modifier,
        showProgress,
        enabled,
        52.dp,
        24.dp,
        MaterialTheme.shapes.medium,
    )
}

@Composable
private fun ButtonInternal(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier,
    showProgress: Boolean,
    enabledGlobal: Boolean,
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
        enabled = if (showProgress) false else enabledGlobal,
        shape = shape,

        // No change button colors to disabled if status in progress
        colors = object : ButtonColors {
            private val colors = ButtonDefaults.buttonColors()

            @Composable
            override fun backgroundColor(enabled: Boolean): State<Color> {
                return colors.backgroundColor(enabled = enabledGlobal)
            }

            @Composable
            override fun contentColor(enabled: Boolean): State<Color> {
                return colors.contentColor(enabled = enabledGlobal)
            }
        }

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
