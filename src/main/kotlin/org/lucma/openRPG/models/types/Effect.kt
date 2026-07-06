package org.lucma.openRPG.models.types

import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.core.effect.EffectPriority

interface Effect {
    val priority: EffectPriority
    val stackType: StackType
    fun apply(context: EffectContext)
}