package ru.falseteam.control.ui.screens.recors

data class RecordCamsFilterState(val cams: List<Entity>) {
    data class Entity(
        val id: Long,
        val name: String,
        val isChecked: Boolean = false
    )
}