package org.lucma.openRPG.models.effects

import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.lucma.openRPG.core.effect.EffectPriority
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Effect
import org.lucma.openRPG.models.types.StackType
import kotlin.math.pow

/**
 * Daño en cadena: salta a [maxTargets] enemigos cercanos.
 * Cada salto reduce el daño por el multiplicador.
 */
class ChainDamageEffect(
    val radius: Double = 5.0,
    val maxTargets: Int = 3,
    val multiplier: Double = 0.7
) : Effect {
    override val priority = EffectPriority.CLASS
    override val stackType = StackType.ADDITIVE

    override fun apply(context: EffectContext) {
        val event = context.event
        if (event !is EntityDamageByEntityEvent) return
        val target = event.entity
        val baseDamage = event.damage

        val nearby = target.getNearbyEntities(radius, radius, radius)
            .filterIsInstance<LivingEntity>()
            .filter { it != target && it != context.player }
            .take(maxTargets)

        nearby.forEachIndexed { index, entity ->
            val chainDmg = baseDamage * multiplier.pow(index + 1)
            entity.damage(chainDmg, context.player)
        }
    }
}
