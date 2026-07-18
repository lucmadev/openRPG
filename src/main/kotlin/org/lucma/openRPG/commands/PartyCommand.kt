package org.lucma.openRPG.commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.lucma.openRPG.core.LanguageManager.msg
import org.lucma.openRPG.core.PartyManager
import org.lucma.openRPG.events.*

class PartyCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(msg("command.player_only"))
            return true
        }

        val player = sender

        if (args.isEmpty()) {
            sendHelp(player)
            return true
        }

        when (args[0].lowercase()) {
            "create" -> handleCreate(player)
            "invite" -> handleInvite(player, args)
            "accept" -> handleAccept(player, args)
            "decline" -> handleDecline(player, args)
            "leave" -> handleLeave(player)
            "kick" -> handleKick(player, args)
            "disband" -> handleDisband(player)
            "transfer" -> handleTransfer(player, args)
            "list" -> handleList(player)
            "help" -> sendHelp(player)
            else -> sendHelp(player)
        }
        return true
    }

    private fun handleCreate(player: Player) {
        if (PartyManager.isInParty(player)) {
            player.sendMessage(msg("party.create.already_in_party"))
            return
        }
        val party = PartyManager.createParty(player)
        player.sendMessage(msg("party.create.created"))
        Bukkit.getLogger().info("[openRPG] Party created: ${party.id} by ${player.name}")
    }

    private fun handleInvite(player: Player, args: Array<out String>) {
        if (args.size < 2) {
            player.sendMessage(msg("party.invite.usage"))
            return
        }
        val party = PartyManager.getParty(player) ?: run {
            player.sendMessage(msg("party.error.not_in_party"))
            return
        }
        if (!party.isLeader(player)) {
            player.sendMessage(msg("party.error.not_leader"))
            return
        }
        val target = Bukkit.getPlayer(args[1]) ?: run {
            player.sendMessage(msg("party.invite.player_not_found"))
            return
        }
        if (target.uniqueId == player.uniqueId) {
            player.sendMessage(msg("party.invite.self"))
            return
        }
        if (PartyManager.isInParty(target)) {
            player.sendMessage(msg("party.invite.already_in_party"))
            return
        }

        // Fire cancellable pre-invite event
        val preEvent = PartyPreInviteEvent(party, player, target)
        Bukkit.getPluginManager().callEvent(preEvent)
        if (preEvent.isCancelled) return

        if (!PartyManager.invitePlayer(player, target)) return

        // Fire invite event
        Bukkit.getPluginManager().callEvent(PartyInviteEvent(party, player, target))

        player.sendMessage(msg("party.invite.sent", target.name))
        target.sendMessage(msg("party.invite.received", player.name))
    }

    private fun handleAccept(player: Player, args: Array<out String>) {
        if (PartyManager.isInParty(player)) {
            player.sendMessage(msg("party.error.already_in_party"))
            return
        }
        val invites = PartyManager.getInvites(player)
        if (invites.isEmpty()) {
            player.sendMessage(msg("party.invite.no_invites"))
            return
        }

        // If a player name was provided, accept that specific invite
        val invite = if (args.size >= 2) {
            val inviterName = args[1]
            invites.find { it.inviter.name.equals(inviterName, ignoreCase = true) }
        } else {
            invites.firstOrNull()
        }

        if (invite == null) {
            player.sendMessage(msg("party.invite.not_found"))
            return
        }
        if (invite.isExpired()) {
            player.sendMessage(msg("party.invite.expired"))
            PartyManager.declineInvite(player, invite.party.id)
            return
        }

        if (PartyManager.acceptInvite(player, invite.party.id)) {
            // Fire join event
            Bukkit.getPluginManager().callEvent(PartyJoinEvent(invite.party, player))

            player.sendMessage(msg("party.join.you_joined"))
            invite.party.members.forEach { member ->
                if (member.uniqueId != player.uniqueId) {
                    member.sendMessage(msg("party.join.joined", player.name))
                }
            }
        }
    }

    private fun handleDecline(player: Player, args: Array<out String>) {
        val invites = PartyManager.getInvites(player)
        if (invites.isEmpty()) {
            player.sendMessage(msg("party.invite.no_invites"))
            return
        }

        val invite = if (args.size >= 2) {
            val inviterName = args[1]
            invites.find { it.inviter.name.equals(inviterName, ignoreCase = true) }
        } else {
            invites.firstOrNull()
        }

        if (invite == null) {
            player.sendMessage(msg("party.invite.not_found"))
            return
        }

        PartyManager.declineInvite(player, invite.party.id)
        player.sendMessage(msg("party.decline.declined"))
        invite.inviter.sendMessage(msg("party.decline.notified", player.name))
    }

    private fun handleLeave(player: Player) {
        val party = PartyManager.getParty(player) ?: run {
            player.sendMessage(msg("party.error.not_in_party"))
            return
        }
        val wasLeader = party.isLeader(player)
        val oldLeader = party.leader

        PartyManager.leaveParty(player)

        // Fire leave event
        Bukkit.getPluginManager()
            .callEvent(PartyLeaveEvent(party, player, org.lucma.openRPG.models.party.LeaveReason.VOLUNTARY))

        player.sendMessage(msg("party.leave.you_left"))
        party.members.forEach { member ->
            member.sendMessage(msg("party.leave.left", player.name))
        }

        // If leadership was transferred, notify
        if (wasLeader && party.size > 0) {
            val newLeader = party.leader
            Bukkit.getPluginManager().callEvent(PartyLeaderChangeEvent(party, oldLeader, newLeader))
            party.members.forEach { member ->
                if (member.uniqueId == newLeader.uniqueId) {
                    member.sendMessage(msg("party.transfer.you_are_leader"))
                } else {
                    member.sendMessage(msg("party.transfer.transferred", newLeader.name))
                }
            }
        }
    }

    private fun handleKick(player: Player, args: Array<out String>) {
        if (args.size < 2) {
            player.sendMessage(msg("party.kick.usage"))
            return
        }
        val party = PartyManager.getParty(player) ?: run {
            player.sendMessage(msg("party.error.not_in_party"))
            return
        }
        if (!party.isLeader(player)) {
            player.sendMessage(msg("party.error.not_leader"))
            return
        }
        val target = Bukkit.getPlayer(args[1]) ?: run {
            player.sendMessage(msg("party.kick.player_not_found"))
            return
        }
        if (!party.contains(target)) {
            player.sendMessage(msg("party.error.not_in_your_party"))
            return
        }
        if (target.uniqueId == player.uniqueId) {
            player.sendMessage(msg("party.kick.self"))
            return
        }

        PartyManager.kickPlayer(player, target)

        // Fire leave event
        Bukkit.getPluginManager().callEvent(
            PartyLeaveEvent(party, target, org.lucma.openRPG.models.party.LeaveReason.KICKED)
        )

        target.sendMessage(msg("party.kick.you_were_kicked"))
        party.members.forEach { member ->
            member.sendMessage(msg("party.kick.kicked", target.name))
        }
    }

    private fun handleDisband(player: Player) {
        val party = PartyManager.getParty(player) ?: run {
            player.sendMessage(msg("party.error.not_in_party"))
            return
        }
        if (!party.isLeader(player)) {
            player.sendMessage(msg("party.error.not_leader"))
            return
        }

        // Fire disband event before clearing
        Bukkit.getPluginManager().callEvent(PartyDisbandEvent(party))

        val members = party.members.toList()
        PartyManager.disbandParty(player)

        members.forEach { member ->
            member.sendMessage(msg("party.disband.disbanded"))
        }
    }

    private fun handleTransfer(player: Player, args: Array<out String>) {
        if (args.size < 2) {
            player.sendMessage(msg("party.transfer.usage"))
            return
        }
        val party = PartyManager.getParty(player) ?: run {
            player.sendMessage(msg("party.error.not_in_party"))
            return
        }
        if (!party.isLeader(player)) {
            player.sendMessage(msg("party.error.not_leader"))
            return
        }
        val target = Bukkit.getPlayer(args[1]) ?: run {
            player.sendMessage(msg("party.transfer.player_not_found"))
            return
        }
        if (!party.contains(target)) {
            player.sendMessage(msg("party.error.not_in_your_party"))
            return
        }
        if (target.uniqueId == player.uniqueId) {
            player.sendMessage(msg("party.transfer.self"))
            return
        }

        val oldLeader = party.leader
        PartyManager.transferLeadership(player, target)

        // Fire leader change event
        Bukkit.getPluginManager().callEvent(PartyLeaderChangeEvent(party, oldLeader, target))

        party.members.forEach { member ->
            if (member.uniqueId == target.uniqueId) {
                member.sendMessage(msg("party.transfer.you_are_leader"))
            } else {
                member.sendMessage(msg("party.transfer.transferred", target.name))
            }
        }
    }

    private fun handleList(player: Player) {
        val party = PartyManager.getParty(player) ?: run {
            player.sendMessage(msg("party.error.not_in_party"))
            return
        }

        player.sendMessage(msg("party.list.header", party.size.toString(), party.maxSize.toString()))
        for (member in party.members) {
            val isOnline = member.isOnline
            val status = if (isOnline) msg("party.list.online") else msg("party.list.offline")
            if (party.isLeader(member)) {
                player.sendMessage(msg("party.list.leader", member.name) + " $status")
            } else {
                player.sendMessage(msg("party.list.member", member.name) + " $status")
            }
        }
    }

    private fun sendHelp(player: Player) {
        player.sendMessage(msg("party.help.title"))
        player.sendMessage(msg("party.help.create"))
        player.sendMessage(msg("party.help.invite"))
        player.sendMessage(msg("party.help.accept"))
        player.sendMessage(msg("party.help.decline"))
        player.sendMessage(msg("party.help.leave"))
        player.sendMessage(msg("party.help.kick"))
        player.sendMessage(msg("party.help.disband"))
        player.sendMessage(msg("party.help.transfer"))
        player.sendMessage(msg("party.help.list"))
    }
}
