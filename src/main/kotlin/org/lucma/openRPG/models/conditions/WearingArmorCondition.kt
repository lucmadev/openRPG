package org.lucma.openRPG.models.conditions

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition

/**
 * Se activa cuando el jugador usa cierta armadura.
 * @param material tipo de armadura (ej: "DIAMOND", "IRON"). Vacío = cualquier armadura.
 */
class WearingArmorCondition(
    private val material: String = ""
) : Condition {
    override fun matches(context: EffectContext): Boolean {
        val armor = context.player.inventory.armorContents ?: return false
        return armor.any { it != null && matchesArmor(it) }
    }

    private fun matchesArmor(item: ItemStack): Boolean {
        if (material.isBlank()) return true
        return item.type.name.contains(material.uppercase(), ignoreCase = true)
    }
}
