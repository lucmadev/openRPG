package org.lucma.openRPG.commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.lucma.openRPG.core.LanguageManager.msg
import org.lucma.openRPG.core.LanguageManager.msgComponent
import org.lucma.openRPG.core.PartyManager
import org.lucma.openRPG.events.*

class PartyCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(msg("command.player_only", null))
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
            player.sendMessage(msg("party.create.already_in_party", player))
            return
        }
        val party = PartyManager.createParty(player)
        player.sendMessage(msg("party.create.created", player))
        Bukkit.getLogger().info("[openRPG] Party created: ${party.id} by ${player.name}")
    }

    private fun handleInvite(player: Player, args: Array<out String>) {
        if (args.size < 2) {
            player.sendMessage(msg("party.invite.usage", player))
            return
        }
        val party = PartyManager.getParty(player) ?: run {
            player.sendMessage(msg("party.error.not_in_party", player))
            return
        }
        if (!party.isLeader(player)) {
            player.sendMessage(msg("party.error.not_leader", player))
            return
        }
        val target = Bukkit.getPlayer(args[1]) ?: run {
            player.sendMessage(msg("party.invite.player_not_found", player))
            return
        }
        if (target.uniqueId == player.uniqueId) {
            player.sendMessage(msg("party.invite.self", player))
            return
        }
        if (PartyManager.isInParty(target)) {
            player.sendMessage(msg("party.invite.already_in_party", player))
            return
        }

        // Fire cancellable pre-invite event
        val preEvent = PartyPreInviteEvent(party, player, target)
        Bukkit.getPluginManager().callEvent(preEvent)
        if (preEvent.isCancelled) return

        if (!PartyManager.invitePlayer(player, target)) return

        // Fire invite event
        Bukkit.getPluginManager().callEvent(PartyInviteEvent(party, player, target))

        player.sendMessage(msgComponent("party.invite.sent", player, target))
        target.sendMessage(msgComponent("party.invite.received", target, player))
    }

    private fun handleAccept(player: Player, args: Array<out String>) {
        if (PartyManager.isInParty(player)) {
            player.sendMessage(msg("party.error.already_in_party", player))
            return
        }
        val invites = PartyManager.getInvites(player)
        if (invites.isEmpty()) {
            player.sendMessage(msg("party.invite.no_invites", player))
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
            player.sendMessage(msg("party.invite.not_found", player))
            return
        }
        if (invite.isExpired()) {
            player.sendMessage(msg("party.invite.expired", player))
            PartyManager.declineInvite(player, invite.party.id)
            return
        }

        if (PartyManager.acceptInvite(player, invite.party.id)) {
            // Fire join event
            Bukkit.getPluginManager().callEvent(PartyJoinEvent(invite.party, player))

            player.sendMessage(msg("party.join.you_joined", player))
            invite.party.members.forEach { member ->
                if (member.uniqueId != player.uniqueId) {
                    member.sendMessage(msgComponent("party.join.joined", member, player))
                }
            }
        }
    }

    private fun handleDecline(player: Player, args: Array<out String>) {
        val invites = PartyManager.getInvites(player)
        if (invites.isEmpty()) {
            player.sendMessage(msg("party.invite.no_invites", player))
            return
        }

        val invite = if (args.size >= 2) {
            val inviterName = args[1]
            invites.find { it.inviter.name.equals(inviterName, ignoreCase = true) }
        } else {
            invites.firstOrNull()
        }

        if (invite == null) {
            player.sendMessage(msg("party.invite.not_found", player))
            return
        }

        PartyManager.declineInvite(player, invite.party.id)
        player.sendMessage(msg("party.decline.declined", player))
        invite.inviter.sendMessage(msgComponent("party.decline.notified", invite.inviter, player))
    }

    private fun handleLeave(player: Player) {
        val party = PartyManager.getParty(player) ?: run {
            player.sendMessage(msg("party.error.not_in_party", player))
            return
        }
        val wasLeader = party.isLeader(player)
        val oldLeader = party.leader

        PartyManager.leaveParty(player)

        // Fire leave event
        Bukkit.getPluginManager()
            .callEvent(PartyLeaveEvent(party, player, org.lucma.openRPG.models.party.LeaveReason.VOLUNTARY))

        player.sendMessage(msg("party.leave.you_left", player))
        party.members.forEach { member ->
            member.sendMessage(msgComponent("party.leave.left", member, player))
        }

        // If leadership was transferred, notify
        if (wasLeader && party.size > 0) {
            val newLeader = party.leader
            Bukkit.getPluginManager().callEvent(PartyLeaderChangeEvent(party, oldLeader, newLeader))
            party.members.forEach { member ->
                if (member.uniqueId == newLeader.uniqueId) {
                    member.sendMessage(msg("party.transfer.you_are_leader", member))
                } else {
                    member.sendMessage(msgComponent("party.transfer.transferred", member, newLeader))
                }
            }
        }
    }

    private fun handleKick(player: Player, args: Array<out String>) {
        if (args.size < 2) {
            player.sendMessage(msg("party.kick.usage", player))
            return
        }
        val party = PartyManager.getParty(player) ?: run {
            player.sendMessage(msg("party.error.not_in_party", player))
            return
        }
        if (!party.isLeader(player)) {
            player.sendMessage(msg("party.error.not_leader", player))
            return
        }
        val target = Bukkit.getPlayer(args[1]) ?: run {
            player.sendMessage(msg("party.kick.player_not_found", player))
            return
        }
        if (!party.contains(target)) {
            player.sendMessage(msg("party.error.not_in_your_party", player))
            return
        }
        if (target.uniqueId == player.uniqueId) {
            player.sendMessage(msg("party.kick.self", player))
            return
        }

        PartyManager.kickPlayer(player, target)

        // Fire leave event
        Bukkit.getPluginManager().callEvent(
            PartyLeaveEvent(party, target, org.lucma.openRPG.models.party.LeaveReason.KICKED)
        )

        target.sendMessage(msg("party.kick.you_were_kicked", target))
        party.members.forEach { member ->
            member.sendMessage(msgComponent("party.kick.kicked", member, target))
        }
    }

    private fun handleDisband(player: Player) {
        val party = PartyManager.getParty(player) ?: run {
            player.sendMessage(msg("party.error.not_in_party", player))
            return
        }
        if (!party.isLeader(player)) {
            player.sendMessage(msg("party.error.not_leader", player))
            return
        }

        // Fire disband event before clearing
        Bukkit.getPluginManager().callEvent(PartyDisbandEvent(party))

        val members = party.members.toList()
        PartyManager.disbandParty(player)

        members.forEach { member ->
            member.sendMessage(msg("party.disband.disbanded", member))
        }
    }

    private fun handleTransfer(player: Player, args: Array<out String>) {
        if (args.size < 2) {
            player.sendMessage(msg("party.transfer.usage", player))
            return
        }
        val party = PartyManager.getParty(player) ?: run {
            player.sendMessage(msg("party.error.not_in_party", player))
            return
        }
        if (!party.isLeader(player)) {
            player.sendMessage(msg("party.error.not_leader", player))
            return
        }
        val target = Bukkit.getPlayer(args[1]) ?: run {
            player.sendMessage(msg("party.transfer.player_not_found", player))
            return
        }
        if (!party.contains(target)) {
            player.sendMessage(msg("party.error.not_in_your_party", player))
            return
        }
        if (target.uniqueId == player.uniqueId) {
            player.sendMessage(msg("party.transfer.self", player))
            return
        }

        val oldLeader = party.leader
        PartyManager.transferLeadership(player, target)

        // Fire leader change event
        Bukkit.getPluginManager().callEvent(PartyLeaderChangeEvent(party, oldLeader, target))

        party.members.forEach { member ->
            if (member.uniqueId == target.uniqueId) {
                member.sendMessage(msg("party.transfer.you_are_leader", member))
            } else {
                member.sendMessage(msgComponent("party.transfer.transferred", member, target))
            }
        }
    }

    private fun handleList(player: Player) {
        val party = PartyManager.getParty(player) ?: run {
            player.sendMessage(msg("party.error.not_in_party", player))
            return
        }

        player.sendMessage(msg("party.list.header", player, party.size, party.maxSize))
        for (member in party.members) {
            val status = if (member.isOnline) msg("party.list.online", player) else msg("party.list.offline", player)
            val line = if (party.isLeader(member)) {
                msgComponent("party.list.leader", player, member)
            } else {
                msgComponent("party.list.member", player, member)
            }
            player.sendMessage(
                line.append(Component.space()).append(LegacyComponentSerializer.legacySection().deserialize(status))
            )
        }
    }

    private fun sendHelp(player: Player) {
        player.sendMessage("")
        player.sendMessage("§6§l╔═════════════════╗")
        player.sendMessage("§6§l║       §e§l" + msg("command.help.title", player) + "     §6§l║")
        player.sendMessage("§6§l╚═════════════════╝")
        player.sendMessage("")
        player.sendMessage(msg("party.help.create", player))
        player.sendMessage(msg("party.help.invite", player))
        player.sendMessage(msg("party.help.accept", player))
        player.sendMessage(msg("party.help.decline", player))
        player.sendMessage(msg("party.help.leave", player))
        player.sendMessage(msg("party.help.kick", player))
        player.sendMessage(msg("party.help.disband", player))
        player.sendMessage(msg("party.help.transfer", player))
        player.sendMessage(msg("party.help.list", player))
    }
}
