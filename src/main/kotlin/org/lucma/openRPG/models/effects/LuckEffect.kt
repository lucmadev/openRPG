package org.lucma.openRPG.models.effects

import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.lucma.openRPG.core.effect.EffectPriority
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Effect
import org.lucma.openRPG.models.types.StackType

class LuckEffect(
    val duration: Int = 30,
    val amplifier: Int = 1
) : Effect {
    override val priority = EffectPriority.CLASS
    override val stackType = StackType.ADDITIVE

    override fun apply(context: EffectContext) {
        context.player.addPotionEffect(
            PotionEffect(PotionEffectType.LUCK, duration * 20, amplifier)
        )
    }
}
