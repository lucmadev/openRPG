package org.lucma.openRPG.models.data

data class PlayerStats(
    var damageMultiplier: Double = 1.0,
    var speedMultiplier: Double = 1.0,
    var defenseMultiplier: Double = 1.0,
    var critChance: Double = 0.0,
    var critMultiplier: Double = 1.0
)
