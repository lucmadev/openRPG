package org.lucma.openRPG.models.effects

import org.bukkit.attribute.Attribute
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.core.effect.EffectPriority
import org.lucma.openRPG.models.types.Effect
import org.lucma.openRPG.models.types.StackType

/**
 * Heals the player a fixed amount of health.
 * @param amount health to heal (2 = 1 heart)
 */
class HealEffect(
    val amount: Double
) : Effect {

    override val priority = EffectPriority.CLASS
    override val stackType = StackType.ADDITIVE

    override fun apply(context: EffectContext) {
        val player = context.player
        val maxHealth = player.getAttribute(Attribute.MAX_HEALTH)?.value ?: 20.0
        val newHealth = (player.health + amount).coerceAtMost(maxHealth)
        player.health = newHealth
    }
}
