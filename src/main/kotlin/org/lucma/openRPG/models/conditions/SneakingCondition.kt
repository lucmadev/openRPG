package org.lucma.openRPG.models.conditions

import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition

/**
 * Se activa cuando el jugador está agachado (sneaking).
 */
class SneakingCondition : Condition {

    override fun matches(context: EffectContext): Boolean {
        return context.player.isSneaking
    }
}
