package org.lucma.openRPG.models.effects

import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.lucma.openRPG.core.effect.EffectPriority
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Effect
import org.lucma.openRPG.models.types.StackType

/**
 * Aumenta el daño si el jugador está detrás del objetivo.
 */
class BackstabEffect(val bonusMultiplier: Double) : Effect {
    override val priority = EffectPriority.CLASS
    override val stackType = StackType.MULTIPLICATIVE

    override fun apply(context: EffectContext) {
        val event = context.event
        if (event !is EntityDamageByEntityEvent) return
        val target = event.entity as? LivingEntity ?: return

        val playerLoc = context.player.location
        val targetLoc = target.location
        val toTarget = targetLoc.toVector().subtract(playerLoc.toVector()).normalize()
        val targetDirection = targetLoc.direction
        val dot = toTarget.dot(targetDirection)

        if (dot > 0.3) {
            context.stats.damageMultiplier *= 1.0 + bonusMultiplier
        }
    }
}
