package com.example.homeapp

import android.content.Context
import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import java.io.UnsupportedEncodingException

class MqttManager(private val context: Context, private val onMessageReceived: (String) -> Unit, private val onStatusChanged: (String) -> Unit) {

    // ⚠️ CONFIGURACIÓN CLAVE ⚠️
    private val serverUri = "tcp://TU_BROKER_IP:1883" // Reemplaza TU_BROKER_IP (Ej: 192.168.1.10)
    private val subscriptionTopic = "home/sensors/data" // Tópico único que tu ESP32 publica
    private val clientId = MqttClient.generateClientId()
    private lateinit var mqttClient: MqttAndroidClient

    private val TAG = "MqttManager"

    fun connect() {
        mqttClient = MqttAndroidClient(context, serverUri, clientId)

        mqttClient.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(reconnect: Boolean, serverURI: String) {
                if (reconnect) {
                    onStatusChanged("Conectado (Reconexión)")
                    subscribeToTopic()
                } else {
                    onStatusChanged("Conectado exitosamente")
                    subscribeToTopic()
                }
            }

            override fun connectionLost(cause: Throwable?) {
                onStatusChanged("Conexión perdida. Intentando reconectar...")
                Log.e(TAG, "Conexión perdida: ${cause?.message}")
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                val payload = String(message?.payload ?: ByteArray(0))
                Log.d(TAG, "Mensaje recibido en $topic: $payload")
                // Enviar el payload de vuelta a MainActivity para su procesamiento
                onMessageReceived(payload)
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                // No usado para este ejemplo
            }
        })

        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.isAutomaticReconnect = true
        mqttConnectOptions.isCleanSession = true

        try {
            mqttClient.connect(mqttConnectOptions, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    onStatusChanged("Conectando...")
                    Log.d(TAG, "Conexión solicitada exitosamente")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    onStatusChanged("Fallo de conexión: ${exception?.message}")
                    Log.e(TAG, "Fallo de conexión: ${exception?.message}")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
            onStatusChanged("Error de MQTT: ${e.message}")
        }
    }

    private fun subscribeToTopic() {
        try {
            mqttClient.subscribe(subscriptionTopic, 0) { _, _ ->
                Log.d(TAG, "Suscrito a $subscriptionTopic")
            }
        } catch (e: MqttException) {
            Log.e(TAG, "Fallo al suscribir: ${e.message}")
        }
    }

    fun disconnect() {
        try {
            mqttClient.disconnect()
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }
}