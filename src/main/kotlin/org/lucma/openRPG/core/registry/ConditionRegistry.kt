package org.lucma.openRPG.core.registry

import org.lucma.openRPG.models.types.Condition
import org.lucma.openRPG.models.conditions.CloseEnemiesCondition
import org.lucma.openRPG.models.conditions.LowHealthCondition
import org.lucma.openRPG.models.conditions.NightTimeCondition
import org.lucma.openRPG.models.conditions.SneakingCondition

object ConditionRegistry {

    private val conditions =
        mutableMapOf<String, (Map<String, Any>) -> Condition>()

    fun register(
        id: String,
        factory: (Map<String, Any>) -> Condition
    ) {
        conditions[id] = factory
    }

    fun create(
        id: String,
        config: Map<String, Any>
    ): Condition? {
        return conditions[id]?.invoke(config)
    }

    fun registerDefaults() {
        register("close_enemies") { config ->
            val radius = (config["radius"] as? Number)?.toDouble() ?: 5.0
            CloseEnemiesCondition(radius)
        }
        register("low_health") { config ->
            val threshold = (config["threshold"] as? Number)?.toDouble() ?: 0.30
            LowHealthCondition(threshold)
        }
        register("night_time") {
            NightTimeCondition()
        }
        register("sneaking") {
            SneakingCondition()
        }
    }

}
