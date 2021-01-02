package ru.falseteam.control.camsconnection.protocol

import java.lang.StringBuilder

/**
 * Message structure:
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |   Head Flag   |    VERSION    |  RESERVED01   |  RESERVED02   |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                            SESSION ID                         |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                         SEQUENCE NUMBER                       |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |  Total Packet |   CurPacket   |           Message Id          |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                          Data Length                          |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                           DATA . . .                          |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *
 * @param headFlag - 1 byte, always 0xFF
 * @param version - 1 byte, used protocol version, known only 0x00 && 0x01 protocol version
 * @param reserved01 - 1 byte, unused, always 0x00
 * @param reserved02 - 1 byte, unused, always 0x00
 * @param sessionId - 4 byte, current session identifier
 * @param sequenceNumber - 4 byte, ???
 * @param totalPacket - 1 byte, ???
 * @param currentPacket - 1 byte, ???
 * @param messageId - 2 byte, protocol name
 * @param dataLength 4 byte, data length
 * @param data - data
 */
internal data class Msg(
    var headFlag: Byte = -1,
    var version: Byte = 0x01,
    var reserved01: Byte = 0x00,
    var reserved02: Byte = 0x00,
    var sessionId: Int = 0x00,
    var sequenceNumber: Int = 0x00,
    var totalPacket: Byte = 0x00,
    var currentPacket: Byte = 0x00,
    var messageId: CommandCode = CommandCode.NULL,
    var dataLength: Int = 0x00,
    var data: ByteArray = ByteArray(0)
) {
    fun getDataAsString(): String {
        return String(data.sliceArray(0 until dataLength - 2))
    }

    // 20 is header size
    fun getSize(): Int = 20 + dataLength

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Msg

        if (headFlag != other.headFlag) return false
        if (version != other.version) return false
        if (reserved01 != other.reserved01) return false
        if (reserved02 != other.reserved02) return false
        if (sessionId != other.sessionId) return false
        if (sequenceNumber != other.sequenceNumber) return false
        if (totalPacket != other.totalPacket) return false
        if (currentPacket != other.currentPacket) return false
        if (messageId != other.messageId) return false
        if (dataLength != other.dataLength) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result: Int = headFlag.toInt()
        result = 31 * result + version
        result = 31 * result + reserved01
        result = 31 * result + reserved02
        result = 31 * result + sessionId
        result = 31 * result + sequenceNumber
        result = 31 * result + totalPacket
        result = 31 * result + currentPacket
        result = 31 * result + messageId.hashCode()
        result = 31 * result + dataLength
        result = 31 * result + data.contentHashCode()
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("Version: 0x").append("%02X".format(version)).append('\n')
        sb.append("Session ID: 0x").append("%08X".format(sessionId)).append('\n')
        sb.append("Sequence number: 0x").append("%08X".format(sequenceNumber)).append('\n')
        sb.append("Current packet: 0x").append("%02X".format(currentPacket)).append('\n')
        sb.append("Total packet: 0x").append("%02X".format(totalPacket)).append('\n')
        sb.append("Message ID: ").append(messageId).append('\n')
        sb.append("Data Length: 0x").append("%08X".format(dataLength)).append('\n')
        sb.append("Data: ")
        if (data[0] == '{'.toByte()) sb.append(getDataAsString())
        else sb.append("<binary data>")
        return sb.toString()
    }

    fun getFullInfo(): String {
        val sb = StringBuilder()
        sb.append("Head Flag: 0x").append("%02X".format(headFlag)).append('\n')
        sb.append("Version: 0x").append("%02X".format(version)).append('\n')
        sb.append("Reserved01: 0x").append("%02X".format(reserved01)).append('\n')
        sb.append("Reserved02: 0x").append("%02X".format(reserved02)).append('\n')
        sb.append("Session ID: 0x").append("%08X".format(sessionId)).append('\n')
        sb.append("Sequence number: 0x").append("%08X".format(sequenceNumber)).append('\n')
        sb.append("Current packet: 0x").append("%02X".format(currentPacket)).append('\n')
        sb.append("Total packet: 0x").append("%02X".format(totalPacket)).append('\n')
        sb.append("Message ID: ").append(messageId).append('\n')
        sb.append("Data Length: 0x").append("%08X".format(dataLength)).append('\n')
        sb.append("Data: ")
        if (data[0] == '{'.toByte()) sb.append(getDataAsString())//TODO change
        else sb.append("<binary data>")
        return sb.toString()
    }
}