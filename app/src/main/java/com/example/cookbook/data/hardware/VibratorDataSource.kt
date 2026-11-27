package com.example.cookbook.data.hardware

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.cookbook.domain.model.HapticEffect
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VibratorDataSource @Inject constructor(
    @ApplicationContext context: Context
) {
    private val appContext: Context = context.applicationContext
    private val vibrator: Vibrator? by lazy {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                appContext.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (_: Exception) {
            null
        }
    }

    val hasVibrator: Boolean
        get() = try {
            vibrator?.hasVibrator() == true
        } catch (_: Exception) {
            false
        }

    fun vibrate(effect: HapticEffect) {
        val vib = vibrator ?: return
        if (!hasVibrator) return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val vibrationEffect = when (effect) {
                    HapticEffect.LIGHT_CLICK -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                        } else {
                            VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE)
                        }
                    }
                    HapticEffect.SUCCESS -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK)
                        } else {
                            VibrationEffect.createWaveform(longArrayOf(0, 40, 60, 60), -1)
                        }
                    }
                    HapticEffect.TIMER_DONE -> {
                        VibrationEffect.createWaveform(longArrayOf(0, 300, 150, 300, 150, 400), -1)
                    }
                }
                vib.vibrate(vibrationEffect)
            } else {
                @Suppress("DEPRECATION")
                when (effect) {
                    HapticEffect.LIGHT_CLICK -> vib.vibrate(25)
                    HapticEffect.SUCCESS -> vib.vibrate(longArrayOf(0, 40, 60, 60), -1)
                    HapticEffect.TIMER_DONE -> vib.vibrate(longArrayOf(0, 300, 150, 300, 150, 400), -1)
                }
            }
        } catch (_: Exception) {
            // Hardware safety: nunca debe propagar excepción al usuario
        }
    }
}
