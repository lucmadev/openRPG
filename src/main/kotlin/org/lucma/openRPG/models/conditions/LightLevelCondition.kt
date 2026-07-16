package org.lucma.openRPG.models.conditions

import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition

/**
 * Se activa según el nivel de luz en la posición del jugador.
 * @param min nivel mínimo de luz (0-15)
 * @param max nivel máximo de luz (0-15)
 * @param type "block" (luz de bloques), "sky" (luz de cielo) o "any" (default)
 */
class LightLevelCondition(
    private val min: Int = 0,
    private val max: Int = 15,
    private val type: String = "any"
) : Condition {
    override fun matches(context: EffectContext): Boolean {
        val block = context.player.location.block
        val level = when (type.lowercase()) {
            "block" -> block.lightLevel
            "sky" -> block.lightFromSky
            else -> block.lightLevel
        }
        return level in min..max
    }
}
