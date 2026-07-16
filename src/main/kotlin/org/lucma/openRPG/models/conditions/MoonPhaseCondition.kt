package org.lucma.openRPG.models.conditions

import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition

/**
 * Se activa según la fase lunar.
 * Fases: 0 = luna llena, 4 = luna nueva, etc.
 * @param phase fase lunar (0-7)
 */
class MoonPhaseCondition(
    private val phase: Int = 0
) : Condition {
    override fun matches(context: EffectContext): Boolean {
        val fullDays = context.player.world.fullTime / 24000L
        return (fullDays % 8).toInt() == phase
    }
}
