package org.lucma.openRPG.listeners

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.lucma.openRPG.events.EffectAppliedEvent
import org.lucma.openRPG.models.effects.CriticalChanceEffect
import org.lucma.openRPG.models.effects.CriticalDamageEffect
import org.lucma.openRPG.models.effects.DamageBonusEffect
import org.lucma.openRPG.models.effects.DefenseBonusEffect
import org.lucma.openRPG.models.effects.FireAuraEffect
import org.lucma.openRPG.models.effects.HealEffect
import org.lucma.openRPG.models.effects.LifeStealEffect
import org.lucma.openRPG.models.effects.SpeedBonusEffect
import kotlin.math.roundToInt

object EffectVisualizer : Listener {

    private val colorDamage = TextColor.color(0xFF5555)
    private val colorDefense = TextColor.color(0x55AAFF)
    private val colorSpeed = TextColor.color(0x55FF55)
    private val colorHeal = TextColor.color(0x55FF55)
    private val colorFire = TextColor.color(0xFF8800)
    private val colorSteal = TextColor.color(0xFF55FF)
    private val colorCritChance = TextColor.color(0xFFAA00)
    private val colorCritDmg = TextColor.color(0xFFAA00)

    @EventHandler
    fun onEffectApplied(event: EffectAppliedEvent) {
        try {
            val effect = event.modifier.effect
            val bonus = when (effect) {
                is DamageBonusEffect -> formatBonus("⚔ Daño", effect.bonus, colorDamage)
                is DefenseBonusEffect -> formatBonus("🛡 Defensa", effect.bonus, colorDefense)
                is SpeedBonusEffect -> formatBonus("💨 Velocidad", effect.bonus, colorSpeed)
                is HealEffect -> formatFixed("❤ Curación", effect.amount, colorHeal)
                is LifeStealEffect -> formatPct("🩸 Robo vida", effect.stealPercentage, colorSteal)
                is FireAuraEffect -> formatFixed("🔥 Aura ígnea", effect.duration.toDouble(), colorFire)
                is CriticalChanceEffect -> formatPct("✦ Prob. crítico", effect.chance, colorCritChance)
                is CriticalDamageEffect -> formatBonus("✦ Daño crítico", effect.bonus, colorCritDmg)
                else -> {
                    Bukkit.getLogger().fine("[openRPG] Efecto desconocido en visualizer: ${effect::class.simpleName}")
                    return
                }
            }

            event.player.sendActionBar(bonus)

        } catch (ex: Exception) {
            Bukkit.getLogger().severe("[openRPG] Error en EffectVisualizer: " + ex.javaClass.simpleName + ": " + ex.message)
            ex.printStackTrace()
        }
    }

    private fun formatBonus(label: String, raw: Double, color: TextColor): Component {
        val pct = ((raw) * 100).roundToInt()
        val signo = if (raw >= 0) "+" else ""
        return Component.text(" $label ")
            .color(TextColor.color(0xAAAAAA))
            .append(
                Component.text("$signo${pct}%")
                    .color(color)
            )
    }

    private fun formatPct(label: String, raw: Double, color: TextColor): Component {
        val pct = ((raw) * 100).roundToInt()
        return Component.text(" $label ")
            .color(TextColor.color(0xAAAAAA))
            .append(
                Component.text("${pct}%")
                    .color(color)
            )
    }

    private fun formatFixed(label: String, value: Double, color: TextColor): Component {
        return Component.text(" $label ")
            .color(TextColor.color(0xAAAAAA))
            .append(
                Component.text("${value}")
                    .color(color)
            )
    }
}
