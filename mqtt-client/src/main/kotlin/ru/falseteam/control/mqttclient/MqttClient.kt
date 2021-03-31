package ru.falseteam.control.mqttclient

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.suspendCancellableCoroutine
import org.eclipse.paho.client.mqttv3.*
import java.lang.IllegalStateException
import kotlin.coroutines.resumeWithException

class MqttClient(serverUrl: String, clientId: String = "control-mqtt-client") {
    private val connection = channelFlow {
        val client = MqttAsyncClient(serverUrl, clientId)
        try {
            connect(client)
            send(client)
            suspendCancellableCoroutine<Unit> {
                it.invokeOnCancellation {
                    client.disconnect().waitForCompletion()
                }
                client.setCallback(object : MqttCallback {
                    override fun connectionLost(cause: Throwable) {
                        it.resumeWithException(cause)
                    }

                    override fun messageArrived(topic: String?, message: MqttMessage?) = Unit
                    override fun deliveryComplete(token: IMqttDeliveryToken?) = Unit
                })

                if (!client.isConnected) throw IllegalStateException()
            }

        } finally {
            client.close()
        }
    }.shareIn(
        GlobalScope,
        SharingStarted.WhileSubscribed(
            stopTimeoutMillis = 10_000L,
            replayExpirationMillis = 0L
        ),
        1
    )

    fun subscribe(filter: String): Flow<Message> = connection.flatMapLatest { client ->
        callbackFlow {
            client.subscribe(filter, 1) { topic, message ->
                sendBlocking(Message(topic, message.payload, message.isRetained))
            }
            awaitClose {
                client.unsubscribe(filter)
            }
        }
    }

    private fun connect(client: IMqttAsyncClient) {
        client.connect().waitForCompletion()
    }

    data class Message(
        val topic: String,
        val payload: ByteArray,
        val isRetained: Boolean,
    ) {
        val payloadString: String
            get() = String(payload)

        override fun toString(): String {
            return "Message(topic=$topic, payload=$payloadString, isRetained=$isRetained)"
        }
    }
}