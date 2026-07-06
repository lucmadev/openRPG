package org.lucma.openRPG.core.condition

import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.data.Modifier

object ConditionEngine {
        fun matches(modifier: Modifier, context: EffectContext): Boolean {
            return modifier.condition.matches(context)
        }
}