package org.lucma.openRPG.models.effects

import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.core.effect.EffectPriority
import org.lucma.openRPG.models.types.Effect
import org.lucma.openRPG.models.types.StackType

/**
 * Prende fuego al enemigo golpeado durante [duration] segundos.
 * @param duration segundos de combustión
 */
class FireAuraEffect(
    val duration: Int = 3
) : Effect {

    override val priority = EffectPriority.CLASS
    override val stackType = StackType.ADDITIVE

    override fun apply(context: EffectContext) {
        val event = context.event
        if (event !is EntityDamageByEntityEvent) return

        val target = event.entity
        if (target !is LivingEntity) return

        target.fireTicks = (target.fireTicks + duration * 20).coerceAtMost(duration * 20)
    }
}
