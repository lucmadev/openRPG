package org.lucma.openRPG.models.conditions

import org.lucma.openRPG.managers.PlayerClassManager
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition

class HasClassCondition(
    private val classId: String = ""
) : Condition {
    override fun matches(context: EffectContext): Boolean {
        val playerClass = PlayerClassManager.getPlayerClass(context.player) ?: return false
        if (classId.isBlank()) return true
        return playerClass.id.equals(classId, ignoreCase = true)
    }
}
