package org.lucma.openRPG.models.effects

import org.lucma.openRPG.core.effect.EffectPriority
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Effect
import org.lucma.openRPG.models.types.StackType

/**
 * Convierte un % del daño en daño verdadero (ignora armadura).
 * Aplica como multiplicador adicional.
 */
class TrueDamageEffect(val percentage: Double = 0.20) : Effect {
    override val priority = EffectPriority.BUFF
    override val stackType = StackType.MULTIPLICATIVE

    override fun apply(context: EffectContext) {
        context.stats.damageMultiplier *= 1.0 + percentage
    }
}
