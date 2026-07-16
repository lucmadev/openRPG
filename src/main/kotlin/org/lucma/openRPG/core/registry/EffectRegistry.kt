package org.lucma.openRPG.core.registry

import org.lucma.openRPG.models.types.Effect
import org.lucma.openRPG.models.effects.*

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
        // ── Existentes ──
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

        // ── Estado del objetivo ──
        register("wither") { config ->
            val dur = (config["duration"] as? Number)?.toInt() ?: 3
            val amp = (config["amplifier"] as? Number)?.toInt() ?: 1
            WitherEffect(dur, amp)
        }
        register("poison") { config ->
            val dur = (config["duration"] as? Number)?.toInt() ?: 3
            val amp = (config["amplifier"] as? Number)?.toInt() ?: 1
            PoisonEffect(dur, amp)
        }
        register("slowness") { config ->
            val dur = (config["duration"] as? Number)?.toInt() ?: 3
            val amp = (config["amplifier"] as? Number)?.toInt() ?: 1
            SlownessEffect(dur, amp)
        }
        register("weakness") { config ->
            val dur = (config["duration"] as? Number)?.toInt() ?: 3
            val amp = (config["amplifier"] as? Number)?.toInt() ?: 1
            WeaknessEffect(dur, amp)
        }
        register("blindness") { config ->
            val dur = (config["duration"] as? Number)?.toInt() ?: 3
            BlindnessEffect(dur)
        }
        register("levitation") { config ->
            val dur = (config["duration"] as? Number)?.toInt() ?: 3
            val amp = (config["amplifier"] as? Number)?.toInt() ?: 1
            LevitationEffect(dur, amp)
        }
        register("glow") { config ->
            val dur = (config["duration"] as? Number)?.toInt() ?: 5
            GlowEffect(dur)
        }
        register("hunger_effect") { config ->
            val dur = (config["duration"] as? Number)?.toInt() ?: 5
            val amp = (config["amplifier"] as? Number)?.toInt() ?: 1
            HungerEffect(dur, amp)
        }
        register("mining_fatigue") { config ->
            val dur = (config["duration"] as? Number)?.toInt() ?: 5
            val amp = (config["amplifier"] as? Number)?.toInt() ?: 1
            MiningFatigueEffect(dur, amp)
        }
        register("nausea") { config ->
            val dur = (config["duration"] as? Number)?.toInt() ?: 3
            NauseaEffect(dur)
        }
        register("silence") { config ->
            val dur = (config["duration"] as? Number)?.toInt() ?: 3
            SilenceEffect(dur)
        }
        register("disarm") { config ->
            val chance = (config["dropChance"] as? Number)?.toDouble() ?: 1.0
            DisarmEffect(chance)
        }
        register("stun") { config ->
            val dur = (config["duration"] as? Number)?.toInt() ?: 2
            StunEffect(dur)
        }
        register("bleed") { config ->
            val dur = (config["duration"] as? Number)?.toInt() ?: 3
            val dmg = (config["damagePerTick"] as? Number)?.toDouble() ?: 0.5
            BleedEffect(dur, dmg)
        }

        // ── Daño especial ──
        register("knockback") { config ->
            val mult = (config["multiplier"] as? Number)?.toDouble() ?: 0.5
            KnockbackEffect(mult)
        }
        register("execute") { config ->
            val mult = (config["bonusMultiplier"] as? Number)?.toDouble() ?: 0.5
            val thr = (config["threshold"] as? Number)?.toDouble() ?: 0.30
            ExecuteEffect(mult, thr)
        }
        register("backstab") { config ->
            val mult = (config["bonusMultiplier"] as? Number)?.toDouble() ?: 0.5
            BackstabEffect(mult)
        }
        register("charge") { config ->
            val mult = (config["bonusMultiplier"] as? Number)?.toDouble() ?: 0.3
            ChargeEffect(mult)
        }
        register("area_damage") { config ->
            val rad = (config["radius"] as? Number)?.toDouble() ?: 3.0
            val mult = (config["multiplier"] as? Number)?.toDouble() ?: 0.5
            AreaDamageEffect(rad, mult)
        }
        register("chain_damage") { config ->
            val rad = (config["radius"] as? Number)?.toDouble() ?: 5.0
            val max = (config["maxTargets"] as? Number)?.toInt() ?: 3
            val mult = (config["multiplier"] as? Number)?.toDouble() ?: 0.7
            ChainDamageEffect(rad, max, mult)
        }
        register("true_damage") { config ->
            val pct = (config["percentage"] as? Number)?.toDouble() ?: 0.20
            TrueDamageEffect(pct)
        }
        register("lightning_strike") { config ->
            val dmg = (config["damage"] as? Number)?.toDouble() ?: 0.0
            LightningStrikeEffect(dmg)
        }
        register("explosion") { config ->
            val power = (config["power"] as? Number)?.toFloat() ?: 2.0f
            val fire = config["fire"] as? Boolean ?: false
            ExplosionEffect(power, fire)
        }
        register("shield_breaker") { config ->
            val mult = (config["bonusMultiplier"] as? Number)?.toDouble() ?: 0.30
            ShieldBreakerEffect(mult)
        }
        register("splash_damage") { config ->
            val rad = (config["radius"] as? Number)?.toDouble() ?: 3.0
            val mult = (config["multiplier"] as? Number)?.toDouble() ?: 0.4
            SplashDamageEffect(rad, mult)
        }

        // ── Curación / Defensa ──
        register("absorption") { config ->
            val hearts = (config["hearts"] as? Number)?.toDouble() ?: 4.0
            AbsorptionEffect(hearts)
        }
        register("regeneration") { config ->
            val dur = (config["duration"] as? Number)?.toInt() ?: 5
            val amp = (config["amplifier"] as? Number)?.toInt() ?: 1
            RegenerationEffect(dur, amp)
        }
        register("damage_reduction") { config ->
            val red = (config["flatReduction"] as? Number)?.toDouble() ?: 1.0
            DamageReductionEffect(red)
        }
        register("damage_reflect") { config ->
            val pct = (config["percentage"] as? Number)?.toDouble() ?: 0.10
            DamageReflectEffect(pct)
        }
        register("dodge") { config ->
            val chance = (config["chance"] as? Number)?.toDouble() ?: 0.10
            DodgeEffect(chance)
        }
        register("shield") { config ->
            val health = (config["health"] as? Number)?.toDouble() ?: 5.0
            ShieldEffect(health)
        }
        register("fire_resistance") { config ->
            val dur = (config["duration"] as? Number)?.toInt() ?: 30
            FireResistanceEffect(dur)
        }
        register("water_breathing") { config ->
            val dur = (config["duration"] as? Number)?.toInt() ?: 30
            WaterBreathingEffect(dur)
        }
        register("invulnerability") { config ->
            val dur = (config["duration"] as? Number)?.toInt() ?: 2
            InvulnerabilityEffect(dur)
        }

        // ── Movimiento ──
        register("jump_boost") { config ->
            val mult = (config["multiplier"] as? Number)?.toDouble() ?: 0.5
            JumpBoostEffect(mult)
        }
        register("slow_fall") { config ->
            val dur = (config["duration"] as? Number)?.toInt() ?: 5
            SlowFallEffect(dur)
        }
        register("leap") { config ->
            val power = (config["power"] as? Number)?.toDouble() ?: 1.5
            val up = (config["upward"] as? Number)?.toDouble() ?: 0.5
            LeapEffect(power, up)
        }
        register("dash") { config ->
            val dist = (config["distance"] as? Number)?.toDouble() ?: 5.0
            DashEffect(dist)
        }
        register("speed_aura") { config ->
            val rad = (config["radius"] as? Number)?.toDouble() ?: 8.0
            val amp = (config["amplifier"] as? Number)?.toInt() ?: 1
            SpeedAuraEffect(rad, amp)
        }
        register("web") { config ->
            val rad = (config["radius"] as? Number)?.toInt() ?: 2
            val dur = (config["duration"] as? Number)?.toInt() ?: 3
            WebEffect(rad, dur)
        }

        // ── Buffs (pociones) ──
        register("strength") { config ->
            val dur = (config["duration"] as? Number)?.toInt() ?: 10
            val amp = (config["amplifier"] as? Number)?.toInt() ?: 1
            StrengthEffect(dur, amp)
        }
        register("speed_potion") { config ->
            val dur = (config["duration"] as? Number)?.toInt() ?: 10
            val amp = (config["amplifier"] as? Number)?.toInt() ?: 1
            SpeedPotionEffect(dur, amp)
        }
        register("resistance") { config ->
            val dur = (config["duration"] as? Number)?.toInt() ?: 10
            val amp = (config["amplifier"] as? Number)?.toInt() ?: 1
            ResistanceEffect(dur, amp)
        }
        register("invisibility") { config ->
            val dur = (config["duration"] as? Number)?.toInt() ?: 10
            InvisibilityEffect(dur)
        }
        register("night_vision") { config ->
            val dur = (config["duration"] as? Number)?.toInt() ?: 30
            NightVisionEffect(dur)
        }
        register("haste") { config ->
            val dur = (config["duration"] as? Number)?.toInt() ?: 10
            val amp = (config["amplifier"] as? Number)?.toInt() ?: 1
            HasteEffect(dur, amp)
        }
        register("dolphin_grace") { config ->
            val dur = (config["duration"] as? Number)?.toInt() ?: 15
            DolphinGraceEffect(dur)
        }
        register("luck_potion") { config ->
            val dur = (config["duration"] as? Number)?.toInt() ?: 30
            val amp = (config["amplifier"] as? Number)?.toInt() ?: 1
            LuckEffect(dur, amp)
        }

        // ── Recursos ──
        register("exp_bonus") { config ->
            val mult = (config["multiplier"] as? Number)?.toDouble() ?: 0.5
            ExpBonusEffect(mult)
        }
        register("loot_bonus") { config ->
            val mult = (config["multiplier"] as? Number)?.toDouble() ?: 0.5
            LootBonusEffect(mult)
        }
        register("mining_speed") { config ->
            val mult = (config["multiplier"] as? Number)?.toDouble() ?: 0.5
            MiningSpeedEffect(mult)
        }
        register("health_regen") { config ->
            val mult = (config["multiplier"] as? Number)?.toDouble() ?: 0.5
            HealthRegenEffect(mult)
        }
        register("mana_regen") { config ->
            val regen = (config["regenPerSecond"] as? Number)?.toDouble() ?: 1.0
            ManaRegenEffect(regen)
        }
        register("saturation") { config ->
            val amount = (config["amount"] as? Number)?.toDouble() ?: 5.0
            SaturationEffect(amount)
        }
        register("feed") { config ->
            val food = (config["hungerRestored"] as? Number)?.toInt() ?: 4
            FeedEffect(food)
        }

        // ── Visuales / Sonido ──
        register("particle") { config ->
            val name = config["particle"] as? String ?: "CRIT"
            val count = (config["count"] as? Number)?.toInt() ?: 10
            val speed = (config["speed"] as? Number)?.toDouble() ?: 0.1
            ParticleEffect(name, count, speed)
        }
        register("sound") { config ->
            val name = config["sound"] as? String ?: "ENTITY_PLAYER_LEVELUP"
            val vol = (config["volume"] as? Number)?.toFloat() ?: 1.0f
            val pitch = (config["pitch"] as? Number)?.toFloat() ?: 1.0f
            SoundEffect(name, vol, pitch)
        }
        register("title") { config ->
            val title = config["title"] as? String ?: ""
            val sub = config["subtitle"] as? String ?: ""
            val fadeIn = (config["fadeIn"] as? Number)?.toInt() ?: 10
            val stay = (config["stay"] as? Number)?.toInt() ?: 40
            val fadeOut = (config["fadeOut"] as? Number)?.toInt() ?: 10
            TitleEffect(title, sub, fadeIn, stay, fadeOut)
        }

        // ── Otros ──
        register("life_steal_multiplier") { config ->
            val mult = (config["multiplier"] as? Number)?.toDouble() ?: 0.5
            LifeStealMultiplierEffect(mult)
        }
    }

}
