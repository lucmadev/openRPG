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
import org.bukkit.inventory.meta.SkullMeta
import org.lucma.openRPG.managers.PlayerClassManager
import org.lucma.openRPG.managers.PlayerDataManager
import org.lucma.openRPG.models.conditions.CloseEnemiesCondition
import org.lucma.openRPG.models.conditions.LowHealthCondition
import org.lucma.openRPG.models.conditions.NightTimeCondition
import org.lucma.openRPG.models.conditions.SneakingCondition
import org.lucma.openRPG.models.effects.CriticalChanceEffect
import org.lucma.openRPG.models.effects.CriticalDamageEffect
import org.lucma.openRPG.models.effects.DamageBonusEffect
import org.lucma.openRPG.models.effects.DefenseBonusEffect
import org.lucma.openRPG.models.effects.FireAuraEffect
import org.lucma.openRPG.models.effects.HealEffect
import org.lucma.openRPG.models.effects.LifeStealEffect
import org.lucma.openRPG.models.effects.SpeedBonusEffect
import org.lucma.openRPG.models.talents.SkillTree
import org.lucma.openRPG.models.types.Effect
import kotlin.math.roundToInt

object StatusGUI : Listener {

    private const val GUI_SIZE = 36
    private const val GUI_TITLE = "§8Estado RPG"

    fun open(player: Player) {
        val clazz = PlayerClassManager.getPlayerClass(player)
        val data = PlayerDataManager.getOrCreate(player)
        val inv = Bukkit.createInventory(null, GUI_SIZE, Component.text(GUI_TITLE))

        // ── Fondo ──
        for (i in 0 until GUI_SIZE) {
            inv.setItem(i, vidrio(Material.BLACK_STAINED_GLASS_PANE))
        }

        if (clazz == null) {
            // Sin clase
            inv.setItem(4, item(Material.BARRIER, "§c§lSIN CLASE", "§7Usa /class para seleccionar una"))
            inv.setItem(22, item(Material.ENDER_CHEST, "§eSeleccionar clase", "§7◆ CLIC para abrir"))
            inv.setItem(31, item(Material.OAK_DOOR, "§cCerrar"))
            player.openInventory(inv)
            return
        }

        // ════════════ Fila 1: info general ════════════
        // Cabeza del jugador con clase
        val head = ItemStack(Material.PLAYER_HEAD)
        val headMeta = head.itemMeta as SkullMeta
        headMeta.setOwningPlayer(player)
        headMeta.displayName(Component.text("§e§l" + clazz.name).decoration(TextDecoration.ITALIC, false))
        headMeta.lore(listOf(
            Component.text("§7" + player.getName()).decoration(TextDecoration.ITALIC, false)
        ))
        headMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        head.itemMeta = headMeta
        inv.setItem(0, head)

        // Nivel
        inv.setItem(2, item(Material.EXPERIENCE_BOTTLE, "§eNivel §f" + data.level, "§7" + data.exp + "§8/§7" + data.expToNextLevel + " §7EXP"))

        // Barra EXP como mapa
        val pct = (data.exp.toDouble() / data.expToNextLevel.toDouble()).coerceIn(0.0, 1.0)
        val barra = "§a" + "|".repeat((pct * 20).toInt()) + "§8" + "|".repeat(20 - (pct * 20).toInt())
        inv.setItem(4, item(Material.FILLED_MAP, "§7Progreso", barra, "§7" + (pct * 100).toInt() + "% al nivel " + (data.level + 1)))

        // Puntos de talento
        inv.setItem(6, item(Material.EMERALD, "§e" + data.talentPoints + " §ePuntos de talento", "§7◆ CLIC para abrir árbol"))

        // Cerrar
        inv.setItem(8, item(Material.OAK_DOOR, "§cCerrar"))

        // ════════════ Fila 2: Stats ════════════
        inv.setItem(9,  item(Material.RED_DYE,      "§cDaño",      "§7Multiplicador: §fx" + String.format("%.2f", 1.0))) // base sin aplicar
        inv.setItem(11, item(Material.BLUE_DYE,     "§9Defensa",   "§7Multiplicador: §fx" + String.format("%.2f", 1.0)))
        inv.setItem(13, item(Material.WHITE_DYE,    "§fVelocidad", "§7Multiplicador: §fx" + String.format("%.2f", 1.0)))
        inv.setItem(15, item(Material.ORANGE_DYE,   "§6Crítico",   "§7Probabilidad: §e" + (0.0 * 100).roundToInt() + "%"))
        inv.setItem(17, item(Material.YELLOW_DYE,   "§eMulti crítico", "§7Multiplicador: §fx" + String.format("%.1f", 1.0)))

        // ════════════ Fila 3: Modificadores activos ════════════
        var slot = 18
        // Modifiers de clase
        for (mod in clazz.modifiers) {
            if (slot >= 26) break
            val desc = describeEffect(mod.effect) + " §8→ §7" + describeCondition(mod.condition)
            inv.setItem(slot, item(Material.ENCHANTED_BOOK, "§eModificador", desc))
            slot++
        }
        // Modifiers de talentos desbloqueados
        if (data.unlockedNodes.isNotEmpty()) {
            if (slot < 26) {
                inv.setItem(slot, vidrio(Material.GRAY_STAINED_GLASS_PANE))
                slot++
            }
            val talentMods = SkillTree.getModifiers(data.unlockedNodes)
            for (mod in talentMods) {
                if (slot >= 26) break
                val desc = describeEffect(mod.effect) + " §8→ §7" + describeCondition(mod.condition)
                inv.setItem(slot, item(Material.LIME_DYE, "§a✔ Talento", desc))
                slot++
            }
        }
        // Rellenar resto de fila
        while (slot < 26) {
            inv.setItem(slot, vidrio(Material.BLACK_STAINED_GLASS_PANE))
            slot++
        }

        // ════════════ Fila 4: Botones ════════════
        inv.setItem(27, item(Material.ENDER_CHEST, "§eCambiar clase", "§7◆ CLIC para cambiar"))
        inv.setItem(31, item(Material.EMERALD_BLOCK, "§eÁrbol de talentos", "§7◆ CLIC para abrir"))
        inv.setItem(35, item(Material.OAK_DOOR, "§cCerrar"))

        player.openInventory(inv)
    }

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        if (event.inventory !== event.view.topInventory) return
        if (event.view.title() != Component.text(GUI_TITLE)) return

