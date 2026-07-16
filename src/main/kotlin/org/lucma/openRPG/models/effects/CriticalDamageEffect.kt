package org.lucma.openRPG.models.effects

import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.core.effect.EffectPriority
import org.lucma.openRPG.models.types.Effect
import org.lucma.openRPG.models.types.StackType

/**
 * Increases critical damage multiplier.
 * @param bonus additional value to the multiplier (e.g. 0.50 = +50% crit damage)
 */
class CriticalDamageEffect(
    val bonus: Double
) : Effect {

    override val priority = EffectPriority.CLASS
    override val stackType = StackType.ADDITIVE

    override fun apply(context: EffectContext) {
        context.stats.critMultiplier += bonus
    }
}
