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
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.data.PlayerStats
import kotlin.math.roundToInt
import kotlin.random.Random

class DamageListener : Listener {

    private val serializer = PlainTextComponentSerializer.plainText()

    @EventHandler
    fun onDamage(event: EntityDamageByEntityEvent) {
        try {
            val player = event.damager as? Player
            if (player == null) {
                Bukkit.getLogger().fine("[openRPG] damager no es un Player: " + event.damager)
                return
            }

            val playerName = serializer.serialize(player.name())
            val baseDmg = event.damage
            val entType = event.entity.type.name
            Bukkit.getLogger().fine("[openRPG] DamageEvent | damager=" + playerName + " | entidad=" + entType + " | base=" + baseDmg)

            val context = EffectContext(
                player = player,
                event = event,
                stats = PlayerStats()
            )

            EventDispatcher.dispatch(context)

            // ── Multiplicador de daño base ──
            event.damage *= context.stats.damageMultiplier

            // ── Golpe crítico ──
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

            Bukkit.getLogger().fine("[openRPG] DamageResult | entidad=" + entityName + " | final=" + finalDamage + " | mult=" + mult + " | crit=" + wasCrit)

            // ── Action Bar ──
            val bar = Component.text(" $entityName ")
                .color(TextColor.color(0xAAAAAA))
                .append(
                    Component.text("❤ " + finalDamage.roundToInt())
                        .color(if (wasCrit) TextColor.color(0xFFAA00) else TextColor.color(0xFF5555))
                )
                .append(
                    when {
                        wasCrit -> Component.text(" ✦CRÍTICO✦")
                            .color(TextColor.color(0xFFAA00))
                        mult != 1.0 -> Component.text(" (x" + String.format("%.2f", mult) + ")")
                            .color(TextColor.color(0xFFAA55))
                        else -> Component.empty()
                    }
                )

            if (wasCrit && context.stats.critMultiplier != 1.0) {
                val critMult = context.stats.critMultiplier
                bar.append(
                    Component.text(" (x" + String.format("%.1f", critMult) + ")")
                        .color(TextColor.color(0xFFAA00))
                )
            }

            player.sendActionBar(bar)

        } catch (ex: Exception) {
            Bukkit.getLogger().severe("[openRPG] Error en DamageListener: " + ex.javaClass.simpleName + ": " + ex.message)
            ex.printStackTrace()
        }
    }
}
