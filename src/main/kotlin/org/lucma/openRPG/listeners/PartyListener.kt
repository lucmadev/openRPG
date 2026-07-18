package org.lucma.openRPG.listeners

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.lucma.openRPG.core.PartyManager
import org.lucma.openRPG.events.PartyLeaderChangeEvent
import org.lucma.openRPG.events.PartyLeaveEvent
import org.lucma.openRPG.models.party.LeaveReason

class PartyListener : Listener {

    private var cleanupTaskId: Int? = null

    fun startCleanupTask() {
        val plugin = Bukkit.getPluginManager().getPlugin("openRPG") ?: return
        cleanupTaskId = Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            PartyManager.cleanupInvites()
        }, 200L, 100L).taskId // every 5 seconds, starting after 10s
    }

    fun stopCleanupTask() {
        cleanupTaskId?.let { Bukkit.getScheduler().cancelTask(it) }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player: Player = event.player
        val party = PartyManager.getParty(player) ?: return

        val wasLeader = party.isLeader(player)
        val oldLeader = party.leader

        // Remove the player from party
        PartyManager.handleDisconnect(player)

        // Fire leave event
        Bukkit.getPluginManager().callEvent(
            PartyLeaveEvent(party, player, LeaveReason.DISCONNECTED)
        )

        if (party.size == 0) return

        // Notify remaining members
        party.members.forEach { member ->
            member.sendMessage(
                org.lucma.openRPG.core.LanguageManager.msg("party.leave.disconnected", player.name)
            )
        }

        // If leader disconnected, transfer leadership
        if (wasLeader) {
            val newLeader = party.leader
            Bukkit.getPluginManager().callEvent(
                PartyLeaderChangeEvent(party, oldLeader, newLeader)
            )
            newLeader.sendMessage(
                org.lucma.openRPG.core.LanguageManager.msg("party.transfer.you_are_leader")
            )
        }
    }
}
