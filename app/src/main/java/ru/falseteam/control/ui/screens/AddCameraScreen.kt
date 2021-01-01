package ru.falseteam.control.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.falseteam.control.ui.PrimaryAccentButton
import ru.falseteam.control.ui.PrimaryButton

@Composable
@Preview(showBackground = true)
fun AddCameraScreen() {
    val name = savedInstanceState { "" }
    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp, 16.dp)
    ) {
        TextField(
            value = name.value,
            onValueChange = { name.value = it },
            label = { Text(text = "Camera name") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 4.dp)

        )
        TextField(
            value = name.value,
            onValueChange = { name.value = it },
            label = { Text(text = "Address") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 4.dp)
        )
        TextField(
            value = name.value,
            onValueChange = { name.value = it },
            label = { Text(text = "Port") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 4.dp)

        )
        Spacer(modifier = Modifier.weight(1f))
        PrimaryAccentButton(
            onClick = { /*TODO*/ },
            text = "Add camera",
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 4.dp)

        )
    }
}