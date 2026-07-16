package org.lucma.openRPG.models.effects

import org.bukkit.Particle
import org.lucma.openRPG.core.effect.EffectPriority
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Effect
import org.lucma.openRPG.models.types.StackType

/**
 * Spawnea partículas en la posición del jugador.
 * @param particleName nombre del Particle (ej: "FLAME", "CRIT", "HEART")
 * @param count cantidad de partículas
 * @param speed velocidad de las partículas
 * @param offsetX dispersión en X
 * @param offsetY dispersión en Y
 * @param offsetZ dispersión en Z
 */
class ParticleEffect(
    val particleName: String = "CRIT",
    val count: Int = 10,
    val speed: Double = 0.1,
    val offsetX: Double = 0.5,
    val offsetY: Double = 0.5,
    val offsetZ: Double = 0.5
) : Effect {
    override val priority = EffectPriority.CLASS
    override val stackType = StackType.ADDITIVE

    override fun apply(context: EffectContext) {
        val player = context.player
        val particle = try {
            Particle.valueOf(particleName.uppercase())
        } catch (_: Exception) {
            return
        }

        player.world.spawnParticle(
            particle,
            player.location,
            count,
            offsetX, offsetY, offsetZ,
            speed
        )
    }
}
