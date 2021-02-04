package ru.falseteam.control.ui.screens.recors

import android.net.Uri

data class RecordUiModel(
    val id: Long,
    val name: String?,
    val cameraName: String?,
    val date: String,
    val keepForever: Boolean,
    val uri: Uri,
    val previewUri: Uri,
)