package org.lucma.openRPG.events

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.lucma.openRPG.models.party.LeaveReason
import org.lucma.openRPG.models.party.Party

class PartyLeaveEvent(
    val party: Party,
    val player: Player,
    val reason: LeaveReason
) : Event() {

    override fun getHandlers(): HandlerList = handlerList

    companion object {
        private val handlerList = HandlerList()
        @JvmStatic
        fun getHandlerList(): HandlerList = handlerList
    }
}
