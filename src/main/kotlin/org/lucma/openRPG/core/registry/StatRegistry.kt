package org.lucma.openRPG.core.registry

import org.lucma.openRPG.models.data.PlayerStats

object StatRegistry {

    private val modifiers =
        mutableMapOf<String, (PlayerStats, Map<String, Any>) -> Unit>()

    fun register(
        id: String,
        applicator: (PlayerStats, Map<String, Any>) -> Unit
    ) {
        modifiers[id] = applicator
    }

    fun apply(
        id: String,
        stats: PlayerStats,
        config: Map<String, Any>
    ): Boolean {
        val applicator = modifiers[id] ?: return false
        applicator(stats, config)
        return true
    }

    fun registerDefaults() {
        register("damage_multiplier") { stats, config ->
            val value = (config["value"] as? Number)?.toDouble() ?: 1.0
            stats.damageMultiplier *= value
        }
        register("defense_multiplier") { stats, config ->
            val value = (config["value"] as? Number)?.toDouble() ?: 1.0
            stats.defenseMultiplier *= value
        }
        register("speed_multiplier") { stats, config ->
            val value = (config["value"] as? Number)?.toDouble() ?: 1.0
            stats.speedMultiplier *= value
        }
        register("crit_chance") { stats, config ->
            val value = (config["value"] as? Number)?.toDouble() ?: 0.0
            stats.critChance += value
        }
        register("crit_multiplier") { stats, config ->
            val value = (config["value"] as? Number)?.toDouble() ?: 0.0
            stats.critMultiplier += value
        }
    }

}
