package org.lucma.openRPG.commands

import org.bukkit.Bukkit
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
 * Single /openrpg command with subcommands.
 *
 *   /openrpg               → help
 *   /openrpg status        → Status GUI
 *   /openrpg class         → Class selection GUI
 *   /openrpg class <id>    → Direct class assignment
 *   /openrpg talent        → Talent tree GUI
 *   /openrpg party [...]   → Party commands
 *   /openrpg help          → This help
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

            "party", "p", "grupo", "g" -> {
                val partyArgs = args.drop(1).toTypedArray()
                val fakeCommand = Bukkit.getPluginCommand("party")
                if (fakeCommand != null) {
                    PartyCommand().onCommand(sender, fakeCommand, "party", partyArgs)
                }
            }

            "help", "h", "?" -> {
                showHelp(sender, label)
            }

            else -> {
                sender.sendMessage(msg("command.help.unknown", sender, sub))
                sender.sendMessage(msg("command.help.use", sender, label))
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
        player.sendMessage("§6§l╔═════════════════╗")
        player.sendMessage("§6§l║         §e§l" + msg("command.help.title", player) + "       §6§l║")
        player.sendMessage("§6§l╚═════════════════╝")
        player.sendMessage("")
        player.sendMessage(msg("command.help.self", player, label))
        player.sendMessage(msg("command.help.status", player, label))
        player.sendMessage(msg("command.help.class_gui", player, label))
        player.sendMessage(msg("command.help.class_assign", player, label))
        player.sendMessage(msg("command.help.talent", player, label))
        player.sendMessage(msg("command.help.party", player, label))
        player.sendMessage("")
        player.sendMessage(msg("command.help.available_classes", player))
        ClassRegistry.all().forEach { c ->
            player.sendMessage(msg("command.help.class_entry", player, c.id, c.name))
        }
        player.sendMessage("")
    }
}
