package org.lucma.openRPG.gui

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.attribute.Attribute
import kotlin.math.roundToInt
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
import org.lucma.openRPG.core.registry.ClassRegistry
import org.lucma.openRPG.managers.PlayerClassManager
import org.lucma.openRPG.models.PlayerClass

object ClassSelectionGUI : Listener {

    private const val GUI_SIZE = 27

    private val classMaterials = mapOf(
        "warrior"  to Material.IRON_SWORD,
        "mage"     to Material.ENDER_PEARL,
        "assassin" to Material.NETHERITE_SWORD
    )

    private val classSlots = mapOf(
        "warrior"  to 11,
        "mage"     to 13,
        "assassin" to 15
    )

    fun open(player: Player) {
        val title = msg("gui.class_selection.title", player)
        val inv = Bukkit.createInventory(null, GUI_SIZE, Component.text(title))

        for (i in 0 until GUI_SIZE) {
            inv.setItem(i, vidrio(Material.GRAY_STAINED_GLASS_PANE))
        }

        ClassRegistry.all().forEach { clazz ->
            val slot = classSlots[clazz.id] ?: return@forEach
            val current = PlayerClassManager.getPlayerClass(player)
            val isSelected = current?.id == clazz.id
            inv.setItem(slot, buildClassItem(player, clazz, isSelected))
        }

        val current = PlayerClassManager.getPlayerClass(player)
        if (current != null) {
            inv.setItem(22, item(Material.BOOK, msg("gui.class_selection.current", player, current.name), ""))
        } else {
            inv.setItem(22, item(Material.BARRIER, msg("gui.class_selection.none", player), msg("gui.class_selection.no_class_hint", player)))
        }

        inv.setItem(26, item(Material.OAK_DOOR, msg("gui.class_selection.close", player)))
        player.openInventory(inv)
    }

    private fun buildClassItem(player: Player, clazz: PlayerClass, selected: Boolean): ItemStack {
        val mat = classMaterials[clazz.id] ?: Material.ENCHANTED_BOOK
        val item = ItemStack(if (selected) Material.PLAYER_HEAD else mat)
        val meta = item.itemMeta

        if (selected) {
            val skull = meta as SkullMeta
            skull.setOwningPlayer(player)
            skull.displayName(Component.text(msg("gui.class_selection.already_have", player)).decoration(TextDecoration.ITALIC, false))
            val maxHp = player.getAttribute(Attribute.MAX_HEALTH)?.value?.roundToInt() ?: 20
            skull.lore(listOf(
            Component.text("§8" + player.getName()).decoration(TextDecoration.ITALIC, false),
            Component.text("").decoration(TextDecoration.ITALIC, false),
            Component.text("§c❤ §7" + player.health.roundToInt() + "§8/§c" + maxHp).decoration(TextDecoration.ITALIC, false),
            Component.text("§6🍗 §7" + player.foodLevel + "§8/§6" + 20).decoration(TextDecoration.ITALIC, false),
            Component.text("§b✦ §7Nivel §f" + player.level).decoration(TextDecoration.ITALIC, false)
        ))
            skull.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
            item.itemMeta = skull
            item.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.UNBREAKING, 1)
        } else {
            meta.displayName(Component.text(msg("class." + clazz.id + ".name", player)).decoration(TextDecoration.ITALIC, false))
            val desc = msg("class." + clazz.id + ".desc", player).replace("\\n", "\n")
            meta.lore(listOf(
                Component.text(desc).decoration(TextDecoration.ITALIC, false),
                Component.text("").decoration(TextDecoration.ITALIC, false),
                Component.text(msg("gui.class_selection.select_hint", player)).decoration(TextDecoration.ITALIC, false)
            ))
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
            item.itemMeta = meta
        }

        return item
    }

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        if (event.inventory !== event.view.topInventory) return
        val titleStr = PlainTextComponentSerializer.plainText().serialize(event.view.title())
        if (!titleStr.contains("Select") && !titleStr.contains("Seleccionar")) return

        event.isCancelled = true
        val player = event.whoClicked as? Player ?: return

        if (event.rawSlot == 26) {
            player.closeInventory()
            return
        }

        val classId = classSlots.entries.firstOrNull { it.value == event.rawSlot }?.key ?: return
        val clazz = ClassRegistry.get(classId) ?: return

        val current = PlayerClassManager.getPlayerClass(player)
        if (current?.id == classId) {
            player.sendActionBar(Component.text(msg("gui.class_selection.already_have", player)))
            return
        }

        PlayerClassManager.setPlayerClass(player, clazz)
        player.sendActionBar(Component.text(msg("gui.class_selection.assigned", player, clazz.name)))
        player.closeInventory()
        open(player)
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

    private fun vidrio(mat: Material): ItemStack {
        val item = ItemStack(mat)
        val meta = item.itemMeta
        meta.displayName(Component.text("").decoration(TextDecoration.ITALIC, false))
        item.itemMeta = meta
        return item
    }
}
