package org.lucma.openRPG.models.effects

import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.core.effect.EffectPriority
import org.lucma.openRPG.core.CooldownManager
import org.lucma.openRPG.models.types.Effect
import org.lucma.openRPG.models.types.StackType

/**
 * Sets the hit enemy on fire for [duration] seconds,
 * with a cooldown of [cooldownSeconds] between activations.
 *
 * @param duration burn seconds
 * @param cooldownSeconds cooldown between uses (0 = no cooldown)
 */
class FireAuraEffect(
    val duration: Int = 3,
    val cooldownSeconds: Int = 0
) : Effect {

    override val priority = EffectPriority.CLASS
    override val stackType = StackType.ADDITIVE

    private val cooldownKey = "fire_aura"

    override fun apply(context: EffectContext) {
        val event = context.event
        if (event !is EntityDamageByEntityEvent) return

        val target = event.entity
        if (target !is LivingEntity) return

        // Cooldown per player (who applies the effect)
        if (cooldownSeconds > 0) {
            if (CooldownManager.isOnCooldown(context.player, cooldownKey)) return
            CooldownManager.set(context.player, cooldownKey, cooldownSeconds)
        }

        target.fireTicks = (target.fireTicks + duration * 20).coerceAtMost(duration * 20)
    }
}
