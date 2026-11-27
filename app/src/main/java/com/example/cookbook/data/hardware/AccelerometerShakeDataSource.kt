package com.example.cookbook.data.hardware

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.sqrt

@Singleton
class AccelerometerShakeDataSource @Inject constructor(
    @ApplicationContext context: Context
) {
    private val appContext: Context = context.applicationContext

    private val sensorManager: SensorManager? by lazy {
        try {
            appContext.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        } catch (_: Exception) {
            null
        }
    }

    private val accelerometer: Sensor? by lazy {
        try {
            sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        } catch (_: Exception) {
            null
        }
    }

    val isAvailable: Boolean
        get() = accelerometer != null

    fun shakes(): Flow<Unit> {
        val manager = sensorManager
        val sensor = accelerometer

        if (manager == null || sensor == null) {
            return emptyFlow()
        }

        return callbackFlow {
            var lastShakeTimestamp = 0L

            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    if (event == null) return
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]

                    // Magnitud de la aceleración resultante
                    val accelerationMagnitude = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
                    val gForce = accelerationMagnitude / SensorManager.GRAVITY_EARTH

                    // Umbral aproximado de 2.5 g
                    if (gForce >= 2.5f) {
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastShakeTimestamp >= 1000L) {
                            lastShakeTimestamp = currentTime
                            trySend(Unit)
                        }
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                    // No se requiere acción ante cambios de precisión
                }
            }

            try {
                manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
            } catch (_: Exception) {
                close()
            }

            awaitClose {
                try {
                    manager.unregisterListener(listener)
                } catch (_: Exception) {
                    // Cierre seguro
                }
            }
        }
    }
}
