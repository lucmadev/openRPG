package org.lucma.openRPG.core.registry

import org.lucma.openRPG.models.types.Condition
import org.lucma.openRPG.models.conditions.*

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
        // ── Existentes ──
        register("close_enemies") { config ->
            val radius = (config["radius"] as? Number)?.toDouble() ?: 5.0
            CloseEnemiesCondition(radius)
        }
        register("low_health") { config ->
            val threshold = (config["threshold"] as? Number)?.toDouble() ?: 0.30
            LowHealthCondition(threshold)
        }
        register("night_time") { NightTimeCondition() }
        register("sneaking") { SneakingCondition() }
        register("always") { AlwaysCondition() }

        // ── Ambientales ──
        register("raining") { RainingCondition() }
        register("thundering") { ThunderingCondition() }
        register("day_time") { DayTimeCondition() }
        register("underground") { UndergroundCondition() }
        register("in_water") { InWaterCondition() }
        register("in_lava") { InLavaCondition() }
        register("on_fire") { OnFireCondition() }
        register("in_cave") { config ->
            val maxSky = (config["maxSkyLight"] as? Number)?.toInt() ?: 7
            InCaveCondition(maxSky)
        }
        register("high_altitude") { config ->
            val minY = (config["minY"] as? Number)?.toInt() ?: 0
            val maxY = (config["maxY"] as? Number)?.toInt() ?: -1
            AltitudeCondition(minY, maxY)
        }
        register("biome") { config ->
            val biome = config["biome"] as? String ?: "plains"
            val matchType = config["matchType"] as? String ?: "exact"
            BiomeCondition(biome, matchType)
        }

        // ── Estado del jugador ──
        register("health_above") { config ->
            val threshold = (config["threshold"] as? Number)?.toDouble() ?: 0.70
            HealthAboveCondition(threshold)
        }
        register("full_health") { FullHealthCondition() }
        register("sprinting") { SprintingCondition() }
        register("swimming") { SwimmingCondition() }
        register("gliding") { GlidingCondition() }
        register("on_ground") { OnGroundCondition() }
        register("airborne") { AirborneCondition() }
        register("hunger") { config ->
            val threshold = (config["threshold"] as? Number)?.toInt() ?: 6
            val mode = config["mode"] as? String ?: "below"
            HungerCondition(threshold, mode)
        }
        register("experience_level") { config ->
            val min = (config["minLevel"] as? Number)?.toInt() ?: 0
            val max = (config["maxLevel"] as? Number)?.toInt() ?: -1
            ExperienceCondition(min, max)
        }
        register("saturation") { config ->
            val threshold = (config["threshold"] as? Number)?.toDouble() ?: 5.0
            val mode = config["mode"] as? String ?: "below"
            SaturationCondition(threshold, mode)
        }
        register("fall_distance") { config ->
            val minDist = (config["minDistance"] as? Number)?.toFloat() ?: 3.0f
            FallDistanceCondition(minDist)
        }

        // ── Estado especial ──
        register("has_potion_effect") { config ->
            val effectType = config["effectType"] as? String ?: ""
            HasPotionEffectCondition(effectType)
        }
        register("holding_item") { config ->
            val mat = config["material"] as? String ?: ""
            val hand = config["hand"] as? String ?: "main"
            HoldingItemCondition(mat, hand)
        }
        register("wearing_armor") { config ->
            val mat = config["material"] as? String ?: ""
            WearingArmorCondition(mat)
        }
        register("wearing_full_armor") { WearingFullArmorCondition() }
        register("in_vehicle") { InVehicleCondition() }
        register("sleeping") { SleepingCondition() }

        // ── Combate ──
        register("in_combat") { config ->
            val time = (config["timeSinceHit"] as? Number)?.toInt() ?: 5
            InCombatCondition(time)
        }
        register("no_enemies") { config ->
            val radius = (config["radius"] as? Number)?.toDouble() ?: 5.0
            NoEnemiesCondition(radius)
        }
        register("outnumbered") { config ->
            val radius = (config["radius"] as? Number)?.toDouble() ?: 5.0
            val ratio = (config["ratio"] as? Number)?.toDouble() ?: 1.5
            OutnumberedCondition(radius, ratio)
        }
        register("target_low_health") { config ->
            val threshold = (config["threshold"] as? Number)?.toDouble() ?: 0.30
            TargetLowHealthCondition(threshold)
        }
        register("target_full_health") { TargetFullHealthCondition() }
        register("last_hit_killer") { KillerBlowCondition() }
        register("behind_target") { BehindTargetCondition() }
        register("blocking") { BlockingCondition() }
        register("recently_hurt") { config ->
            val time = (config["timeWindow"] as? Number)?.toInt() ?: 3
            RecentlyHurtCondition(time)
        }

        // ── Compuestas / Lógicas ──
        register("and") { config ->
            val list = (config["conditions"] as? List<Map<String, Any>>)
                ?.map { createCond(it) }
                ?.filterNotNull() ?: emptyList()
            AndCondition(list)
        }
        register("or") { config ->
            val list = (config["conditions"] as? List<Map<String, Any>>)
                ?.map { createCond(it) }
                ?.filterNotNull() ?: emptyList()
            OrCondition(list)
        }
        register("not") { config ->
            val inner = config["condition"] as? Map<String, Any> ?: return@register NotCondition(AlwaysCondition())
            val cond = createCond(inner) ?: AlwaysCondition()
            NotCondition(cond)
        }
        register("random") { config ->
            val chance = (config["chance"] as? Number)?.toDouble() ?: 0.5
            RandomCondition(chance)
        }
        register("cooldown") { config ->
            val key = config["key"] as? String ?: "generic"
            val seconds = (config["seconds"] as? Number)?.toInt() ?: 5
            CooldownCondition(key, seconds)
        }

        // ── Clase / Datos ──
        register("has_class") { config ->
            val classId = config["classId"] as? String ?: ""
            HasClassCondition(classId)
        }
        register("has_talent") { config ->
            val nodeId = config["nodeId"] as? String ?: ""
            HasTalentCondition(nodeId)
        }
        register("player_level") { config ->
            val min = (config["minLevel"] as? Number)?.toInt() ?: 1
            val max = (config["maxLevel"] as? Number)?.toInt() ?: -1
            PlayerLevelCondition(min, max)
        }
        register("talent_points") { config ->
            val min = (config["minPoints"] as? Number)?.toInt() ?: 1
            TalentPointsCondition(min)
        }

        // ── Misceláneas ──
        register("in_region") { config ->
            val world = config["world"] as? String ?: ""
            val x1 = (config["x1"] as? Number)?.toInt() ?: 0
            val z1 = (config["z1"] as? Number)?.toInt() ?: 0
            val x2 = (config["x2"] as? Number)?.toInt() ?: 0
            val z2 = (config["z2"] as? Number)?.toInt() ?: 0
            InRegionCondition(world, x1, z1, x2, z2)
        }
        register("moon_phase") { config ->
            val phase = (config["phase"] as? Number)?.toInt() ?: 0
            MoonPhaseCondition(phase)
        }
        register("light_level") { config ->
            val min = (config["min"] as? Number)?.toInt() ?: 0
            val max = (config["max"] as? Number)?.toInt() ?: 15
            val type = config["type"] as? String ?: "any"
            LightLevelCondition(min, max, type)
        }
        register("in_world") { config ->
            val world = config["worldName"] as? String ?: "world"
            InWorldCondition(world)
        }
    }

    /**
     * Crea una Condition desde un mapa con type + config.
     */
    private fun createCond(map: Map<String, Any>): Condition? {
        val type = map["type"] as? String ?: return null
        val cfg = map["config"] as? Map<String, Any> ?: emptyMap()
        return create(type, cfg)
    }

}
