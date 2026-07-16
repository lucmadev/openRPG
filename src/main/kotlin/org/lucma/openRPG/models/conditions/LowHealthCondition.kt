package org.lucma.openRPG.models.conditions

import org.bukkit.attribute.Attribute
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition

/**
 * Activates when the player's health is below the threshold.
 * @param thresholdPercentage health percentage (0.0 - 1.0). E.g. 0.30 = 30%
 */
class LowHealthCondition(
    val thresholdPercentage: Double = 0.30
) : Condition {

    override fun matches(context: EffectContext): Boolean {
        val player = context.player
        val health = player.health
        val maxHealth = player.getAttribute(Attribute.MAX_HEALTH)?.value ?: 20.0
        return (health / maxHealth) < thresholdPercentage
    }
}
