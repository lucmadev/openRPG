package org.lucma.openRPG.core.stats

import org.lucma.openRPG.models.data.PlayerStats
import org.lucma.openRPG.core.registry.StatRegistry

object StatEngine {

    fun applyDamageMultiplier(stats: PlayerStats, multiplier: Double) {
        stats.damageMultiplier *= multiplier
    }

    fun applySpeedMultiplier(stats: PlayerStats, multiplier: Double) {
        stats.speedMultiplier *= multiplier
    }

    fun applyDefenseMultiplier(stats: PlayerStats, multiplier: Double) {
        stats.defenseMultiplier *= multiplier
    }

    fun applyKnockbackMultiplier(stats: PlayerStats, multiplier: Double) {
        stats.knockbackMultiplier *= multiplier
    }

    fun applyExpMultiplier(stats: PlayerStats, multiplier: Double) {
        stats.expMultiplier *= multiplier
    }

    fun applyLootMultiplier(stats: PlayerStats, multiplier: Double) {
        stats.lootMultiplier *= multiplier
    }

    fun applyMiningSpeedMultiplier(stats: PlayerStats, multiplier: Double) {
        stats.miningSpeedMultiplier *= multiplier
    }

    fun applyJumpMultiplier(stats: PlayerStats, multiplier: Double) {
        stats.jumpMultiplier *= multiplier
    }

    fun applyHealthRegenMultiplier(stats: PlayerStats, multiplier: Double) {
        stats.healthRegenMultiplier *= multiplier
    }

    fun applyAttackSpeedMultiplier(stats: PlayerStats, multiplier: Double) {
        stats.attackSpeedMultiplier *= multiplier
    }

    /**
     * Aplica un modificador registrado en [StatRegistry] por su ID.
     * Útil para efectos dinámicos basados en configuración.
     */
    fun apply(id: String, stats: PlayerStats, config: Map<String, Any>): Boolean {
        return StatRegistry.apply(id, stats, config)
    }

}
