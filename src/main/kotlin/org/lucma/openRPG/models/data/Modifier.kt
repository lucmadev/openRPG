package org.lucma.openRPG.models.data

import org.lucma.openRPG.models.types.Condition
import org.lucma.openRPG.models.types.Effect

data class Modifier(
    val condition: Condition,
    val effect: Effect

)