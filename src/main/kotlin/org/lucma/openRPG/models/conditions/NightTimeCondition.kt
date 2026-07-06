package org.lucma.openRPG.models.conditions

import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition

/**
 * Se activa cuando es de noche en el mundo del jugador.
 * Tiempo de Minecraft: 13000 = anochecer, 23000 = amanecer.
 */
class NightTimeCondition : Condition {

    override fun matches(context: EffectContext): Boolean {
        val time = context.player.world.time
        return time in 13000L..23000L
    }
}
