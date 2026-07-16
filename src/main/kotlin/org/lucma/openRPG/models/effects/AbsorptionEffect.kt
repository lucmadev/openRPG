package org.lucma.openRPG.models.effects

import org.bukkit.attribute.Attribute
import org.lucma.openRPG.core.effect.EffectPriority
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Effect
import org.lucma.openRPG.models.types.StackType

/**
 * Da corazones de absorción al jugador.
 * @param hearts número de corazones de absorción (2 = 1 corazón visual)
 */
class AbsorptionEffect(val hearts: Double) : Effect {
    override val priority = EffectPriority.CLASS
    override val stackType = StackType.ADDITIVE

    override fun apply(context: EffectContext) {
        val player = context.player
        val currentAbsorption = player.getAbsorptionAmount()
        player.setAbsorptionAmount(currentAbsorption + hearts)
    }
}
