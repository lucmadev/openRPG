package org.lucma.openRPG.models.conditions

import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition

class WearingFullArmorCondition : Condition {
    override fun matches(context: EffectContext): Boolean {
        val armor = context.player.inventory.armorContents ?: return false
        return armor.all { it != null && !it.type.name.endsWith("AIR") }
    }
}
