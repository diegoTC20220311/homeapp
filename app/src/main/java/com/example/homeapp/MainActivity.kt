package com.example.homeapp

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.activity.enableEdgeToEdge
import com.google.gson.Gson

// ⬇️ IMPORTACIONES CORREGIDAS PARA RESOLVER ERRORES DE REFERENCIA ⬇️
import com.example.homeapp.SensorData // Importa la Data Class
// Si MqttManager.kt está en el mismo paquete 'com.example.homeapp', no necesita importación explícita.
// Si lo colocaste en otro paquete, añade: import com.ruta.del.archivo.MqttManager

class MainActivity : AppCompatActivity() {

    private lateinit var mqttManager: MqttManager

    // Referencias a los TextViews del layout (activity_main.xml)
    private lateinit var textViewMqttStatus: TextView
    private lateinit var textViewEstacionamiento: TextView
    private lateinit var textViewLuz: TextView
    private lateinit var textViewPuerta: TextView
    private lateinit var textViewTemperatura: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // 1. Inicializar vistas
        initViews()

        // Aplicar insets (de tu código base)
        // Usamos R.id.textViewTitle como la vista principal para los insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.textViewTitle)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 2. Inicializar MqttManager
        mqttManager = MqttManager(
            context = applicationContext,
            onMessageReceived = { payload -> updateUIWithData(payload) },
            onStatusChanged = { status -> updateMqttStatus(status) }
        )

        // 3. Iniciar la conexión MQTT
        mqttManager.connect()
    }

    // Corregida para usar los IDs definidos en activity_main.xml
    private fun initViews() {
        textViewMqttStatus = findViewById(R.id.textViewMqttStatus)
        textViewEstacionamiento = findViewById(R.id.textViewEstacionamiento)
        textViewLuz = findViewById(R.id.textViewLuz)
        textViewPuerta = findViewById(R.id.textViewPuerta)
        textViewTemperatura = findViewById(R.id.textViewTemperatura)
    }

    /**
     * Actualiza el TextView de estado de la conexión MQTT.
     */
    private fun updateMqttStatus(status: String) {
        runOnUiThread {
            // Uso de Placeholders para evitar la advertencia de concatenación (aunque se recomienda strings.xml)
            textViewMqttStatus.text = "Estado MQTT: $status"

            // Colores definidos con constantes de Color para evitar advertencias de String.toColorInt
            val color = if (status.contains("Conectado")) {
                Color.GREEN // Usa la constante GREEN (es Color.parseColor("#00FF00"))
            } else {
                Color.RED // Usa la constante RED
            }
            textViewMqttStatus.setTextColor(color)
        }
    }

    /**
     * Procesa el mensaje JSON y actualiza la UI.
     */
    private fun updateUIWithData(payload: String) {
        val gson = Gson()
        try {
            val data: SensorData = gson.fromJson(payload, SensorData::class.java)

            runOnUiThread {
                // Uso de Placeholders para evitar advertencias de concatenación
                textViewEstacionamiento.text = "Estado: ${data.estacionamiento}"
                textViewLuz.text = "Estado: ${data.luz}"
                textViewPuerta.text = "Estado: ${data.puerta}"
                textViewTemperatura.text = "Valor: ${data.temperatura}"
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error al parsear JSON: ${e.message}. Payload recibido: $payload")
            runOnUiThread {
                textViewMqttStatus.text = "Error de datos: JSON inválido"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mqttManager.isInitialized) {
            mqttManager.disconnect()
        }
    }
}