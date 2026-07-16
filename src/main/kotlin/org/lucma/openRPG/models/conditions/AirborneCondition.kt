package org.lucma.openRPG.models.conditions

import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition

class AirborneCondition : Condition {
    override fun matches(context: EffectContext): Boolean {
        return !context.player.isOnGround() && !context.player.isInWater() && !context.player.isClimbing()
    }
}
