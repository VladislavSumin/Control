package ru.falseteam.control.common

import java.text.CharacterIterator
import java.text.StringCharacterIterator

inline class ByteSize(val bytes: Long) {
    fun toHumanReadableBinString(): String {
        val absB = if (bytes == Long.MIN_VALUE) Long.MAX_VALUE else Math.abs(bytes)
        if (absB < 1024) {
            return "$bytes B"
        }
        var value = absB
        val ci: CharacterIterator = StringCharacterIterator("KMGTPE")
        var i = 40
        while (i >= 0 && absB > 0xfffccccccccccccL shr i) {
            value = value shr 10
            ci.next()
            i -= 10
        }
        value *= java.lang.Long.signum(bytes).toLong()
        return "%.1f %ciB".format(value / 1024.0, ci.current())
    }

    fun toHumanReadableSiString(): String {
        var bytes = bytes
        if (-1000 < bytes && bytes < 1000) {
            return "$bytes B"
        }
        val ci: CharacterIterator = StringCharacterIterator("kMGTPE")
        while (bytes <= -999950 || bytes >= 999950) {
            bytes /= 1000
            ci.next()
        }
        return "%.1f %cB".format(bytes / 1000.0, ci.current())
    }
}
