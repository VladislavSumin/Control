package ru.falseteam.control.repository.servers

import android.content.Context
import com.russhwolf.settings.string
import ru.falseteam.control.repository.BaseRepository
import ru.falseteam.control.repository.Repository

class ServerRepositoryImpl(context: Context) :
    BaseRepository(context, Repository.Servers), ServersRepository {

    override var defaultServer: String by settings.string("default_server")
}