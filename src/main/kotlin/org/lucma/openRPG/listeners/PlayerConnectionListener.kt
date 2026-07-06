package org.lucma.openRPG.listeners

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.lucma.openRPG.core.CooldownManager
import org.lucma.openRPG.managers.PlayerClassManager
import org.lucma.openRPG.managers.PlayerDataManager

object PlayerConnectionListener : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        try {
            PlayerClassManager.loadFromPDC(event.player)
            PlayerDataManager.load(event.player)
        } catch (ex: Exception) {
            Bukkit.getLogger().severe("[openRPG] Error en PlayerJoinEvent: " + ex.message)
            ex.printStackTrace()
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        try {
            val player = event.player
            val clazz = PlayerClassManager.getPlayerClass(player)

            if (clazz != null) {
                PlayerClassManager.saveToPDC(player, clazz.id)
            }

            PlayerClassManager.removeFromMemory(player)
            PlayerDataManager.unload(player)
            CooldownManager.clear(player)

        } catch (ex: Exception) {
            Bukkit.getLogger().severe("[openRPG] Error en PlayerQuitEvent: " + ex.message)
            ex.printStackTrace()
        }
    }
}
