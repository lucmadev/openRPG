package org.lucma.openRPG.listeners

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.lucma.openRPG.core.LanguageManager.msg
import org.lucma.openRPG.core.PartyManager
import org.lucma.openRPG.managers.PlayerClassManager
import org.lucma.openRPG.managers.PlayerDataManager

object ExpListener : Listener {

    private val colorExp = TextColor.color(0x55FF55)
    private val colorLevel = TextColor.color(0xFFAA00)
    private val colorPartyExp = TextColor.color(0x55AAFF)

    private const val PARTY_EXP_RADIUS = 50.0

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        try {
            val killer = event.entity.killer ?: return
            val clazz = PlayerClassManager.getPlayerClass(killer)
            if (clazz == null) return

            val exp = calculateExp(event)
            if (exp <= 0) return

            // Grant EXP to the killer
            grantExp(killer, exp, false)

            // Grant EXP to all nearby party members
            val party = PartyManager.getParty(killer)
            if (party != null) {
                val killerLoc = killer.location
                for (member in party.members) {
                    if (member.uniqueId == killer.uniqueId) continue
                    if (!member.isOnline) continue

                    val distance = member.location.distance(killerLoc)
                    if (distance > PARTY_EXP_RADIUS) continue

                    grantExp(member, exp, true)
                }
            }

        } catch (ex: Exception) {
            Bukkit.getLogger().severe("[openRPG] Error in ExpListener: " + ex.message)
            ex.printStackTrace()
        }
    }

    private fun grantExp(player: org.bukkit.entity.Player, exp: Int, isPartyShare: Boolean) {
        val leveledUp = PlayerDataManager.addExp(player, exp)
        val data = PlayerDataManager.getOrCreate(player)

        if (leveledUp) {
            player.sendMessage(
                Component.text(msg("exp.level_up", player, data.level))
                    .color(colorLevel)
                    .append(
                        Component.text(" " + msg("exp.talent_points_hint", player, data.talentPoints))
                            .color(TextColor.color(0xAAAAAA))
                    )
            )
        } else if (isPartyShare) {
            player.sendActionBar(
                Component.text(msg("exp.party_share", player, exp))
                    .color(colorPartyExp)
            )
        } else {
            player.sendActionBar(
                Component.text(msg("exp.gained", player, exp))
                    .color(colorExp)
                    .append(
                        Component.text(" " + msg("exp.bar_format", player, data.exp, data.expToNextLevel))
                            .color(TextColor.color(0x888888))
                    )
            )
        }
    }

    private fun calculateExp(event: EntityDeathEvent): Int {
        val maxHealth = event.entity.getAttribute(Attribute.MAX_HEALTH)?.value ?: 20.0
        return when {
            maxHealth >= 200 -> 50
            maxHealth >= 80 -> 25
            maxHealth >= 40 -> 15
            maxHealth >= 10 -> 8
            else -> 3
        }
    }
}
