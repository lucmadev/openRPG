package org.lucma.openRPG.models.conditions

import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition

/**
 * Invierte el resultado de una condición interna.
 */
class NotCondition(
    private val condition: Condition
) : Condition {
    override fun matches(context: EffectContext): Boolean {
        return !condition.matches(context)
    }
}
