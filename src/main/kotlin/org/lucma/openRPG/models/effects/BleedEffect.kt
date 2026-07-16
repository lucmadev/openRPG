package org.lucma.openRPG.models.effects

import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.lucma.openRPG.core.effect.EffectPriority
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Effect
import org.lucma.openRPG.models.types.StackType

/**
 * Aplica daño en el tiempo (bleed) al objetivo.
 * @param duration segundos de sangrado
 * @param damagePerTick daño por tick (20 ticks/segundo)
 */
class BleedEffect(
    val duration: Int = 3,
    val damagePerTick: Double = 0.5
) : Effect {
    override val priority = EffectPriority.CLASS
    override val stackType = StackType.ADDITIVE

    override fun apply(context: EffectContext) {
        val event = context.event
        if (event !is EntityDamageByEntityEvent) return
        val target = event.entity as? LivingEntity ?: return

        val totalTicks = duration * 20
        val delayPerTick = 20 / 4 // 4 veces por segundo
        var ticksApplied = 0

        // Aplicar daño diferido mediante task programado
        val plugin = org.bukkit.Bukkit.getPluginManager().getPlugin("openRPG") ?: return
        val task = org.bukkit.Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            if (ticksApplied < totalTicks && !target.isDead) {
                target.damage(damagePerTick, context.player)
                ticksApplied += delayPerTick
            }
        }, 0L, delayPerTick.toLong())

        // Cancelar después de la duración
        org.bukkit.Bukkit.getScheduler().runTaskLater(plugin, object : Runnable {
            override fun run() {
                task.cancel()
            }
        }, totalTicks.toLong())
    }
}
