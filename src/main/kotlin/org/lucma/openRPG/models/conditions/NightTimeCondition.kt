package org.lucma.openRPG.models.conditions

import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition

/**
 * Activates when it is night in the player's world.
 * Minecraft time: 13000 = dusk, 23000 = dawn.
 */
class NightTimeCondition : Condition {

    override fun matches(context: EffectContext): Boolean {
        val time = context.player.world.time
        return time in 13000L..23000L
    }
}
