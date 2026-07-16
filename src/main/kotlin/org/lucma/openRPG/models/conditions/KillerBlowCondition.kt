package org.lucma.openRPG.models.conditions

import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition

/**
 * Se activa si el golpe actual mataría al objetivo.
 */
class KillerBlowCondition : Condition {

    override fun matches(context: EffectContext): Boolean {
        val event = context.event
        if (event !is EntityDamageByEntityEvent) return false
        val target = event.entity
        if (target is org.bukkit.entity.LivingEntity) {
            return event.damage >= target.health
        }
        return false
    }
}
