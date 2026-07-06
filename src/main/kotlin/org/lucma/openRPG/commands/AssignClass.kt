package org.lucma.openRPG.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.lucma.openRPG.core.registry.ClassRegistry
import org.lucma.openRPG.gui.ClassSelectionGUI
import org.lucma.openRPG.managers.PlayerClassManager
import org.lucma.openRPG.models.PlayerClass

class AssignClass : CommandExecutor {

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§cEste comando solo puede ser usado por jugadores.")
            return true
        }

        // Sin args → abre GUI
        if (args.isEmpty()) {
            ClassSelectionGUI.open(sender)
            return true
        }

        // Con args → asignación directa por ID
        val classId = args[0]
        val playerClass = ClassRegistry.get(classId)

        if (playerClass == null) {
            sender.sendMessage("§cNo se encontró la clase '§f$classId§c'.")
            return true
        }

        PlayerClassManager.setPlayerClass(sender, playerClass)
        sender.sendMessage("§aSe te ha asignado la clase §e${playerClass.name}§a.")
        return true
    }
}
