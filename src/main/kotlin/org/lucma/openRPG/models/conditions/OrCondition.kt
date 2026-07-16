package org.lucma.openRPG.models.conditions

import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition

/**
 * Se activa si AL MENOS UNA de las condiciones internas coincide.
 */
class OrCondition(
    private val conditions: List<Condition>
) : Condition {
    override fun matches(context: EffectContext): Boolean {
        return conditions.any { it.matches(context) }
    }
}
