package org.lucma.openRPG.models.effects

import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.lucma.openRPG.core.effect.EffectPriority
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Effect
import org.lucma.openRPG.models.types.StackType

class LightningStrikeEffect(
    val damage: Double = 0.0
) : Effect {
    override val priority = EffectPriority.CLASS
    override val stackType = StackType.ADDITIVE

    override fun apply(context: EffectContext) {
        val event = context.event
        if (event !is EntityDamageByEntityEvent) return
        val target = event.entity

        val world = target.world
        val loc = target.location
        world.strikeLightning(loc)

        if (damage > 0.0 && target is LivingEntity) {
            target.damage(damage, context.player)
        }
    }
}
