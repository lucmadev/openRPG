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
            inv.setItem(31, item(Material.OAK_DOOR, msg("gui.status.close", player)))
            player.openInventory(inv)
            return
        }

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

        val pct = (data.exp.toDouble() / data.expToNextLevel.toDouble()).coerceIn(0.0, 1.0)
        val barra = "§a" + "|".repeat((pct * 20).toInt()) + "§8" + "|".repeat(20 - (pct * 20).toInt())
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
        inv.setItem(27, item(Material.ENDER_CHEST, msg("gui.status.btn_change_class", player)))
        inv.setItem(31, item(Material.EMERALD_BLOCK, msg("gui.status.btn_talent_tree", player)))
        inv.setItem(35, item(Material.OAK_DOOR, msg("gui.status.close", player)))

        player.openInventory(inv)
    }

    /** Build the player head with their stats in the lore */
    private fun buildPlayerHead(player: Player, className: String): ItemStack {
        val item = ItemStack(Material.PLAYER_HEAD)
        val meta = item.itemMeta as SkullMeta
        meta.setOwningPlayer(player)
        meta.displayName(Component.text("§e§l" + className).decoration(TextDecoration.ITALIC, false))

        val maxHp = player.getAttribute(Attribute.MAX_HEALTH)?.value?.roundToInt() ?: 20
        val health = "${player.health.roundToInt()}§8/§c$maxHp"
        val hunger = "${player.foodLevel}§8/§6${20}"
        val xp = player.level

        meta.lore(
            listOf(
                Component.text("§8" + player.getName()).decoration(TextDecoration.ITALIC, false),
                Component.text("").decoration(TextDecoration.ITALIC, false),
                Component.text("§c❤ §7" + health).decoration(TextDecoration.ITALIC, false),
                Component.text("§6🍗 §7" + hunger).decoration(TextDecoration.ITALIC, false),
                Component.text("§b✦ §7Nivel §f" + xp).decoration(TextDecoration.ITALIC, false)
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
        }
    }

    private fun describeCondition(cond: Any, player: Player): String {
        return when (cond) {
            is CloseEnemiesCondition -> msg("condition.close_enemies", player)
            is NightTimeCondition -> msg("condition.night_time", player)
            is LowHealthCondition -> msg("condition.low_health", player, (cond.thresholdPercentage * 100).roundToInt())
            is SneakingCondition -> msg("condition.sneaking", player)
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
