package org.lucma.openRPG.listeners

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.lucma.openRPG.core.LanguageManager.msg
import org.lucma.openRPG.managers.PlayerClassManager
import org.lucma.openRPG.managers.PlayerDataManager

object ExpListener : Listener {

    private val colorExp = TextColor.color(0x55FF55)
    private val colorLevel = TextColor.color(0xFFAA00)

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        try {
            val killer = event.entity.killer ?: return
            val clazz = PlayerClassManager.getPlayerClass(killer)
            if (clazz == null) return

            val exp = calculateExp(event)
            if (exp <= 0) return

            val leveledUp = PlayerDataManager.addExp(killer, exp)
            val data = PlayerDataManager.getOrCreate(killer)

            if (leveledUp) {
                killer.sendMessage(
                    Component.text(msg("exp.level_up", killer, data.level))
                        .color(colorLevel)
                        .append(Component.text(" " + msg("exp.talent_points_hint", killer, data.talentPoints))
                            .color(TextColor.color(0xAAAAAA)))
                )
            } else {
                killer.sendActionBar(
                    Component.text(msg("exp.gained", killer, exp))
                        .color(colorExp)
                        .append(Component.text(" " + msg("exp.bar_format", killer, data.exp, data.expToNextLevel))
                            .color(TextColor.color(0x888888)))
                )
            }

        } catch (ex: Exception) {
            Bukkit.getLogger().severe("[openRPG] Error en ExpListener: " + ex.message)
            ex.printStackTrace()
        }
    }

    private fun calculateExp(event: EntityDeathEvent): Int {
        val maxHealth = event.entity.getAttribute(Attribute.MAX_HEALTH)?.value ?: 20.0
        return when {
            maxHealth >= 200 -> 50
            maxHealth >= 80  -> 25
            maxHealth >= 40  -> 15
            maxHealth >= 10  -> 8
            else -> 3
        }
    }
}
