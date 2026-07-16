package org.lucma.openRPG.models.conditions

import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition

/**
 * Se activa cuando el objetivo del golpe tiene vida baja.
 * @param threshold porcentaje de vida (0.0-1.0)
 */
class TargetLowHealthCondition(
    private val threshold: Double = 0.30
) : Condition {

    override fun matches(context: EffectContext): Boolean {
        val event = context.event
        if (event !is EntityDamageByEntityEvent) return false
        val target = event.entity
        if (target !is LivingEntity) return false
        val maxHealth = target.getAttribute(Attribute.MAX_HEALTH)?.value ?: 20.0
        return (target.health / maxHealth) <= threshold
    }
}
