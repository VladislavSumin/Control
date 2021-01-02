package ru.falseteam.control.camsconnection.protocol

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


internal object CommandRepository {
    fun auth() = compile(
        CommandCode.LOGIN_REQ, 0,
        Json.encodeToString(AuthData())
    )

    @Serializable
    data class AuthData(
        val EncryptType: String = "MD5",
        val LoginType: String = "DVRIP-Web",
        val Password: String = "tlJwpbo6",
        val Username: String = "admin",
    )

//    fun keepAlive(sessionID: Int) = compile(
//        CommandCode.KEEPALIVE_REQ, sessionID,
//        "Name" to "KeepAlive",
//        "SessionID" to "0x%X".format(sessionID)
//    )
//
//    fun monitorClaim(sessionID: Int) = compile(
//        CommandCode.MONITOR_CLAIM, sessionID,
//        "Name" to "OPMonitor",
//        "OPMonitor" to mapOf(
//            "Action" to "Claim",
//            "Parameter" to mapOf(
//                "Channel" to 0,
//                "CombinMode" to "NONE",
//                "StreamType" to "Main",
//                "TransMode" to "TCP"
//            )
//        ),
//        "SessionID" to "0x%X".format(sessionID)
//    )
//
//    fun monitorStart(sessionID: Int) = compile(
//        CommandCode.MONITOR_REQ, sessionID,
//        "OPMonitor" to mapOf(
//            "Action" to "Start",
//            "Parameter" to mapOf(
//                "Channel" to 0,
//                "CombinMode" to "NONE",
//                "StreamType" to "Main",
//                "TransMode" to "TCP"
//            )
//        ),
//        "SessionID" to "0x%X".format(sessionID)
//    )
//
//    fun monitorStop(sessionID: Int) = compile(
//        CommandCode.MONITOR_REQ, sessionID,
//        "OPMonitor" to mapOf(
//            "Action" to "Stop",
//            "Parameter" to mapOf(
//                "Channel" to 0,
//                "CombinMode" to "NONE",
//                "StreamType" to "Main",
//                "TransMode" to "TCP"
//            )
//        ),
//        "SessionID" to "0x%X".format(sessionID)
//    )
//
//    fun alarmStart(sessionID: Int) = compile(
//        CommandCode.GUARD_REQ, sessionID,
//        "SessionID" to "0x%X".format(sessionID)
//    )


    private fun compile(commandCode: CommandCode, sessionID: Int, data: String) =
        newInstance(commandCode, data.toByteArray(), sessionID)

    private fun newInstance(messageId: CommandCode, data: ByteArray, sessionId: Int = 0) =
        Msg(
            messageId = messageId,
            dataLength = data.size,
            data = data,
            sessionId = sessionId
        )
}