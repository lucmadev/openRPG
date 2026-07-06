package org.lucma.openRPG.models.effects

import org.bukkit.attribute.Attribute
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.core.effect.EffectPriority
import org.lucma.openRPG.models.types.Effect
import org.lucma.openRPG.models.types.StackType

/**
 * Convierte un porcentaje del daño infligido en curación para el jugador.
 * @param stealPercentage porcentaje del daño que se convierte en vida (ej: 0.10 = 10%)
 */
class LifeStealEffect(
    val stealPercentage: Double
) : Effect {

    override val priority = EffectPriority.CLASS
    override val stackType = StackType.ADDITIVE

    override fun apply(context: EffectContext) {
        val event = context.event
        if (event !is EntityDamageByEntityEvent) return

        val damage = event.damage
        val healAmount = damage * stealPercentage
        val player = context.player
        val maxHealth = player.getAttribute(Attribute.MAX_HEALTH)?.value ?: 20.0
        val newHealth = (player.health + healAmount).coerceAtMost(maxHealth)
        player.health = newHealth
    }
}
