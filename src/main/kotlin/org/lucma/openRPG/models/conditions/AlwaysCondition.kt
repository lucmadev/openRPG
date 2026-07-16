package org.lucma.openRPG.models.conditions

import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition

/**
 * Condición que siempre se cumple.
 * Útil para buffos permanentes.
 */
class AlwaysCondition : Condition {
    override fun matches(context: EffectContext): Boolean = true
}
