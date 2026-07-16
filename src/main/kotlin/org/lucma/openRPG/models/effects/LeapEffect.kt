package org.lucma.openRPG.models.effects

import org.bukkit.util.Vector
import org.lucma.openRPG.core.effect.EffectPriority
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Effect
import org.lucma.openRPG.models.types.StackType

/**
 * Impulsa al jugador hacia adelante.
 * @param power fuerza del impulso
 * @param upward componente vertical (0 = recto, 0.5 = algo arriba)
 */
class LeapEffect(
    val power: Double = 1.5,
    val upward: Double = 0.5
) : Effect {
    override val priority = EffectPriority.CLASS
    override val stackType = StackType.ADDITIVE

    override fun apply(context: EffectContext) {
        val player = context.player
        val direction = player.location.direction
        val velocity = direction.multiply(power).setY(upward)
        player.velocity = velocity
    }
}
