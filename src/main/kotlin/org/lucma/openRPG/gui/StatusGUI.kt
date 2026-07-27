package org.lucma.openRPG.gui

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.attribute.Attribute
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.lucma.openRPG.core.LanguageManager.msg
import org.lucma.openRPG.managers.PlayerClassManager
import org.lucma.openRPG.managers.PlayerDataManager
import org.lucma.openRPG.models.talents.SkillTree
import org.lucma.openRPG.models.types.Effect
import org.lucma.openRPG.models.effects.*
import org.lucma.openRPG.models.conditions.*
import kotlin.math.roundToInt

object StatusGUI : Listener {

    private const val GUI_SIZE = 36

    fun open(player: Player) {
        val title = msg("gui.status.title", player)
        val clazz = PlayerClassManager.getPlayerClass(player)
        val data = PlayerDataManager.getOrCreate(player)
        val inv = Bukkit.createInventory(null, GUI_SIZE, Component.text(title))

        for (i in 0 until GUI_SIZE) {
            inv.setItem(i, vidrio(Material.BLACK_STAINED_GLASS_PANE))
        }

        if (clazz == null) {
            inv.setItem(
                4,
                item(Material.BARRIER, msg("gui.status.no_class", player), msg("gui.status.no_class_hint", player))
            )
            inv.setItem(
                22,
                item(
                    Material.ENDER_CHEST,
                    msg("gui.status.select_class", player),
                    msg("gui.status.select_class", player)
                )
            )
            inv.setItem(
                4,
                item(Material.BARRIER, msg("gui.status.no_class", player), msg("gui.status.no_class_hint", player))
            )
            inv.setItem(
                22,
                item(
                    Material.ENDER_CHEST,
                    msg("gui.status.select_class", player),
                    msg("gui.status.select_class", player)
                )
            )
            inv.setItem(31, item(Material.OAK_DOOR, msg("gui.status.close", player)))
            player.openInventory(inv)
            return
        }

        // ── Row 1: general info ──
        // ── Row 1: general info ──
        inv.setItem(0, buildPlayerHead(player, clazz.name))
        inv.setItem(
            2,
            item(
                Material.EXPERIENCE_BOTTLE,
                msg("gui.status.level", player, data.level),
                msg("gui.status.exp_header", player, data.exp, data.expToNextLevel)
            )
        )
        inv.setItem(
            2,
            item(
                Material.EXPERIENCE_BOTTLE,
                msg("gui.status.level", player, data.level),
                msg("gui.status.exp_header", player, data.exp, data.expToNextLevel)
            )
        )

        val pct = (data.exp.toDouble() / data.expToNextLevel.toDouble()).coerceIn(0.0, 1.0)
        val filled = (pct * 20).toInt()
        val empty = 20 - filled
        val barra = msg("gui.status.exp_bar_filled", player) + "|".repeat(filled) + msg("gui.status.exp_bar_empty", player) + "|".repeat(empty)
        inv.setItem(
            4,
            item(
                Material.FILLED_MAP,
                msg("gui.status.exp_progress", player),
                barra,
                msg("gui.status.exp_bar", player, (pct * 100).toInt(), data.level + 1)
            )
        )

        inv.setItem(6, item(Material.EMERALD, msg("gui.status.talent_points", player, data.talentPoints)))
        inv.setItem(8, item(Material.OAK_DOOR, msg("gui.status.close", player)))

        // ── Row 2: Stats ──
        inv.setItem(
            9,
            item(
                Material.RED_DYE,
                msg("gui.status.stats_damage", player),
                msg("gui.status.stats_multiplier", player, "1.00")
            )
        )
        inv.setItem(
            11,
            item(
                Material.BLUE_DYE,
                msg("gui.status.stats_defense", player),
                msg("gui.status.stats_multiplier", player, "1.00")
            )
        )
        inv.setItem(
            13,
            item(
                Material.WHITE_DYE,
                msg("gui.status.stats_speed", player),
                msg("gui.status.stats_multiplier", player, "1.00")
            )
        )
        inv.setItem(
            15,
            item(
                Material.ORANGE_DYE,
                msg("gui.status.stats_crit", player),
                msg("gui.status.stats_crit_chance", player, "0")
            )
        )
        inv.setItem(
            17,
            item(
                Material.YELLOW_DYE,
                msg("gui.status.stats_crit_multi", player),
                msg("gui.status.stats_multiplier", player, "1.0")
            )
        )
        // ── Row 2: Stats ──
        inv.setItem(
            9,
            item(
                Material.RED_DYE,
                msg("gui.status.stats_damage", player),
                msg("gui.status.stats_multiplier", player, "1.00")
            )
        )
        inv.setItem(
            11,
            item(
                Material.BLUE_DYE,
                msg("gui.status.stats_defense", player),
                msg("gui.status.stats_multiplier", player, "1.00")
            )
        )
        inv.setItem(
            13,
            item(
                Material.WHITE_DYE,
                msg("gui.status.stats_speed", player),
                msg("gui.status.stats_multiplier", player, "1.00")
            )
        )
        inv.setItem(
            15,
            item(
                Material.ORANGE_DYE,
                msg("gui.status.stats_crit", player),
                msg("gui.status.stats_crit_chance", player, "0")
            )
        )
        inv.setItem(
            17,
            item(
                Material.YELLOW_DYE,
                msg("gui.status.stats_crit_multi", player),
                msg("gui.status.stats_multiplier", player, "1.0")
            )
        )

        // ── Row 3: Active modifiers ──
        // ── Row 3: Active modifiers ──
        var slot = 18
        for (mod in clazz.modifiers) {
            if (slot >= 26) break
            val desc = describeEffect(mod.effect, player) + " §8→ §7" + describeCondition(mod.condition, player)
            inv.setItem(slot, item(Material.ENCHANTED_BOOK, msg("gui.status.modifier", player), desc))
            slot++
        }
        if (data.unlockedNodes.isNotEmpty()) {
            if (slot < 26) {
                inv.setItem(slot, vidrio(Material.GRAY_STAINED_GLASS_PANE))
                slot++
            }
            val talentMods = SkillTree.getModifiers(data.unlockedNodes)
            for (mod in talentMods) {
                if (slot >= 26) break
                val desc = describeEffect(mod.effect, player) + " §8→ §7" + describeCondition(mod.condition, player)
                inv.setItem(slot, item(Material.LIME_DYE, msg("gui.status.talent_unlocked", player), desc))
                slot++
            }
        }
        while (slot < 26) {
            inv.setItem(slot, vidrio(Material.BLACK_STAINED_GLASS_PANE))
            slot++
        }

        // ── Row 4: Buttons ──
        // ── Row 4: Buttons ──
        inv.setItem(27, item(Material.ENDER_CHEST, msg("gui.status.btn_change_class", player)))
        inv.setItem(31, item(Material.EMERALD_BLOCK, msg("gui.status.btn_talent_tree", player)))
        inv.setItem(35, item(Material.OAK_DOOR, msg("gui.status.close", player)))

        player.openInventory(inv)
    }

