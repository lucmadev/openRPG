package org.lucma.openRPG.core.registry

import org.lucma.openRPG.models.types.Effect
import org.lucma.openRPG.models.effects.CriticalChanceEffect
import org.lucma.openRPG.models.effects.CriticalDamageEffect
import org.lucma.openRPG.models.effects.DamageBonusEffect
import org.lucma.openRPG.models.effects.DefenseBonusEffect
import org.lucma.openRPG.models.effects.FireAuraEffect
import org.lucma.openRPG.models.effects.HealEffect
import org.lucma.openRPG.models.effects.LifeStealEffect
import org.lucma.openRPG.models.effects.SpeedBonusEffect

object EffectRegistry {

    private val effects =
        mutableMapOf<String, (Map<String, Any>) -> Effect>()

    fun register(
        id: String,
        factory: (Map<String, Any>) -> Effect
    ) {
        effects[id] = factory
    }

    fun create(
        id: String,
        config: Map<String, Any>
    ): Effect? {
        return effects[id]?.invoke(config)
    }

    fun registerDefaults() {
        register("damage_bonus") { config ->
            val bonus = (config["bonus"] as? Number)?.toDouble() ?: 0.0
            DamageBonusEffect(bonus)
        }
        register("defense_bonus") { config ->
            val bonus = (config["bonus"] as? Number)?.toDouble() ?: 0.0
            DefenseBonusEffect(bonus)
        }
        register("speed_bonus") { config ->
            val bonus = (config["bonus"] as? Number)?.toDouble() ?: 0.0
            SpeedBonusEffect(bonus)
        }
        register("heal") { config ->
            val amount = (config["amount"] as? Number)?.toDouble() ?: 2.0
            HealEffect(amount)
        }
        register("life_steal") { config ->
            val pct = (config["percentage"] as? Number)?.toDouble() ?: 0.10
            LifeStealEffect(pct)
        }
        register("fire_aura") { config ->
            val duration = (config["duration"] as? Number)?.toInt() ?: 3
            val cooldown = (config["cooldown"] as? Number)?.toInt() ?: 0
            FireAuraEffect(duration, cooldown)
        }
        register("critical_chance") { config ->
            val chance = (config["chance"] as? Number)?.toDouble() ?: 0.05
            CriticalChanceEffect(chance)
        }
        register("critical_damage") { config ->
            val bonus = (config["bonus"] as? Number)?.toDouble() ?: 0.50
            CriticalDamageEffect(bonus)
        }
    }

}
