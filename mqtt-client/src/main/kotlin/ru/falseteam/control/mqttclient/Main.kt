package ru.falseteam.control.mqttclient

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

fun main() {
    val mqttClient = MqttClient("tcp://nuc.vs:1883")
    runBlocking {
        mqttClient.subscribe("#").collect {
            println(it)
        }
    }
}