    /** Build the player head with their stats in the lore */
    /** Build the player head with their stats in the lore */
    private fun buildPlayerHead(player: Player, className: String): ItemStack {
        val item = ItemStack(Material.PLAYER_HEAD)
        val meta = item.itemMeta as SkullMeta
        meta.setOwningPlayer(player)
        meta.displayName(Component.text("§e§l" + className).decoration(TextDecoration.ITALIC, false))

        val maxHp = player.getAttribute(Attribute.MAX_HEALTH)?.value?.roundToInt() ?: 20
        val curHp = player.health.roundToInt()
        val hungerLvl = player.foodLevel
        val xpLvl = player.level

        meta.lore(
            listOf(
                Component.text("§8" + player.getName()).decoration(TextDecoration.ITALIC, false),
                Component.text("").decoration(TextDecoration.ITALIC, false),
                Component.text(msg("gui.stats.health", player, curHp, maxHp))
                    .decoration(TextDecoration.ITALIC, false),
                Component.text(msg("gui.stats.hunger", player, hungerLvl))
                    .decoration(TextDecoration.ITALIC, false),
                Component.text(msg("gui.stats.level", player, xpLvl))
                    .decoration(TextDecoration.ITALIC, false)
            )
        )
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        item.itemMeta = meta
        return item
    }

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        if (event.inventory !== event.view.topInventory) return
        val titleStr = PlainTextComponentSerializer.plainText().serialize(event.view.title())
        if (!titleStr.contains("Estado") && !titleStr.contains("Status") && !titleStr.contains("RPG")) return

        event.isCancelled = true
        val player = event.whoClicked as? Player ?: return

