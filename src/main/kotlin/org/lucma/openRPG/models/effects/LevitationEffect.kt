package org.lucma.openRPG.models.effects

import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.lucma.openRPG.core.effect.EffectPriority
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Effect
import org.lucma.openRPG.models.types.StackType

class LevitationEffect(
    val duration: Int = 3,
    val amplifier: Int = 1
) : Effect {
    override val priority = EffectPriority.CLASS
    override val stackType = StackType.ADDITIVE
    override fun apply(context: EffectContext) {
        val event = context.event
        if (event !is EntityDamageByEntityEvent) return
        val target = event.entity as? LivingEntity ?: return
        target.addPotionEffect(PotionEffect(PotionEffectType.LEVITATION, duration * 20, amplifier))
    }
}
