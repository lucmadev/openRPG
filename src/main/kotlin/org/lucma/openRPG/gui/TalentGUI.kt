package org.lucma.openRPG.gui

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.attribute.Attribute
import kotlin.math.roundToInt
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
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
import org.lucma.openRPG.models.talents.SkillTreeNode

object TalentGUI : Listener {

    private const val GUI_SIZE = 36
    private val nodeSlots = listOf(10, 11, 12, 14, 15, 16)

    fun open(player: Player) {
        val clazz = PlayerClassManager.getPlayerClass(player)
        if (clazz == null) {
            player.sendMessage(msg("gui.talent.no_class", player))
            return
        }

        val data = PlayerDataManager.getOrCreate(player)
        val nodes = SkillTree.getNodesForClass(clazz.id)
        if (nodes.isEmpty()) return

        val title = msg("gui.talent.title", player)
        val inv = Bukkit.createInventory(null, GUI_SIZE, Component.text(title))

        // ── Info ──
        inv.setItem(0, itemHead(player, msg("class." + clazz.id + ".name", player)))
        inv.setItem(2, item(Material.EXPERIENCE_BOTTLE, msg("gui.talent.points_header", player, data.talentPoints), msg("gui.talent.exp_header", player, data.exp, data.expToNextLevel)))
        inv.setItem(8, item(Material.OAK_DOOR, msg("gui.talent.close", player)))

        for (slot in listOf(1, 3, 5, 7)) {
            inv.setItem(slot, vidrio(Material.GRAY_STAINED_GLASS_PANE))
        }
        for (slot in 9..17) {
            if (inv.getItem(slot) == null) inv.setItem(slot, vidrio(Material.BLACK_STAINED_GLASS_PANE))
        }

        // ── Nodos ──
        nodes.forEachIndexed { i, node ->
            if (i < nodeSlots.size) {
                inv.setItem(nodeSlots[i], buildNodeItem(player, node))
            }
        }

        // ── Conexiones ──
        if (nodes.size > 1 && nodes[0].prerequisites.isEmpty() && nodes[1].prerequisites.contains(nodes[0].id)) {
            inv.setItem(19, item(Material.ARROW, "§8├──►"))
        }

        for (slot in 27..35) {
            inv.setItem(slot, vidrio(Material.GRAY_STAINED_GLASS_PANE))
        }

        player.openInventory(inv)
    }

    private fun buildNodeItem(player: Player, node: SkillTreeNode): ItemStack {
        val data = PlayerDataManager.getOrCreate(player)
        val unlocked = node.id in data.unlockedNodes
        val check = SkillTree.canUnlock(node.id, data.unlockedNodes)
        val canAfford = data.talentPoints > 0
        val canUnlock = check.can && canAfford

        return when {
            unlocked -> {
                val item = ItemStack(node.material)
                val meta = item.itemMeta
                meta.displayName(Component.text("§a§l✔ " + node.name).decoration(TextDecoration.ITALIC, false))
                meta.lore(listOf(
                    Component.text(node.description).decoration(TextDecoration.ITALIC, false),
                    Component.text("").decoration(TextDecoration.ITALIC, false),
                    Component.text(msg("gui.talent.learned", player)).decoration(TextDecoration.ITALIC, false)
                ))
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
                item.itemMeta = meta
                item.addUnsafeEnchantment(Enchantment.UNBREAKING, 1)
                item
            }
            canUnlock -> {
                val item = ItemStack(node.material)
                val meta = item.itemMeta
                meta.displayName(Component.text("§e§l◉ " + node.name).decoration(TextDecoration.ITALIC, false))
                val lore = mutableListOf(
                    Component.text(node.description).decoration(TextDecoration.ITALIC, false),
                    Component.text("").decoration(TextDecoration.ITALIC, false),
                    Component.text(msg("gui.talent.click_to_learn", player)).decoration(TextDecoration.ITALIC, false)
                )
                if (node.prerequisites.isNotEmpty()) {
                    val pre = node.prerequisites.mapNotNull { SkillTree.getNode(it)?.name }.joinToString(", ")
                    lore.add(Component.text(msg("gui.talent.requires", player, pre)).decoration(TextDecoration.ITALIC, false))
                }
                meta.lore(lore)
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
                item.itemMeta = meta
                item.addUnsafeEnchantment(Enchantment.UNBREAKING, 1)
                item
            }
            else -> {
                val item = ItemStack(Material.GRAY_DYE)
                val meta = item.itemMeta
                meta.displayName(Component.text("§8" + node.name).decoration(TextDecoration.ITALIC, false))
                val lore = mutableListOf(
                    Component.text(node.description).decoration(TextDecoration.ITALIC, false),
                    Component.text("").decoration(TextDecoration.ITALIC, false)
                )
                if (!check.can) {
                    lore.add(Component.text(msg("gui.talent.not_available", player, check.reason)).decoration(TextDecoration.ITALIC, false))
                } else {
                    lore.add(Component.text(msg("gui.talent.no_points", player)).decoration(TextDecoration.ITALIC, false))
                }
                meta.lore(lore)
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                item.itemMeta = meta
                item
            }
        }
    }

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        if (event.inventory !== event.view.topInventory) return
        val titleStr = PlainTextComponentSerializer.plainText().serialize(event.view.title())
        if (!titleStr.contains("Talent") && !titleStr.contains("Talento")) return

