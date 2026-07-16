package org.lucma.openRPG.listeners

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
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

            Bukkit.getLogger().fine("[openRPG] DamageEvent | damager=" + player.getName() + " | base=" + event.damage)

            val context = EffectContext(player, event, PlayerStats())
            EventDispatcher.dispatch(context)

            // ── Victim context (para efectos defensivos) ──
            val victim = event.entity
            val victimContext = if (victim is Player) {
                val vc = EffectContext(victim, event, PlayerStats())
                EventDispatcher.dispatch(vc)
                vc
            } else null

            // ── Dodge ──
            if (victimContext != null && victimContext.stats.dodgeChance > 0.0 && Random.nextDouble() < victimContext.stats.dodgeChance) {
                event.isCancelled = true
                victim.sendActionBar(Component.text(" §a¡ESQUIVADO!").color(TextColor.color(0x55FF55)))
                player.sendActionBar(
                    Component.text(" §cEl objetivo esquivó el ataque").color(TextColor.color(0xFF5555))
                )
                return
            }

            // ── Thorns ──
            if (victimContext != null && victimContext.stats.thornsChance > 0.0 && Random.nextDouble() < victimContext.stats.thornsChance) {
                val thornDmg = victimContext.stats.thornsDamage
                player.damage(thornDmg, victim)
                player.sendActionBar(
                    Component.text(" §cEspinas: §4" + thornDmg.roundToInt() + "").color(TextColor.color(0xFF5555))
                )
            }

            // ── Shield ──
            if (victimContext != null && victimContext.stats.shieldHealth > 0.0) {
                val shield = victimContext.stats.shieldHealth
                if (shield >= event.damage) {
                    victimContext.stats.shieldHealth -= event.damage
                    event.damage = 0.0
                } else {
                    event.damage -= shield
                    victimContext.stats.shieldHealth = 0.0
                }
            }

            // ── Damage Reduction ──
            if (victimContext != null && victimContext.stats.damageReduction > 0.0) {
                event.damage = (event.damage - victimContext.stats.damageReduction).coerceAtLeast(0.0)
            }

            // ── Damage Reflect ──
            val originalDamage = event.damage
            if (victimContext != null && victimContext.stats.damageReflect > 0.0) {
                val reflectDamage = originalDamage * victimContext.stats.damageReflect
                player.damage(reflectDamage, victim)
            }

            event.damage *= context.stats.damageMultiplier

            var wasCrit = false
            if (context.stats.critChance > 0.0 && Random.nextDouble() < context.stats.critChance) {
                event.damage *= context.stats.critMultiplier
                wasCrit = true
            }

            val entityName = serializer.serialize(
                event.entity.customName() ?: event.entity.name()
            )

            // ── Entity current / max health ──
            val entityHealth = if (event.entity is LivingEntity) {
                val living = event.entity as LivingEntity
                val cur = living.health
                val max = living.getAttribute(Attribute.MAX_HEALTH)?.value ?: 20.0
                "${cur.roundToInt()}/${max.roundToInt()}"
            } else {
                "?"
            }

            val finalDamage = event.damage
            val dmgColor = if (wasCrit) TextColor.color(0xFFAA00) else TextColor.color(0xFF5555)

            val bar = Component.text(" ")
                .append(Component.text(entityName).color(TextColor.color(0xAAAAAA)))
                .append(Component.text(": ").color(TextColor.color(0xAAAAAA)))
                .append(Component.text(entityHealth).color(TextColor.color(0xFFAA55)))
                .append(Component.text("   ").color(TextColor.color(0x888888)))
                .append(Component.text("❤ " + finalDamage.roundToInt()).color(dmgColor))

            if (wasCrit) {
                bar.append(Component.text(" " + msg("damage.critical")).color(TextColor.color(0xFFAA00)))
            }

            player.sendActionBar(bar)

        } catch (ex: Exception) {
            Bukkit.getLogger().severe("[openRPG] Error in DamageListener: " + ex.message)
            ex.printStackTrace()
        }
    }
}
