package org.lucma.openRPG.models.effects

import org.lucma.openRPG.core.effect.EffectPriority
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Effect
import org.lucma.openRPG.models.types.StackType

class ManaRegenEffect(val regenPerSecond: Double) : Effect {
    override val priority = EffectPriority.CLASS
    override val stackType = StackType.ADDITIVE
    override fun apply(context: EffectContext) {
        context.stats.manaRegen += regenPerSecond
    }
}
