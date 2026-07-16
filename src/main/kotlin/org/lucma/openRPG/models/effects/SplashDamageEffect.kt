package org.lucma.openRPG.models.effects

import org.bukkit.entity.LivingEntity
import org.lucma.openRPG.core.effect.EffectPriority
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Effect
import org.lucma.openRPG.models.types.StackType

/**
 * Daño en área alrededor del jugador.
 */
class SplashDamageEffect(
    val radius: Double = 3.0,
    val multiplier: Double = 0.4
) : Effect {
    override val priority = EffectPriority.CLASS
    override val stackType = StackType.ADDITIVE

    override fun apply(context: EffectContext) {
        val player = context.player
        val baseDamage = 1.0 // daño base para el splash

        val entities = player.getNearbyEntities(radius, radius, radius)
        for (entity in entities) {
            if (entity is LivingEntity && entity != player) {
                entity.damage(baseDamage * multiplier, player)
            }
        }
    }
}
