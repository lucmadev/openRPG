package org.lucma.openRPG.models.conditions

import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition

/**
 * Se activa según el nivel de experiencia vanilla del jugador.
 * @param minLevel nivel mínimo (inclusive)
 * @param maxLevel nivel máximo (inclusive, -1 = sin límite)
 */
class ExperienceCondition(
    private val minLevel: Int = 0,
    private val maxLevel: Int = -1
) : Condition {
    override fun matches(context: EffectContext): Boolean {
        val level = context.player.level
        if (level < minLevel) return false
        if (maxLevel >= 0 && level > maxLevel) return false
        return true
    }
}
