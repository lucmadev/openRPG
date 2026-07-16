package org.lucma.openRPG.models.conditions

import org.lucma.openRPG.managers.PlayerDataManager
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition

class HasTalentCondition(
    private val nodeId: String
) : Condition {
    override fun matches(context: EffectContext): Boolean {
        val data = PlayerDataManager.get(context.player) ?: return false
        return nodeId in data.unlockedNodes
    }
}
