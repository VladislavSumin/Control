package ru.falseteam.control.server.domain.videoencoder

import java.nio.file.Path

interface VideoEncodeInteractor {
    suspend fun encode(input: Path, output: Path)

    suspend fun getDuration(file: Path): Double
}
