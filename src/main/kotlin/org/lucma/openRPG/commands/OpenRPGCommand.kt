package org.lucma.openRPG.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.lucma.openRPG.core.LanguageManager.msg
import org.lucma.openRPG.core.registry.ClassRegistry
import org.lucma.openRPG.gui.ClassSelectionGUI
import org.lucma.openRPG.gui.StatusGUI
import org.lucma.openRPG.gui.TalentGUI
import org.lucma.openRPG.managers.PlayerClassManager

/**
 * Comando único /openrpg con subcomandos.
 *
 *   /openrpg               → ayuda
 *   /openrpg status        → GUI de estado
 *   /openrpg class         → GUI de selección de clase
 *   /openrpg class <id>    → asigna clase directamente
 *   /openrpg talent        → GUI de árbol de talentos
 *   /openrpg help          → esta ayuda
 */
class OpenRPGCommand : CommandExecutor {

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage(msg("command.player_only"))
            return true
        }

        val sub = args.firstOrNull()?.lowercase() ?: "help"

        when (sub) {
            "status", "stats", "s" -> {
                StatusGUI.open(sender)
            }
            "class", "clase", "c" -> {
                val classId = args.getOrNull(1)
                if (classId != null) {
                    assignDirect(sender, classId)
                } else {
                    ClassSelectionGUI.open(sender)
                }
            }
            "talent", "talento", "t", "skill" -> {
                TalentGUI.open(sender)
            }
            "help", "h", "?" -> {
                showHelp(sender, label)
            }
            else -> {
                sender.sendMessage("§cSubcomando desconocido: §f" + sub)
                sender.sendMessage("§7Usa §e/" + label + " help§7 para ver los disponibles.")
            }
        }

        return true
    }

    private fun assignDirect(player: Player, classId: String) {
        val clazz = ClassRegistry.get(classId)
        if (clazz == null) {
            player.sendMessage(msg("command.class.not_found", player, classId))
            return
        }
        PlayerClassManager.setPlayerClass(player, clazz)
        player.sendMessage(msg("command.class.assigned", player, clazz.name))
    }

    private fun showHelp(player: Player, label: String) {
        player.sendMessage("")
        player.sendMessage("§6§l╔══════════════════════════════╗")
        player.sendMessage("§6§l║      §e§lopenRPG Ayuda       §6§l║")
        player.sendMessage("§6§l╚══════════════════════════════╝")
        player.sendMessage("")
        player.sendMessage("§e/" + label + " §7- Muestra esta ayuda")
        player.sendMessage("§e/" + label + " status §7- Estado del jugador")
        player.sendMessage("§e/" + label + " class §7- Seleccionar clase (GUI)")
        player.sendMessage("§e/" + label + " class <id> §7- Asignar clase directa")
        player.sendMessage("§e/" + label + " talent §7- Árbol de talentos")
        player.sendMessage("")
        player.sendMessage("§7Clases disponibles:")
        ClassRegistry.all().forEach { c ->
            player.sendMessage("  §e" + c.id + "§8 → §f" + c.name)
        }
        player.sendMessage("")
    }
}
