package org.lucma.openRPG.models.effects

import org.bukkit.util.Vector
import org.lucma.openRPG.core.effect.EffectPriority
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Effect
import org.lucma.openRPG.models.types.StackType

/**
 * Dash en la dirección de movimiento.
 * @param distance distancia del dash
 */
class DashEffect(val distance: Double = 5.0) : Effect {
    override val priority = EffectPriority.CLASS
    override val stackType = StackType.ADDITIVE

    override fun apply(context: EffectContext) {
        val player = context.player
        val direction = player.location.direction
        val velocity = direction.normalize().multiply(distance * 0.4).setY(0.2)
        player.velocity = velocity
    }
}
