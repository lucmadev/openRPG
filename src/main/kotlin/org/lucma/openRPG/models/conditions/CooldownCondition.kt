package org.lucma.openRPG.models.conditions

import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition
import org.lucma.openRPG.core.CooldownManager

/**
 * Solo se activa si ha pasado el cooldown desde la última activación.
 * @param key clave única del cooldown
 * @param seconds segundos de cooldown
 */
class CooldownCondition(
    private val key: String,
    private val seconds: Int
) : Condition {
    override fun matches(context: EffectContext): Boolean {
        if (CooldownManager.isOnCooldown(context.player, key)) return false
        CooldownManager.set(context.player, key, seconds)
        return true
    }
}
