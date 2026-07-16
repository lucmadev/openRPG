package org.lucma.openRPG.models.effects

import org.bukkit.Material
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.lucma.openRPG.core.effect.EffectPriority
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Effect
import org.lucma.openRPG.models.types.StackType

/**
 * Coloca telarañas alrededor del objetivo.
 * @param radius radio de telarañas
 * @param duration segundos antes de romperse (0 = permanente)
 */
class WebEffect(
    val radius: Int = 2,
    val duration: Int = 3
) : Effect {
    override val priority = EffectPriority.CLASS
    override val stackType = StackType.ADDITIVE

    override fun apply(context: EffectContext) {
        val event = context.event
        if (event !is EntityDamageByEntityEvent) return
        val target = event.entity
        val loc = target.location

        val blocks = mutableListOf<org.bukkit.block.Block>()
        for (dx in -radius..radius) {
            for (dz in -radius..radius) {
                val block = loc.clone().add(dx.toDouble(), 0.0, dz.toDouble()).block
                if (block.type == Material.AIR) {
                    block.type = Material.COBWEB
                    blocks.add(block)
                }
            }
        }

        if (duration > 0 && blocks.isNotEmpty()) {
            val plugin = org.bukkit.Bukkit.getPluginManager().getPlugin("openRPG") ?: return
            org.bukkit.Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                blocks.forEach { it.type = Material.AIR }
            }, (duration * 20).toLong())
        }
    }
}
