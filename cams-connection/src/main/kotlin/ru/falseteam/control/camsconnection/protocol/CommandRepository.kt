package ru.falseteam.control.camsconnection.protocol

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// TODO remove data classes
@Suppress("ConstructorParameterNaming")
internal object CommandRepository {
    private val json = Json {
        encodeDefaults = true
    }

    fun auth() = compile(
        CommandCode.LOGIN_REQ, 0,
        json.encodeToString(AuthData())
    )

    @Serializable
    data class AuthData(
        val EncryptType: String = "MD5",
        val LoginType: String = "DVRIP-Web",
        val PassWord: String = "tlJwpbo6",
        val UserName: String = "admin",
    )

    fun keepAlive(sessionID: Int) = compile(
        CommandCode.KEEPALIVE_REQ, sessionID,
        json.encodeToString(
            KeepAliveData(
                SessionID = "0x%X".format(sessionID)
            )
        )
    )

    @Serializable
    data class KeepAliveData(
        val Name: String = "KeepAlive",
        val SessionID: String,
    )

    fun monitorClaim(sessionID: Int) = compile(
        CommandCode.MONITOR_CLAIM, sessionID,
        json.encodeToString(
            MonitorClaimData(
                SessionID = "0x%X".format(sessionID)
            )
        )
    )

    @Serializable
    data class MonitorClaimData(
        val Name: String = "OPMonitor",
        val OPMonitor: OpMonitor = OpMonitor(),
        val SessionID: String
    ) {
        @Serializable
        data class OpMonitor(
            val Action: String = "Claim",
            val Parameter: MonitorParameter = MonitorParameter()
        ) {
            @Serializable
            data class MonitorParameter(
                val Channel: Int = 0,
                val CombinMode: String = "NONE",
                val StreamType: String = "Main",
                val TransMode: String = "TCP",
            )
        }
    }

    fun monitorStart(sessionID: Int) = compile(
        CommandCode.MONITOR_REQ, sessionID,
        json.encodeToString(
            MonitorClaimData(
                SessionID = "0x%X".format(sessionID),
                OPMonitor = MonitorClaimData.OpMonitor(Action = "Start")
            )
        )
    )

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
    fun alarmStart(sessionID: Int) = compile(
        CommandCode.GUARD_REQ, sessionID,
        json.encodeToString(AlarmStartData("0x%X".format(sessionID)))
    )

    @Serializable
    data class AlarmStartData(
        val SessionID: String
    )

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