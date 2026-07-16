package org.lucma.openRPG.models.conditions

import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition

class InWorldCondition(
    private val worldName: String
) : Condition {
    override fun matches(context: EffectContext): Boolean {
        return context.player.world.name.equals(worldName, ignoreCase = true)
    }
}
