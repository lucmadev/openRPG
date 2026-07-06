package org.lucma.openRPG.listeners

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.lucma.openRPG.managers.PlayerClassManager
import org.lucma.openRPG.managers.PlayerDataManager

object ExpListener : Listener {

    private val colorExp = TextColor.color(0x55FF55)
    private val colorLevel = TextColor.color(0xFFAA00)

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        try {
            val killer = event.entity.killer ?: return

            // Sin clase = no gana EXP
            val clazz = PlayerClassManager.getPlayerClass(killer)
            if (clazz == null) return

            val exp = calculateExp(event)
            if (exp <= 0) return

            Bukkit.getLogger().fine("[openRPG] EXP: " + killer.getName() + " +" + exp + " por " + event.entity.type.name)

            val leveledUp = PlayerDataManager.addExp(killer, exp)
            val data = PlayerDataManager.getOrCreate(killer)

            if (leveledUp) {
                killer.sendMessage(
                    Component.text(" §6§l¡SUBISTE A NIVEL " + data.level + "!")
                        .color(colorLevel)
                        .append(Component.text(" §7(Tienes §e" + data.talentPoints + "§7 puntos de talento)")
                            .color(TextColor.color(0xAAAAAA)))
                )
            } else {
                killer.sendActionBar(
                    Component.text(" +" + exp + " EXP")
                        .color(colorExp)
                        .append(Component.text(" §8[§7" + data.exp + "§8/§7" + data.expToNextLevel + "§8]")
                            .color(TextColor.color(0x888888)))
                )
            }

        } catch (ex: Exception) {
            Bukkit.getLogger().severe("[openRPG] Error en ExpListener: " + ex.message)
            ex.printStackTrace()
        }
    }

    private fun calculateExp(event: EntityDeathEvent): Int {
        val entity = event.entity
        val maxHealth = entity.getAttribute(Attribute.MAX_HEALTH)?.value ?: 20.0

        return when {
            maxHealth >= 200 -> 50   // bosses
            maxHealth >= 80  -> 25   // mobs fuertes
            maxHealth >= 40  -> 15   // mobs medios
            maxHealth >= 10  -> 8    // mobs básicos
            else -> 3                // criaturas pasivas
        }
    }
}
