package org.lucma.openRPG.events

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.data.Modifier

/**
 * Fired after a [Modifier] (condition + effect) is applied
 * to a player. Other plugins can listen to it to:
 *
 * - Show particles or play sounds
 * - Track applied effect statistics
 * - Integrate with PlaceholderAPI
 * - Implement achievements or quests
 * - Add secondary visual effects
 */
class EffectAppliedEvent(
    val player: Player,
    val modifier: Modifier,
    val context: EffectContext
) : Event() {

    override fun getHandlers(): HandlerList = handlerList

    companion object {
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = handlerList
    }
}
