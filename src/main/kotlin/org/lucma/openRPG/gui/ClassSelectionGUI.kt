package org.lucma.openRPG.gui

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.lucma.openRPG.core.registry.ClassRegistry
import org.lucma.openRPG.managers.PlayerClassManager
import org.lucma.openRPG.models.PlayerClass

object ClassSelectionGUI : Listener {

    private const val GUI_SIZE = 27
    private const val GUI_TITLE = "§8Seleccionar Clase"

    private val classMaterials = mapOf(
        "warrior"  to Material.IRON_SWORD,
        "mage"     to Material.ENDER_PEARL,
        "assassin" to Material.NETHERITE_SWORD
    )

    // Slot de cada clase en el inventario
    private val classSlots = mapOf(
        "warrior"  to 11,
        "mage"     to 13,
        "assassin" to 15
    )

    fun open(player: Player) {
        val inv = Bukkit.createInventory(null, GUI_SIZE, Component.text(GUI_TITLE))

        // ── Fondo ──
        for (i in 0 until GUI_SIZE) {
            inv.setItem(i, vidrio(Material.GRAY_STAINED_GLASS_PANE))
        }

        // ── Clases ──
        ClassRegistry.all().forEach { clazz ->
            val slot = classSlots[clazz.id] ?: return@forEach
            val current = PlayerClassManager.getPlayerClass(player)
            val isSelected = current?.id == clazz.id
            inv.setItem(slot, buildClassItem(player, clazz, isSelected))
        }

        // ── Info ──
        val current = PlayerClassManager.getPlayerClass(player)
        if (current != null) {
            inv.setItem(22, item(Material.BOOK, "§eTu clase actual: §f" + current.name, "§7Usa /talent para ver tus habilidades"))
        } else {
            inv.setItem(22, item(Material.BARRIER, "§cSin clase asignada", "§7Selecciona una clase arriba"))
        }

        // ── Cerrar ──
        inv.setItem(26, item(Material.OAK_DOOR, "§cCerrar"))

        player.openInventory(inv)
    }

    private fun buildClassItem(player: Player, clazz: PlayerClass, selected: Boolean): ItemStack {
        val mat = classMaterials[clazz.id] ?: Material.ENCHANTED_BOOK
        val item = ItemStack(if (selected) Material.PLAYER_HEAD else mat)
        val meta = item.itemMeta

        if (selected) {
            val skull = meta as org.bukkit.inventory.meta.SkullMeta
            skull.setOwningPlayer(player)
            skull.displayName(Component.text("§a§l✔ " + clazz.name).decoration(TextDecoration.ITALIC, false))
            skull.lore(listOf(
                Component.text("§7Clase actual").decoration(TextDecoration.ITALIC, false),
                Component.text("").decoration(TextDecoration.ITALIC, false),
                Component.text("§8Ya tienes esta clase").decoration(TextDecoration.ITALIC, false)
            ))
            skull.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
            item.itemMeta = skull
            item.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.UNBREAKING, 1)
        } else {
            val name = clazz.name
            val desc = when (clazz.id) {
                "warrior"  -> "§7Fuerza bruta. Bonos cuerpo a cuerpo\n§7cerca de enemigos."
                "mage"     -> "§7Poder arcano. Bonos de noche\n§7y defensa mágica."
                "assassin" -> "§7Sigilo letal. Bonos al agacharte\n§7y robo de vida."
                else -> "§7Una clase misteriosa..."
            }
            meta.displayName(Component.text("§e§l" + name).decoration(TextDecoration.ITALIC, false))
            meta.lore(listOf(
                Component.text(desc).decoration(TextDecoration.ITALIC, false),
                Component.text("").decoration(TextDecoration.ITALIC, false),
                Component.text("§e◆ CLIC PARA SELECCIONAR").decoration(TextDecoration.ITALIC, false)
            ))
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
            item.itemMeta = meta
        }

        return item
    }

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        if (event.inventory !== event.view.topInventory) return
        if (event.view.title() != Component.text(GUI_TITLE)) return

        event.isCancelled = true
        val player = event.whoClicked as? Player ?: return
        val slot = event.rawSlot

        // Cerrar
        if (slot == 26) {
            player.closeInventory()
            return
        }

        // Ver si el slot corresponde a una clase
        val classId = classSlots.entries.firstOrNull { it.value == slot }?.key ?: return
        val clazz = ClassRegistry.get(classId) ?: return

        val current = PlayerClassManager.getPlayerClass(player)
        if (current?.id == classId) {
            player.sendActionBar(Component.text("§e✔ Ya tienes esta clase"))
            return
        }

        PlayerClassManager.setPlayerClass(player, clazz)
        player.sendActionBar(Component.text("§aClase asignada: §e" + clazz.name))
        player.closeInventory()
        open(player) // reabrir con checkmark actualizado
    }

    private fun item(mat: Material, name: String, vararg lore: String): ItemStack {
        val item = ItemStack(mat)
        val meta = item.itemMeta
        meta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false))
        meta.lore(lore.map { Component.text(it).decoration(TextDecoration.ITALIC, false) })
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
