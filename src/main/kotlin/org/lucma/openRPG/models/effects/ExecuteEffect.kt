package org.lucma.openRPG.models.effects

import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.lucma.openRPG.core.effect.EffectPriority
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Effect
import org.lucma.openRPG.models.types.StackType

/**
 * Aumenta el daño si el objetivo tiene vida por debajo del threshold.
 * @param bonusMultiplier multiplicador extra (ej: 0.5 = +50%)
 * @param threshold porcentaje de vida del objetivo (0.0-1.0)
 */
class ExecuteEffect(
    val bonusMultiplier: Double,
    val threshold: Double = 0.30
) : Effect {
    override val priority = EffectPriority.CLASS
    override val stackType = StackType.MULTIPLICATIVE

    override fun apply(context: EffectContext) {
        val event = context.event
        if (event !is EntityDamageByEntityEvent) return
        val target = event.entity as? LivingEntity ?: return
        val maxHealth = target.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH)?.value ?: 20.0
        if ((target.health / maxHealth) <= threshold) {
            context.stats.damageMultiplier *= 1.0 + bonusMultiplier
        }
    }
}
