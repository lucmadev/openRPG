package org.lucma.openRPG.models.conditions

import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition

/**
 * Se activa cuando el jugador está dentro de una región rectangular.
 * @param world nombre del mundo
 * @param x1 coordenada X mínima
 * @param z1 coordenada Z mínima
 * @param x2 coordenada X máxima
 * @param z2 coordenada Z máxima
 */
class InRegionCondition(
    private val world: String = "",
    private val x1: Int = 0,
    private val z1: Int = 0,
    private val x2: Int = 0,
    private val z2: Int = 0
) : Condition {
    override fun matches(context: EffectContext): Boolean {
        val loc = context.player.location
        if (world.isNotBlank() && !context.player.world.name.equals(world, ignoreCase = true)) return false
        val minX = minOf(x1, x2)
        val maxX = maxOf(x1, x2)
        val minZ = minOf(z1, z2)
        val maxZ = maxOf(z1, z2)
        return loc.blockX in minX..maxX && loc.blockZ in minZ..maxZ
    }
}
