package org.lucma.openRPG.models.effects

import org.lucma.openRPG.core.effect.EffectPriority
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Effect
import org.lucma.openRPG.models.types.StackType

class FeedEffect(val hungerRestored: Int) : Effect {
    override val priority = EffectPriority.CLASS
    override val stackType = StackType.ADDITIVE

    override fun apply(context: EffectContext) {
        val player = context.player
        player.foodLevel = (player.foodLevel + hungerRestored).coerceAtMost(20)
    }
}
