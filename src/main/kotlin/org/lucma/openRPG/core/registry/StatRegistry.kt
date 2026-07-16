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
        // ── Existentes ──
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

        // ── Nuevas ──
        register("knockback_multiplier") { stats, config ->
            val value = (config["value"] as? Number)?.toDouble() ?: 1.0
            stats.knockbackMultiplier *= value
        }
        register("thorns") { stats, config ->
            val chance = (config["chance"] as? Number)?.toDouble() ?: 0.0
            val damage = (config["damage"] as? Number)?.toDouble() ?: 1.0
            stats.thornsChance += chance
            stats.thornsDamage += damage
        }
        register("dodge_chance") { stats, config ->
            val value = (config["value"] as? Number)?.toDouble() ?: 0.0
            stats.dodgeChance += value
        }
        register("damage_reduction") { stats, config ->
            val value = (config["value"] as? Number)?.toDouble() ?: 0.0
            stats.damageReduction += value
        }
        register("damage_reflect") { stats, config ->
            val value = (config["value"] as? Number)?.toDouble() ?: 0.0
            stats.damageReflect += value
        }
        register("exp_multiplier") { stats, config ->
            val value = (config["value"] as? Number)?.toDouble() ?: 1.0
            stats.expMultiplier *= value
        }
        register("loot_multiplier") { stats, config ->
            val value = (config["value"] as? Number)?.toDouble() ?: 1.0
            stats.lootMultiplier *= value
        }
        register("mining_speed") { stats, config ->
            val value = (config["value"] as? Number)?.toDouble() ?: 1.0
            stats.miningSpeedMultiplier *= value
        }
        register("jump_multiplier") { stats, config ->
            val value = (config["value"] as? Number)?.toDouble() ?: 1.0
            stats.jumpMultiplier *= value
        }
        register("health_regen_multiplier") { stats, config ->
            val value = (config["value"] as? Number)?.toDouble() ?: 1.0
            stats.healthRegenMultiplier *= value
        }
        register("attack_speed") { stats, config ->
            val value = (config["value"] as? Number)?.toDouble() ?: 1.0
            stats.attackSpeedMultiplier *= value
        }
        register("lifesteal_multiplier") { stats, config ->
            val value = (config["value"] as? Number)?.toDouble() ?: 1.0
            stats.lifestealMultiplier *= value
        }
    }

}
