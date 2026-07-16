package org.lucma.openRPG.models.effects

import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.core.effect.EffectPriority
import org.lucma.openRPG.core.stats.StatEngine
import org.lucma.openRPG.models.types.Effect
import org.lucma.openRPG.models.types.StackType

/**
 * Adds critical hit chance.
 * @param chance additional probability (e.g. 0.10 = +10%)
 */
class CriticalChanceEffect(
    val chance: Double
) : Effect {

    override val priority = EffectPriority.CLASS
    override val stackType = StackType.ADDITIVE

    override fun apply(context: EffectContext) {
        context.stats.critChance += chance
    }
}
