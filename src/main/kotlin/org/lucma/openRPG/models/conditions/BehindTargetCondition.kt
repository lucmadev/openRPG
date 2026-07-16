package org.lucma.openRPG.models.conditions

import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition

/**
 * Se activa cuando el jugador golpea a una entidad por detrás.
 * Usa el vector de dirección del jugador y la posición relativa del objetivo.
 */
class BehindTargetCondition : Condition {

    override fun matches(context: EffectContext): Boolean {
        val event = context.event
        if (event !is EntityDamageByEntityEvent) return false
        val target = event.entity
        if (target !is LivingEntity) return false

        val playerLoc = context.player.location
        val targetLoc = target.location

        // Vector del jugador al objetivo
        val toTarget = targetLoc.toVector().subtract(playerLoc.toVector()).normalize()
        // Vector hacia donde mira el objetivo
        val targetDirection = targetLoc.direction

        // Si el objetivo mira en dirección OPUESTA al jugador, está detrás
        val dot = toTarget.dot(targetDirection)
        return dot > 0.3
    }
}
