package org.lucma.openRPG.models.conditions

import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition

/**
 * Se activa según la saturación del jugador.
 * @param threshold umbral de saturación (0.0 - 20.0)
 * @param mode "below" (default) o "above"
 */
class SaturationCondition(
    private val threshold: Double = 5.0,
    private val mode: String = "below"
) : Condition {
    override fun matches(context: EffectContext): Boolean {
        val saturation = context.player.saturation
        return if (mode == "above") saturation >= threshold else saturation <= threshold
    }
}
