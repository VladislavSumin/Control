package ru.falseteam.control.server.utils

import org.apache.commons.lang3.SystemUtils
import java.io.BufferedReader
import java.io.InputStreamReader

object PathUtils {

    fun getPathOfProgram(name: String): String? {
        val command = if (SystemUtils.IS_OS_WINDOWS) "where $name" else "which $name"
        val process = Runtime.getRuntime().exec(command)
        val br = BufferedReader(InputStreamReader(process.inputStream))
        val s = br.readLine()
        br.close()
        return s
    }
}