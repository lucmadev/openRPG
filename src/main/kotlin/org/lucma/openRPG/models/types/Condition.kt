package org.lucma.openRPG.models.types

import org.lucma.openRPG.models.data.EffectContext

interface Condition {
    fun matches(context: EffectContext): Boolean
}