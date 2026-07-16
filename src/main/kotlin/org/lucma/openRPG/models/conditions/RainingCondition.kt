package org.lucma.openRPG.models.conditions

import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition

class RainingCondition : Condition {
    override fun matches(context: EffectContext): Boolean {
        val world = context.player.world
        return world.hasStorm() || world.isThundering()
    }
}
