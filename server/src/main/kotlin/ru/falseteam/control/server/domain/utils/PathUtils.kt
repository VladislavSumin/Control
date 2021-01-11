package ru.falseteam.control.server.domain.utils

import org.apache.commons.lang3.SystemUtils
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

object PathUtils {

    fun findInPath(name: String): File? {
        val overriddenName = if (SystemUtils.IS_OS_WINDOWS) "$name.exe" else name
        return System.getenv("PATH").split(File.pathSeparator)
                .asSequence()
                .map { File(it) }
                .map { it.resolve(overriddenName) }
                .filter { it.exists() }
                .firstOrNull()
    }

    fun getPathOfProgram(name: String): String? {
        val command = if (SystemUtils.IS_OS_WINDOWS) "where $name" else "which $name"
        val process = Runtime.getRuntime().exec(command)
        val br = BufferedReader(InputStreamReader(process.inputStream))
        val s = br.readLine()
        br.close()
        return s
    }

}