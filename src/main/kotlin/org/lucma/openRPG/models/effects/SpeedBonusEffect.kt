package org.lucma.openRPG.models.effects

import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.core.effect.EffectPriority
import org.lucma.openRPG.core.stats.StatEngine
import org.lucma.openRPG.models.types.Effect
import org.lucma.openRPG.models.types.StackType

class SpeedBonusEffect(val bonus: Double) : Effect {

    override val priority = EffectPriority.CLASS
    override val stackType = StackType.MULTIPLICATIVE

    override fun apply(context: EffectContext) {
        StatEngine.applySpeedMultiplier(context.stats, 1.0 + bonus)
    }
}
