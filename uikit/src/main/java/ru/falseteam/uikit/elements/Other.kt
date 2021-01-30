package ru.falseteam.uikit.elements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun UikitCheckBoxListItem(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    ListItemInternal(
        text = text,
        checked = checked,
        onCheckedChange = onCheckedChange
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun UikitSwitchListItem(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    ListItemInternal(
        text = text,
        checked = checked,
        onCheckedChange = onCheckedChange
    ) {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun ListItemInternal(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    block: @Composable () -> Unit,
) {
    Box(
        Modifier.clickable { onCheckedChange(!checked) }
    ) {
        Row(
            Modifier.padding(16.dp, 8.dp)
        ) {
            Text(
                text = text,
                modifier = Modifier
                    .weight(1f)
                    .padding(0.dp, 0.dp, 8.dp, 0.dp)
            )
            block()
        }
    }
}