        when (event.rawSlot) {
            8, 35 -> player.closeInventory()
            6, 31 -> {
                player.closeInventory(); TalentGUI.open(player)
            }

            22, 27 -> {
                player.closeInventory(); ClassSelectionGUI.open(player)
            }
            6, 31 -> {
                player.closeInventory(); TalentGUI.open(player)
            }

            22, 27 -> {
                player.closeInventory(); ClassSelectionGUI.open(player)
            }
        }
    }

    private fun describeCondition(cond: Any, player: Player): String {
        return when (cond) {
            is CloseEnemiesCondition -> msg("condition.close_enemies", player)
            is NightTimeCondition -> msg("condition.night_time", player)
            is LowHealthCondition -> msg("condition.low_health", player, (cond.thresholdPercentage * 100).roundToInt())
            is SneakingCondition -> msg("condition.sneaking", player)
            is RainingCondition -> msg("condition.raining", player)
            is ThunderingCondition -> msg("condition.thundering", player)
            is DayTimeCondition -> msg("condition.day_time", player)
            is UndergroundCondition -> msg("condition.underground", player)
            is InWaterCondition -> msg("condition.in_water", player)
            is InLavaCondition -> msg("condition.in_lava", player)
            is OnFireCondition -> msg("condition.on_fire", player)
            is InCaveCondition -> msg("condition.in_cave", player)
            is AltitudeCondition -> msg("condition.high_altitude", player)
            is BiomeCondition -> msg("condition.biome", player, cond.biome)
            is HealthAboveCondition -> msg("condition.health_above", player, (cond.threshold * 100).roundToInt())
            is FullHealthCondition -> msg("condition.full_health", player)
            is SprintingCondition -> msg("condition.sprinting", player)
            is SwimmingCondition -> msg("condition.swimming", player)
            is GlidingCondition -> msg("condition.gliding", player)
            is OnGroundCondition -> msg("condition.on_ground", player)
            is AirborneCondition -> msg("condition.airborne", player)
            is HungerCondition -> msg("condition.hunger", player)
            is ExperienceCondition -> msg("condition.experience_level", player)
            is SaturationCondition -> msg("condition.saturation", player)
            is FallDistanceCondition -> msg("condition.fall_distance", player)
            is HasPotionEffectCondition -> msg("condition.has_potion_effect", player)
            is HoldingItemCondition -> msg("condition.holding_item", player)
            is WearingArmorCondition -> msg("condition.wearing_armor", player)
            is WearingFullArmorCondition -> msg("condition.wearing_full_armor", player)
            is InVehicleCondition -> msg("condition.in_vehicle", player)
            is SleepingCondition -> msg("condition.sleeping", player)
            is InCombatCondition -> msg("condition.in_combat", player)
            is NoEnemiesCondition -> msg("condition.no_enemies", player)
            is OutnumberedCondition -> msg("condition.outnumbered", player)
            is TargetLowHealthCondition -> msg("condition.target_low_health", player)
            is TargetFullHealthCondition -> msg("condition.target_full_health", player)
            is KillerBlowCondition -> msg("condition.last_hit_killer", player)
            is BehindTargetCondition -> msg("condition.behind_target", player)
            is BlockingCondition -> msg("condition.blocking", player)
            is RecentlyHurtCondition -> msg("condition.recently_hurt", player)
            is HasClassCondition -> msg("condition.has_class", player)
            is HasTalentCondition -> msg("condition.has_talent", player)
            is PlayerLevelCondition -> msg("condition.player_level", player)
            is TalentPointsCondition -> msg("condition.talent_points", player)
            is InRegionCondition -> msg("condition.in_region", player)
            is MoonPhaseCondition -> msg("condition.moon_phase", player)
            is LightLevelCondition -> msg("condition.light_level", player)
            is InWorldCondition -> msg("condition.in_world", player)
            is AndCondition -> msg("condition.and", player)
            is OrCondition -> msg("condition.or", player)
            is NotCondition -> msg("condition.not", player)
            is RandomCondition -> msg("condition.random", player)
            is CooldownCondition -> msg("condition.cooldown", player)
            is AlwaysCondition -> "§aAlways"
            else -> cond::class.simpleName ?: "?"
        }
    }

    private fun describeEffect(effect: Effect, player: Player): String {
        return when (effect) {
            is DamageBonusEffect -> msg("effect.damage_bonus", player, (effect.bonus * 100).roundToInt())
            is DefenseBonusEffect -> msg("effect.defense_bonus", player, (effect.bonus * 100).roundToInt())
            is SpeedBonusEffect -> msg("effect.speed_bonus", player, (effect.bonus * 100).roundToInt())
            is HealEffect -> msg("effect.heal", player, effect.amount)
            is LifeStealEffect -> msg("effect.life_steal", player, (effect.stealPercentage * 100).roundToInt())
            is FireAuraEffect -> msg("effect.fire_aura", player, effect.duration)
            is CriticalChanceEffect -> msg("effect.crit_chance", player, (effect.chance * 100).roundToInt())
            is CriticalDamageEffect -> msg("effect.crit_damage", player, (effect.bonus * 100).roundToInt())
            is KnockbackEffect -> msg("effect.knockback", player, (effect.multiplier * 100).roundToInt())
            is ExecuteEffect -> msg("effect.execute", player, effect.bonusMultiplier)
            is BackstabEffect -> msg("effect.backstab", player, (effect.bonusMultiplier * 100).roundToInt())
            is ChargeEffect -> msg("effect.charge", player, (effect.bonusMultiplier * 100).roundToInt())
            is AreaDamageEffect -> msg("effect.area_damage", player, effect.multiplier)
            is ChainDamageEffect -> msg("effect.chain_damage", player)
            is TrueDamageEffect -> msg("effect.true_damage", player)
            is LightningStrikeEffect -> msg("effect.lightning_strike", player)
            is ExplosionEffect -> msg("effect.explosion", player)
            is ShieldBreakerEffect -> msg("effect.shield_breaker", player)
            is SplashDamageEffect -> msg("effect.splash_damage", player)
            is AbsorptionEffect -> msg("effect.absorption", player, effect.hearts)
            is RegenerationEffect -> msg("effect.regeneration", player)
            is DamageReductionEffect -> msg("effect.damage_reduction", player, effect.flatReduction)
            is DamageReflectEffect -> msg("effect.damage_reflect", player, (effect.percentage * 100).roundToInt())
            is DodgeEffect -> msg("effect.dodge", player, (effect.chance * 100).roundToInt())
            is ShieldEffect -> msg("effect.shield", player, effect.health)
            is FireResistanceEffect -> msg("effect.fire_resistance", player)
            is WaterBreathingEffect -> msg("effect.water_breathing", player)
            is InvulnerabilityEffect -> msg("effect.invulnerability", player)
            is JumpBoostEffect -> msg("effect.jump_boost", player, (effect.multiplier * 100).roundToInt())
            is SlowFallEffect -> msg("effect.slow_fall", player)
            is LeapEffect -> msg("effect.leap", player)
            is DashEffect -> msg("effect.dash", player)
            is SpeedAuraEffect -> msg("effect.speed_aura", player)
            is WebEffect -> msg("effect.web", player)
            is StrengthEffect -> msg("effect.strength", player)
            is SpeedPotionEffect -> msg("effect.speed_potion", player)
            is ResistanceEffect -> msg("effect.resistance", player)
            is InvisibilityEffect -> msg("effect.invisibility", player)
            is NightVisionEffect -> msg("effect.night_vision", player)
            is HasteEffect -> msg("effect.haste", player)
            is DolphinGraceEffect -> msg("effect.dolphin_grace", player)
            is LuckEffect -> msg("effect.luck_potion", player)
            is ExpBonusEffect -> msg("effect.exp_bonus", player, (effect.multiplier * 100).roundToInt())
            is LootBonusEffect -> msg("effect.loot_bonus", player, (effect.multiplier * 100).roundToInt())
            is MiningSpeedEffect -> msg("effect.mining_speed", player, (effect.multiplier * 100).roundToInt())
            is HealthRegenEffect -> msg("effect.health_regen", player, (effect.multiplier * 100).roundToInt())
            is ManaRegenEffect -> msg("effect.mana_regen", player, effect.regenPerSecond)
            is SaturationEffect -> msg("effect.saturation", player)
            is FeedEffect -> msg("effect.feed", player, effect.hungerRestored)
            is ParticleEffect -> msg("effect.particle", player)
            is SoundEffect -> msg("effect.sound", player)
            is TitleEffect -> msg("effect.title", player)
            is WitherEffect -> msg("effect.wither", player)
            is PoisonEffect -> msg("effect.poison", player)
            is SlownessEffect -> msg("effect.slowness", player)
            is WeaknessEffect -> msg("effect.weakness", player)
            is BlindnessEffect -> msg("effect.blindness", player)
            is LevitationEffect -> msg("effect.levitation", player)
            is GlowEffect -> msg("effect.glow", player)
            is HungerEffect -> msg("effect.hunger_effect", player)
            is MiningFatigueEffect -> msg("effect.mining_fatigue", player)
            is NauseaEffect -> msg("effect.nausea", player)
            is SilenceEffect -> msg("effect.silence", player)
            is DisarmEffect -> msg("effect.disarm", player)
            is StunEffect -> msg("effect.stun", player)
            is BleedEffect -> msg("effect.bleed", player)
            is LifeStealMultiplierEffect -> msg("effect.life_steal_multiplier", player, (effect.multiplier * 100).roundToInt())
            else -> "§f" + (effect::class.simpleName ?: "?")
        }
    }

    private fun item(mat: Material, name: String, vararg lore: String): ItemStack {
        val item = ItemStack(mat)
        val meta = item.itemMeta
        meta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false))
        val loreList = java.util.ArrayList<Component>()
        for (line in lore) loreList.add(Component.text(line).decoration(TextDecoration.ITALIC, false))
        meta.lore(loreList)
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        item.itemMeta = meta
        return item
    }

    private fun vidrio(mat: Material): ItemStack {
        val item = ItemStack(mat)
        val meta = item.itemMeta
        meta.displayName(Component.text("").decoration(TextDecoration.ITALIC, false))
        item.itemMeta = meta
        return item
    }
}
