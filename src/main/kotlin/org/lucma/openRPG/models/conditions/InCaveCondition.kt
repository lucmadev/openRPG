package org.lucma.openRPG.models.conditions

import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition

/**
 * Se activa cuando el jugador está en una cueva
 * (luz de cielo baja, típicamente bajo tierra).
 */
class InCaveCondition(
    private val maxSkyLight: Int = 7
) : Condition {
    override fun matches(context: EffectContext): Boolean {
        val block = context.player.location.block
        return block.lightLevel <= maxSkyLight
    }
}
