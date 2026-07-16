package org.lucma.openRPG.models.effects

import org.lucma.openRPG.core.effect.EffectPriority
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Effect
import org.lucma.openRPG.models.types.StackType

/**
 * Ignora escudos: añade daño extra para compensar la reducción del escudo.
 */
class ShieldBreakerEffect(
    val bonusMultiplier: Double = 0.30
) : Effect {
    override val priority = EffectPriority.CLASS
    override val stackType = StackType.MULTIPLICATIVE

    override fun apply(context: EffectContext) {
        if (context.player.isBlocking) {
            context.stats.damageMultiplier *= 1.0 + bonusMultiplier
        }
    }
}
