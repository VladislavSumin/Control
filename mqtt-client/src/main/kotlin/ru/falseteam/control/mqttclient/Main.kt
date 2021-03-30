package ru.falseteam.control.mqttclient

import org.eclipse.paho.client.mqttv3.*

fun main() {
    println("a")
    val mqttClient = MqttAsyncClient("tcp://nuc.vs:1883", "my_client_id")
    val co = MqttConnectOptions().apply {
        isCleanSession = true
    }
    mqttClient.connect(co).waitForCompletion()// TODO add auto reconnect

    mqttClient.setCallback(object : MqttCallback {
        override fun connectionLost(cause: Throwable?) {
        }

        override fun messageArrived(topic: String?, message: MqttMessage?) {
            println("$topic : $message")
        }

        override fun deliveryComplete(token: IMqttDeliveryToken?) {
        }
    })
    mqttClient.subscribe("#", 1).waitForCompletion()

    Thread.sleep(3000)

//    mqttClient.disconnect().waitForCompletion()
//    println("AAAA")
//    mqttClient.reconnect()
//    println("AAAA")

}