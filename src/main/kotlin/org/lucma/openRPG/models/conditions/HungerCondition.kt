package org.lucma.openRPG.models.conditions

import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition

/**
 * Se activa según el nivel de hambre del jugador.
 * @param threshold umbral de hambre (0-20)
 * @param mode "below" (default) o "above"
 */
class HungerCondition(
    private val threshold: Int = 6,
    private val mode: String = "below"
) : Condition {
    override fun matches(context: EffectContext): Boolean {
        val food = context.player.foodLevel
        return if (mode == "above") food >= threshold else food <= threshold
    }
}
