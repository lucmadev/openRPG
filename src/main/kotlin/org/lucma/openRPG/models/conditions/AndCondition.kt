package org.lucma.openRPG.models.conditions

import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition

/**
 * Solo se activa si TODAS las condiciones internas coinciden.
 */
class AndCondition(
    private val conditions: List<Condition>
) : Condition {
    override fun matches(context: EffectContext): Boolean {
        return conditions.all { it.matches(context) }
    }
}
