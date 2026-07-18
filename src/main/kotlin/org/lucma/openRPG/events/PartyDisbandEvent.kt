package org.lucma.openRPG.events

import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.lucma.openRPG.models.party.Party

class PartyDisbandEvent(
    val party: Party
) : Event() {

    override fun getHandlers(): HandlerList = handlerList

    companion object {
        private val handlerList = HandlerList()
        @JvmStatic
        fun getHandlerList(): HandlerList = handlerList
    }
}
