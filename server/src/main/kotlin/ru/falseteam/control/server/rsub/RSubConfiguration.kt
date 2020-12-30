package ru.falseteam.control.server.rsub

import ru.falseteam.control.api.rsub.CamsRSub
import ru.falseteam.rsub.server.RSubServer

fun createRSubServer(
    camsRSub: CamsRSub,
): RSubServer {
    return RSubServer().apply {
        registerImpl(camsRSub, "camsRSub")
    }
}