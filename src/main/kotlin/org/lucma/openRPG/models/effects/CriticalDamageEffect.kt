package org.lucma.openRPG.models.effects

import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.core.effect.EffectPriority
import org.lucma.openRPG.models.types.Effect
import org.lucma.openRPG.models.types.StackType

/**
 * Aumenta el multiplicador de daño crítico.
 * @param bonus valor adicional al multiplicador (ej: 0.50 = +50% daño crítico)
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
