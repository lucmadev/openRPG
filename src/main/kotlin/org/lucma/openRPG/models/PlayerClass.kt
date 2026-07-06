package org.lucma.openRPG.models

import org.lucma.openRPG.models.data.Modifier

abstract class PlayerClass {
    abstract val id: String
    abstract val name: String
    abstract val modifiers: List<Modifier>
}