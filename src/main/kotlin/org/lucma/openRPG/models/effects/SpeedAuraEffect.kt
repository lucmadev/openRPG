package org.lucma.openRPG.models.effects

import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.lucma.openRPG.core.effect.EffectPriority
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Effect
import org.lucma.openRPG.models.types.StackType

/**
 * Da velocidad a los aliados cercanos.
 */
class SpeedAuraEffect(
    val radius: Double = 8.0,
    val amplifier: Int = 1
) : Effect {
    override val priority = EffectPriority.CLASS
    override val stackType = StackType.ADDITIVE

    override fun apply(context: EffectContext) {
        val player = context.player
        val entities = player.getNearbyEntities(radius, radius, radius)
        for (entity in entities) {
            if (entity is Player) {
                entity.addPotionEffect(
                    PotionEffect(PotionEffectType.SPEED, 60, amplifier)
                )
            }
        }
    }
}