        event.isCancelled = true
        val player = event.whoClicked as? Player ?: return

        if (event.rawSlot == 8) {
            player.closeInventory()
            return
        }

        val nodeIndex = nodeSlots.indexOf(event.rawSlot)
        if (nodeIndex == -1) return

        val clazz = PlayerClassManager.getPlayerClass(player) ?: return
        val nodes = SkillTree.getNodesForClass(clazz.id)
        if (nodeIndex >= nodes.size) return

        val node = nodes[nodeIndex]
        val data = PlayerDataManager.getOrCreate(player)

        if (node.id in data.unlockedNodes) {
            player.sendActionBar(Component.text(msg("gui.talent.already_learned", player)))
            return
        }

        val result = SkillTree.canUnlock(node.id, data.unlockedNodes)
        if (!result.can) {
            player.sendActionBar(Component.text(msg("gui.talent.not_available", player, result.reason)))
            return
        }

        if (data.talentPoints <= 0) {
            player.sendActionBar(Component.text(msg("gui.talent.no_points", player)))
            return
        }

        val ok = PlayerDataManager.allocateNode(player, node.id)
        if (ok) {
            player.sendActionBar(Component.text(msg("gui.talent.learned_ok", player, node.name)))
            player.closeInventory()
            open(player)
        }
    }

    private fun item(mat: Material, name: String, vararg lore: String): ItemStack {
        val item = ItemStack(mat)
        val meta = item.itemMeta
        meta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false))
        val loreList = java.util.ArrayList<Component>()
        for (line in lore) {
            loreList.add(Component.text(line).decoration(TextDecoration.ITALIC, false))
        }
        meta.lore(loreList)
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        item.itemMeta = meta
        return item
    }

    private fun itemHead(player: Player, name: String): ItemStack {
        val item = ItemStack(Material.PLAYER_HEAD)
        val meta = item.itemMeta as SkullMeta
        meta.setOwningPlayer(player)
        meta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false))
        meta.lore(listOf(
            Component.text("§8" + player.getName()).decoration(TextDecoration.ITALIC, false),
            Component.text("").decoration(TextDecoration.ITALIC, false),
            Component.text("§c❤ §7" + player.health.roundToInt() + "§8/§c" + (player.getAttribute(Attribute.MAX_HEALTH)?.value?.roundToInt() ?: 20)).decoration(TextDecoration.ITALIC, false),
            Component.text("§6🍗 §7" + player.foodLevel + "§8/§6" + 20).decoration(TextDecoration.ITALIC, false),
            Component.text("§b✦ §7Nivel §f" + player.level).decoration(TextDecoration.ITALIC, false)
        ))
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
