package org.lucma.openRPG.models.conditions

import org.bukkit.Material
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition

/**
 * Se activa cuando el jugador sostiene un material específico.
 * @param material nombre del material (ej: "DIAMOND_SWORD"). Vacío = cualquier ítem.
 * @param hand "main" (default), "offhand" o "any"
 */
class HoldingItemCondition(
    private val material: String = "",
    private val hand: String = "main"
) : Condition {
    override fun matches(context: EffectContext): Boolean {
        val player = context.player
        val item = when (hand) {
            "offhand" -> player.inventory.itemInOffHand
            "any" -> {
                val main = player.inventory.itemInMainHand
                if (main.type != Material.AIR) return matchesItem(main.type)
                player.inventory.itemInOffHand
            }

            else -> player.inventory.itemInMainHand
        }
        if (item.type == Material.AIR) return material.isBlank()
        return matchesItem(item.type)
    }

    private fun matchesItem(type: Material): Boolean {
        if (material.isBlank()) return true
        return type.name.equals(material.uppercase(), ignoreCase = true)
    }
}
