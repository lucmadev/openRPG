package org.lucma.openRPG.models.conditions

import org.bukkit.attribute.Attribute
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition

class FullHealthCondition : Condition {
    override fun matches(context: EffectContext): Boolean {
        val player = context.player
        val maxHealth = player.getAttribute(Attribute.MAX_HEALTH)?.value ?: 20.0
        return player.health >= maxHealth
    }
}
