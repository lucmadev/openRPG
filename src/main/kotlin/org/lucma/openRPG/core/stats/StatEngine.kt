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
     * Aplica un modificador registrado en [StatRegistry] por su ID.
     * Útil para efectos dinámicos basados en configuración.
     */
    fun apply(
        id: String,
        stats: PlayerStats,
        config: Map<String, Any>
    ): Boolean {
        return StatRegistry.apply(id, stats, config)
    }

}
