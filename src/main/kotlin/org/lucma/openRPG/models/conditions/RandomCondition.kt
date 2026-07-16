package org.lucma.openRPG.models.conditions

import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition
import kotlin.random.Random

/**
 * Se activa con una probabilidad aleatoria.
 * @param chance probabilidad de activarse (0.0 - 1.0)
 */
class RandomCondition(
    private val chance: Double = 0.5
) : Condition {
    override fun matches(context: EffectContext): Boolean {
        return Random.nextDouble() < chance
    }
}
