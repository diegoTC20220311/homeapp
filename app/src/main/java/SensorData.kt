package com.example.homeapp

import com.google.gson.annotations.SerializedName

/**
 * Representa la estructura del mensaje JSON que se espera del broker MQTT.
 * Las variables deben coincidir con las claves (keys) enviadas por el ESP32.
 */
data class SensorData(
    @SerializedName("estacionamiento") val estacionamiento: String,
    @SerializedName("luz") val luz: String,
    @SerializedName("puerta") val puerta: String,
    @SerializedName("temperatura") val temperatura: String
)