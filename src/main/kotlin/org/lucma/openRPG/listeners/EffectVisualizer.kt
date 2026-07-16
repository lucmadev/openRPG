package org.lucma.openRPG.listeners

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.lucma.openRPG.core.LanguageManager.msg
import org.lucma.openRPG.events.EffectAppliedEvent
import org.lucma.openRPG.models.effects.*
import kotlin.math.roundToInt

object EffectVisualizer : Listener {

    private val colorDamage = TextColor.color(0xFF5555)
    private val colorDefense = TextColor.color(0x55AAFF)
    private val colorSpeed = TextColor.color(0x55FF55)
    private val colorHeal = TextColor.color(0x55FF55)
    private val colorFire = TextColor.color(0xFF8800)
    private val colorSteal = TextColor.color(0xFF55FF)
    private val colorCrit = TextColor.color(0xFFAA00)

    @EventHandler
    fun onEffectApplied(event: EffectAppliedEvent) {
        try {
            val p = event.player
            val effect = event.modifier.effect
            val bonus = when (effect) {
                is DamageBonusEffect -> formatPct(msg("visualizer.damage", p), effect.bonus, colorDamage)
                is DefenseBonusEffect -> formatPct(msg("visualizer.defense", p), effect.bonus, colorDefense)
                is SpeedBonusEffect -> formatPct(msg("visualizer.speed", p), effect.bonus, colorSpeed)
                is HealEffect -> formatFixed(msg("visualizer.heal", p), effect.amount, colorHeal)
                is LifeStealEffect -> formatPct(msg("visualizer.life_steal", p), effect.stealPercentage, colorSteal)
                is FireAuraEffect -> formatFixed(msg("visualizer.fire_aura", p), effect.duration.toDouble(), colorFire)
                is CriticalChanceEffect -> formatPct(msg("visualizer.crit_chance", p), effect.chance, colorCrit)
                is CriticalDamageEffect -> formatPct(msg("visualizer.crit_damage", p), effect.bonus, colorCrit)
                else -> {
                    Bukkit.getLogger().fine("[openRPG] Unknown effect: ${effect::class.simpleName}")
                    return
                }
            }
            event.player.sendActionBar(bonus)
        } catch (ex: Exception) {
            Bukkit.getLogger().severe("[openRPG] Error in EffectVisualizer: " + ex.message)
            ex.printStackTrace()
        }
    }

    private fun formatPct(label: String, raw: Double, color: TextColor): Component {
        val pct = ((raw) * 100).roundToInt()
        val signo = if (raw >= 0) "+" else ""
        return Component.text(" $label ")
            .color(TextColor.color(0xAAAAAA))
            .append(Component.text("$signo${pct}%").color(color))
    }

    private fun formatFixed(label: String, value: Double, color: TextColor): Component {
        return Component.text(" $label ")
            .color(TextColor.color(0xAAAAAA))
            .append(Component.text("$value").color(color))
    }
}
