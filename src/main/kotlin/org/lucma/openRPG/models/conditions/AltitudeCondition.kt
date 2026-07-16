package org.lucma.openRPG.models.conditions

import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition

/**
 * Se activa cuando el jugador está entre ciertos Y levels.
 * @param minY Y mínimo (inclusive)
 * @param maxY Y máximo (inclusive). -1 = sin límite superior
 */
class AltitudeCondition(
    private val minY: Int = 0,
    private val maxY: Int = -1
) : Condition {
    override fun matches(context: EffectContext): Boolean {
        val y = context.player.location.blockY
        if (y < minY) return false
        if (maxY >= 0 && y > maxY) return false
        return true
    }
}
