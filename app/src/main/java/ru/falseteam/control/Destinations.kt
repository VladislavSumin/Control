package ru.falseteam.control

@Suppress("FunctionName")
object Destinations {
    const val CamsScreen = "cams"
    const val AddCameraScreen = "add_camera"
    const val LivestreamScreen = "livestream/{id}"
    fun LivestreamScreen(id: Long) = "livestream/$id"
}