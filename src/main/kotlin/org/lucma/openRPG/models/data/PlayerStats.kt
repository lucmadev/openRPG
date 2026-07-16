package org.lucma.openRPG.models.data

data class PlayerStats(
    // ── Existentes ──
    var damageMultiplier: Double = 1.0,
    var speedMultiplier: Double = 1.0,
    var defenseMultiplier: Double = 1.0,
    var critChance: Double = 0.0,
    var critMultiplier: Double = 1.0,

    // ── Combate ──
    var knockbackMultiplier: Double = 1.0,
    var thornsChance: Double = 0.0,
    var thornsDamage: Double = 1.0,
    var dodgeChance: Double = 0.0,
    var damageReduction: Double = 0.0,
    var damageReflect: Double = 0.0,
    var lifestealMultiplier: Double = 1.0,
    var attackSpeedMultiplier: Double = 1.0,

    // ── Defensa absorbente ──
    var shieldHealth: Double = 0.0,

    // ── Recursos ──
    var luckMultiplier: Double = 1.0,
    var expMultiplier: Double = 1.0,
    var lootMultiplier: Double = 1.0,
    var miningSpeedMultiplier: Double = 1.0,
    var healthRegenMultiplier: Double = 1.0,
    var jumpMultiplier: Double = 1.0,
    var manaRegen: Double = 0.0,
    var staminaRegen: Double = 0.0
)
