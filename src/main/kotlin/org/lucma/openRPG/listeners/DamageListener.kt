package org.lucma.openRPG.listeners

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.lucma.openRPG.core.EventDispatcher
import org.lucma.openRPG.core.LanguageManager.msg
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.data.PlayerStats
import kotlin.math.roundToInt
import kotlin.random.Random

class DamageListener : Listener {

    private val serializer = PlainTextComponentSerializer.plainText()

    @EventHandler
    fun onDamage(event: EntityDamageByEntityEvent) {
        try {
            val player = event.damager as? Player ?: return

            val baseDmg = event.damage
            Bukkit.getLogger().fine("[openRPG] DamageEvent | damager=" + player.getName() + " | base=" + baseDmg)

            val context = EffectContext(player, event, PlayerStats())
            EventDispatcher.dispatch(context)

            event.damage *= context.stats.damageMultiplier

            var wasCrit = false
            if (context.stats.critChance > 0.0 && Random.nextDouble() < context.stats.critChance) {
                event.damage *= context.stats.critMultiplier
                wasCrit = true
            }

            val entityName = serializer.serialize(
                event.entity.customName() ?: event.entity.name()
            )
            val finalDamage = event.damage
            val mult = context.stats.damageMultiplier

            val bar = Component.text(" $entityName ")
                .color(TextColor.color(0xAAAAAA))
                .append(
                    Component.text("❤ " + finalDamage.roundToInt())
                        .color(if (wasCrit) TextColor.color(0xFFAA00) else TextColor.color(0xFF5555))
                )
                .append(
                    when {
                        wasCrit -> Component.text(" " + msg("damage.critical"))
                            .color(TextColor.color(0xFFAA00))
                        mult != 1.0 -> Component.text(" (" + msg("damage.format", player, String.format("%.2f", mult)) + ")")
                            .color(TextColor.color(0xFFAA55))
                        else -> Component.empty()
                    }
                )

            if (wasCrit && context.stats.critMultiplier != 1.0) {
                bar.append(
                    Component.text(" (" + msg("damage.format", player, String.format("%.1f", context.stats.critMultiplier)) + ")")
                        .color(TextColor.color(0xFFAA00))
                )
            }

            player.sendActionBar(bar)

        } catch (ex: Exception) {
            Bukkit.getLogger().severe("[openRPG] Error en DamageListener: " + ex.message)
            ex.printStackTrace()
        }
    }
}
