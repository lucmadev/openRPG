package org.lucma.openRPG.models.conditions

import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition

class DayTimeCondition : Condition {
    override fun matches(context: EffectContext): Boolean {
        val time = context.player.world.time
        return time < 13000L || time > 23000L
    }
}
