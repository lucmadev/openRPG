package org.lucma.openRPG.models.conditions

import org.bukkit.potion.PotionEffectType
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition

/**
 * Se activa cuando el jugador tiene un efecto de poción activo.
 * @param effectType tipo de efecto (ej: "speed", "strength"). Vacío = cualquier efecto.
 */
class HasPotionEffectCondition(
    private val effectType: String = ""
) : Condition {
    override fun matches(context: EffectContext): Boolean {
        val player = context.player
        if (effectType.isBlank()) return player.activePotionEffects.isNotEmpty()
        val type = PotionEffectType.getByName(effectType.uppercase()) ?: return false
        return player.hasPotionEffect(type)
    }
}
