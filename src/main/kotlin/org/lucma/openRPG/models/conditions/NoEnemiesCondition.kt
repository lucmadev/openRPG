package org.lucma.openRPG.models.conditions

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition

class NoEnemiesCondition(
    private val radius: Double = 5.0
) : Condition {

    override fun matches(context: EffectContext): Boolean {
        val nearbyEntities = context.player.getNearbyEntities(radius, radius, radius)
        return nearbyEntities.none { it is LivingEntity && it !is Player }
    }
}
