package org.lucma.openRPG.models.effects

import org.bukkit.Sound
import org.lucma.openRPG.core.effect.EffectPriority
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Effect
import org.lucma.openRPG.models.types.StackType

/**
 * Reproduce un sonido en la posición del jugador.
 * @param soundName nombre del Sound (ej: "ENTITY_PLAYER_LEVELUP")
 * @param volume volumen (default: 1.0)
 * @param pitch tono (default: 1.0)
 */
class SoundEffect(
    val soundName: String = "ENTITY_PLAYER_LEVELUP",
    val volume: Float = 1.0f,
    val pitch: Float = 1.0f
) : Effect {
    override val priority = EffectPriority.CLASS
    override val stackType = StackType.ADDITIVE

    override fun apply(context: EffectContext) {
        val player = context.player
        val sound = try {
            Sound.valueOf(soundName.uppercase())
        } catch (_: Exception) {
            return
        }

        player.playSound(player.location, sound, volume, pitch)
    }
}
