package org.lucma.openRPG.core.stats

import org.lucma.openRPG.models.data.PlayerStats
import org.lucma.openRPG.core.registry.StatRegistry

object StatEngine {

    fun applyDamageMultiplier(
        stats: PlayerStats,
        multiplier: Double
    ) {
        stats.damageMultiplier *= multiplier
    }

    fun applySpeedMultiplier(
        stats: PlayerStats,
        multiplier: Double
    ) {
        stats.speedMultiplier *= multiplier
    }

    fun applyDefenseMultiplier(
        stats: PlayerStats,
        multiplier: Double
    ) {
        stats.defenseMultiplier *= multiplier
    }

    /**
     * Apply a modifier registered in [StatRegistry] by its ID.
     * Useful for dynamic config-based effects.
     */
    fun apply(
        id: String,
        stats: PlayerStats,
        config: Map<String, Any>
    ): Boolean {
        return StatRegistry.apply(id, stats, config)
    }

}
