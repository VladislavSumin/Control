package ru.falseteam.control

import android.app.Application
import org.kodein.di.DI
import org.kodein.di.DIAware
import ru.falseteam.control.di.Kodein

class App : Application(), DIAware {
    override val di: DI by Kodein
}