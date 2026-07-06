package org.lucma.openRPG.gui

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.lucma.openRPG.managers.PlayerClassManager
import org.lucma.openRPG.managers.PlayerDataManager
import org.lucma.openRPG.models.talents.SkillTree
import org.lucma.openRPG.models.talents.SkillTreeNode

object TalentGUI : Listener {

    private const val GUI_SIZE = 36
    private const val GUI_TITLE = "§8Árbol de Talentos"

    // Slots para los 6 nodos
    private val nodeSlots = listOf(10, 11, 12, 14, 15, 16)

    fun open(player: Player) {
        val clazz = PlayerClassManager.getPlayerClass(player)
        if (clazz == null) {
            player.sendMessage("§cNecesitas una clase para ver talentos.")
            return
        }

        val data = PlayerDataManager.getOrCreate(player)
        val nodes = SkillTree.getNodesForClass(clazz.id)
        if (nodes.isEmpty()) {
            player.sendMessage("§cNo hay talentos disponibles para tu clase.")
            return
        }

        val inv = Bukkit.createInventory(null, GUI_SIZE, Component.text(GUI_TITLE))

        // ── Fila superior: info ──
        inv.setItem(0, itemHead(player, "§e§l" + clazz.name))
        inv.setItem(2, item(Material.EXPERIENCE_BOTTLE, "§eNivel §f" + data.level, "§7" + data.exp + "§8/§7" + data.expToNextLevel + " §7EXP"))
        inv.setItem(4, item(Material.EMERALD, "§e§l" + data.talentPoints + " §ePuntos de talento", "§7Usa tus puntos para aprender nuevas habilidades"))
        inv.setItem(6, item(Material.FILLED_MAP, "§7Progreso EXP", barra(data.exp, data.expToNextLevel)))
        inv.setItem(8, item(Material.BARRIER, "§cCerrar"))

        // ── Separadores ──
        for (slot in listOf(1, 3, 5, 7)) {
            inv.setItem(slot, vidrio(Material.GRAY_STAINED_GLASS_PANE))
        }

        // ── Línea decorativa arriba de los nodos ──
        for (slot in 9..17) {
            if (inv.getItem(slot) == null) {
                inv.setItem(slot, vidrio(Material.BLACK_STAINED_GLASS_PANE))
            }
        }

        // ── Nodos ──
        nodes.forEachIndexed { i, node ->
            if (i < nodeSlots.size) {
                inv.setItem(nodeSlots[i], buildNodeItem(player, node))
            }
        }

        // ── Conexiones decorativas ──
        // Flecha entre war_damage_1 → war_damage_2 (slot 10 → 11)
        if (nodes.size > 1 && nodes[0].prerequisites.isEmpty() && nodes[1].prerequisites.contains(nodes[0].id)) {
            inv.setItem(19, item(Material.ARROW, "§8▼"))
        }

        // ── Fila inferior: decoración ──
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
                    Component.text("§7" + node.description).decoration(TextDecoration.ITALIC, false),
                    Component.text("").decoration(TextDecoration.ITALIC, false),
                    Component.text("§a§l¡APRENDIDO!").decoration(TextDecoration.ITALIC, false)
                ))
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
                item.itemMeta = meta
                item.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.UNBREAKING, 1)
                item
            }
            canUnlock -> {
                val item = ItemStack(node.material)
                val meta = item.itemMeta
                meta.displayName(Component.text("§e§l◉ " + node.name).decoration(TextDecoration.ITALIC, false))
                val lore = mutableListOf(
                    Component.text("§7" + node.description).decoration(TextDecoration.ITALIC, false),
                    Component.text("").decoration(TextDecoration.ITALIC, false),
                    Component.text("§e◆ CLIC PARA APRENDER").decoration(TextDecoration.ITALIC, false)
                )
                if (node.prerequisites.isNotEmpty()) {
                    val pre = node.prerequisites.mapNotNull { SkillTree.getNode(it)?.name }.joinToString(", ")
                    lore.add(Component.text("§8Requiere: §7" + pre).decoration(TextDecoration.ITALIC, false))
                }
                meta.lore(lore)
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
                item.itemMeta = meta
                item.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.UNBREAKING, 1)
                item
            }
            else -> {
                val item = ItemStack(Material.GRAY_DYE)
                val meta = item.itemMeta
                meta.displayName(Component.text("§8" + node.name).decoration(TextDecoration.ITALIC, false))
                val lore = mutableListOf(
                    Component.text("§7" + node.description).decoration(TextDecoration.ITALIC, false),
                    Component.text("").decoration(TextDecoration.ITALIC, false)
                )
                if (!check.can) {
                    lore.add(Component.text("§c" + check.reason).decoration(TextDecoration.ITALIC, false))
                } else if (!canAfford) {
                    lore.add(Component.text("§cNecesitas puntos de talento").decoration(TextDecoration.ITALIC, false))
                }
                meta.lore(lore)
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                item.itemMeta = meta
                item
            }
        }
    }

    // ── Click handler ──

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        if (event.inventory !== event.view.topInventory) return
        if (event.view.title() != Component.text(GUI_TITLE)) return

        event.isCancelled = true

        val player = event.whoClicked as? Player ?: return
        val slot = event.rawSlot

        // Cerrar
        if (slot == 8) {
            player.closeInventory()
            return
        }

        // Click en nodo
        val nodeIndex = nodeSlots.indexOf(slot)
        if (nodeIndex == -1) return

        val clazz = PlayerClassManager.getPlayerClass(player) ?: return
        val nodes = SkillTree.getNodesForClass(clazz.id)
        if (nodeIndex >= nodes.size) return

        val node = nodes[nodeIndex]
        val data = PlayerDataManager.getOrCreate(player)

        if (node.id in data.unlockedNodes) {
            player.sendActionBar(Component.text("§e✔ Ya tienes este talento"))
            return
        }

        val result = SkillTree.canUnlock(node.id, data.unlockedNodes)
        if (!result.can) {
            player.sendActionBar(Component.text("§c" + result.reason))
            return
        }

        if (data.talentPoints <= 0) {
            player.sendActionBar(Component.text("§cNo tienes puntos de talento"))
            return
        }

        val ok = PlayerDataManager.allocateNode(player, node.id)
        if (ok) {
            player.sendActionBar(Component.text("§a✔ Talento aprendido: §e" + node.name))
            player.closeInventory()
            open(player) // reabrir con datos actualizados
        }
    }

    // ── Helpers ──

    private fun item(mat: Material, name: String, vararg lore: String): ItemStack {
        val item = ItemStack(mat)
        val meta = item.itemMeta
        meta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false))
        meta.lore(lore.map { Component.text(it).decoration(TextDecoration.ITALIC, false) })
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        item.itemMeta = meta
        return item
    }

    private fun itemHead(player: Player, name: String): ItemStack {
        val item = ItemStack(Material.PLAYER_HEAD)
        val meta = item.itemMeta as SkullMeta
        meta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false))
        meta.setOwningPlayer(player)
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

    private fun barra(actual: Int, max: Int): String {
        val pct = (actual.toDouble() / max.toDouble()).coerceIn(0.0, 1.0)
        val llenos = (pct * 20).toInt()
        val vacios = 20 - llenos
        return "§a" + "|".repeat(llenos) + "§8" + "|".repeat(vacios) + " §7" + (pct * 100).toInt() + "%"
    }
}
