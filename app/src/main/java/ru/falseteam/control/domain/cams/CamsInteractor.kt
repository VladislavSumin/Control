package ru.falseteam.control.domain.cams

import kotlinx.coroutines.flow.Flow

interface CamsInteractor {
    fun observeCams(): Flow<List<Camera>>
}