        event.isCancelled = true
        val player = event.whoClicked as? Player ?: return

        when (event.rawSlot) {
            8, 35 -> player.closeInventory()                  // Cerrar
            6, 31 -> { player.closeInventory(); TalentGUI.open(player) }  // Árbol talentos
            22, 27 -> { player.closeInventory(); ClassSelectionGUI.open(player) }  // Cambiar clase
        }
    }

    // ── Helpers de descripción (copia de RPGCommand) ──

    private fun describeCondition(condition: Any): String {
        return when (condition) {
            is CloseEnemiesCondition -> "cerca de enemigos"
            is NightTimeCondition -> "de noche"
            is LowHealthCondition -> "vida < " + (condition.thresholdPercentage * 100).roundToInt() + "%"
            is SneakingCondition -> "agachado"
            else -> condition::class.simpleName ?: "?"
        }
    }

    private fun describeEffect(effect: Effect): String {
        return when (effect) {
            is DamageBonusEffect -> "§a+" + (effect.bonus * 100).roundToInt() + "% daño"
            is DefenseBonusEffect -> "§b+" + (effect.bonus * 100).roundToInt() + "% defensa"
            is SpeedBonusEffect -> "§a+" + (effect.bonus * 100).roundToInt() + "% velocidad"
            is HealEffect -> "§c❤ +" + effect.amount + " vida"
            is LifeStealEffect -> "§d" + (effect.stealPercentage * 100).roundToInt() + "% robo vida"
            is FireAuraEffect -> "§6🔥 " + effect.duration + "s ígneo"
            is CriticalChanceEffect -> "§e+" + (effect.chance * 100).roundToInt() + "% prob. crítico"
            is CriticalDamageEffect -> "§e+" + (effect.bonus * 100).roundToInt() + "% daño crítico"
            else -> "§f" + (effect::class.simpleName ?: "?")
        }
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
