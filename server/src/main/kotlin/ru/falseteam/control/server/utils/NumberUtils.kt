package ru.falseteam.control.server.utils

fun Long.toBoolean() = this != 0L
fun Boolean.toLong() = if (this) 1L else 0L