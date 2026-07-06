package org.lucma.openRPG.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.lucma.openRPG.gui.StatusGUI

class RPGCommand : CommandExecutor {

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

        StatusGUI.open(sender)
        return true
    }
}
