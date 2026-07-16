package org.lucma.openRPG.models.conditions

import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition

/**
 * Se activa cuando el jugador está cayendo desde cierta altura.
 * @param minDistance distancia mínima de caída en bloques
 */
class FallDistanceCondition(
    private val minDistance: Float = 3.0f
) : Condition {
    override fun matches(context: EffectContext): Boolean {
        return context.player.fallDistance >= minDistance
    }
}
