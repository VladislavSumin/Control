package ru.falseteam.control.server.di

import org.kodein.di.*
import ru.falseteam.control.api.rsub.CamsRSub
import ru.falseteam.control.server.domain.cams.CamsInteractor
import ru.falseteam.control.server.domain.cams.CamsInteractorImpl
import ru.falseteam.control.server.rsub.CamsRSubImpl
import ru.falseteam.rsub.server.RSubServer

val Kodein = DI {
    // Domain
    bind<CamsInteractor>() with singleton { CamsInteractorImpl() }

    // rSub
    bind<RSubServer>() with singleton {
        RSubServer().apply {
            registerImpl(instance<CamsRSub>())
        }
    }
    bind<CamsRSub>() with singleton { CamsRSubImpl(instance()) }
}
