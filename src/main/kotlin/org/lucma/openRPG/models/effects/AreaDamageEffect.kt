package org.lucma.openRPG.models.effects

import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.lucma.openRPG.core.effect.EffectPriority
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Effect
import org.lucma.openRPG.models.types.StackType

/**
 * Daño en área alrededor del objetivo golpeado.
 * @param radius radio del área
 * @param multiplier multiplicador del daño original para entidades cercanas
 */
class AreaDamageEffect(
    val radius: Double = 3.0,
    val multiplier: Double = 0.5
) : Effect {
    override val priority = EffectPriority.CLASS
    override val stackType = StackType.ADDITIVE

    override fun apply(context: EffectContext) {
        val event = context.event
        if (event !is EntityDamageByEntityEvent) return
        val target = event.entity
        val baseDamage = event.damage

        val entities = target.getNearbyEntities(radius, radius, radius)
        for (entity in entities) {
            if (entity is LivingEntity && entity != target && entity != context.player) {
                entity.damage(baseDamage * multiplier, context.player)
            }
        }
    }
}
