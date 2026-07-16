package org.lucma.openRPG.models.conditions

import org.lucma.openRPG.managers.PlayerDataManager
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition

/**
 * Se activa según el nivel openRPG del jugador.
 * @param minLevel nivel mínimo (inclusive)
 * @param maxLevel nivel máximo (inclusive, -1 = sin límite)
 */
class PlayerLevelCondition(
    private val minLevel: Int = 1,
    private val maxLevel: Int = -1
) : Condition {
    override fun matches(context: EffectContext): Boolean {
        val data = PlayerDataManager.get(context.player) ?: return false
        if (data.level < minLevel) return false
        if (maxLevel >= 0 && data.level > maxLevel) return false
        return true
    }
}
