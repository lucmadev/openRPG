package org.lucma.openRPG.core

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.lucma.openRPG.models.party.LeaveReason
import org.lucma.openRPG.models.party.PartyImpl
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object PartyManager {

    private val parties = ConcurrentHashMap<UUID, PartyImpl>()
    private val playerParties = ConcurrentHashMap<UUID, UUID>()
    private val invites = ConcurrentHashMap<UUID, MutableList<PartyInvite>>()

    data class PartyInvite(
        val party: PartyImpl,
        val inviter: Player,
        val invited: Player,
        val expiresAt: Long = System.currentTimeMillis() + 30_000L
    ) {
        fun isExpired(): Boolean = System.currentTimeMillis() >= expiresAt
    }

    // ── Create ──

    fun createParty(leader: Player): PartyImpl {
        if (playerParties.containsKey(leader.uniqueId)) {
            throw IllegalStateException("Player is already in a party")
        }
        val party = PartyImpl(leader = leader)
        parties[party.id] = party
        playerParties[leader.uniqueId] = party.id
        return party
    }

    // ── Get ──

    fun getParty(player: Player): PartyImpl? {
        val partyId = playerParties[player.uniqueId] ?: return null
        return parties[partyId]
    }

    fun getPartyById(partyId: UUID): PartyImpl? = parties[partyId]

    // ── Invite ──

    fun invitePlayer(inviter: Player, invited: Player): Boolean {
        val party = getParty(inviter) ?: return false
        if (!party.isLeader(inviter)) return false
        if (playerParties.containsKey(invited.uniqueId)) return false

        val inviteList = invites.computeIfAbsent(invited.uniqueId) { mutableListOf() }
        // Remove expired invites
        inviteList.removeAll { it.isExpired() }
        inviteList.add(PartyInvite(party, inviter, invited))
        return true
    }

    fun getInvites(player: Player): List<PartyInvite> {
        return invites[player.uniqueId]?.filter { !it.isExpired() } ?: emptyList()
    }

    fun getInviteFrom(invited: Player, inviter: Player): PartyInvite? {
        return invites[invited.uniqueId]
            ?.firstOrNull { it.inviter.uniqueId == inviter.uniqueId && !it.isExpired() }
    }

    fun hasInviteFrom(invited: Player, inviter: Player): Boolean =
        getInviteFrom(invited, inviter) != null

    // ── Accept ──

    fun acceptInvite(player: Player, partyId: UUID): Boolean {
        val inviteList = invites[player.uniqueId] ?: return false
        val invite = inviteList.firstOrNull { it.party.id == partyId && !it.isExpired() }
            ?: return false
        if (playerParties.containsKey(player.uniqueId)) return false

        val party = parties[partyId] ?: return false
        if (party.isFull) return false

        party.addMember(player)
        playerParties[player.uniqueId] = partyId
        inviteList.removeAll { it.party.id == partyId }
        return true
    }

    // ── Decline ──

    fun declineInvite(player: Player, partyId: UUID): Boolean {
        val inviteList = invites[player.uniqueId] ?: return false
        return inviteList.removeAll { it.party.id == partyId }
    }

    // ── Leave ──

    fun leaveParty(player: Player): Boolean {
        return removeFromParty(player, LeaveReason.VOLUNTARY)
    }

    // ── Kick ──

    fun kickPlayer(leader: Player, target: Player): Boolean {
        val party = getParty(leader) ?: return false
        if (!party.isLeader(leader)) return false
        if (!party.contains(target)) return false

        return removeFromParty(target, LeaveReason.KICKED)
    }

    // ── Disband ──

    fun disbandParty(leader: Player): Boolean {
        val party = getParty(leader) ?: return false
        if (!party.isLeader(leader)) return false

        for (member in party.members) {
            playerParties.remove(member.uniqueId)
        }
        parties.remove(party.id)
        return true
    }

    // ── Transfer Leadership ──

    fun transferLeadership(leader: Player, newLeader: Player): Boolean {
        val party = getParty(leader) ?: return false
        if (!party.isLeader(leader)) return false
        if (!party.contains(newLeader)) return false

        return party.setLeader(newLeader)
    }

    // ── Remove (internal) ──

    fun removeFromParty(player: Player, reason: LeaveReason): Boolean {
        val party = getParty(player) ?: return false
        val isLeader = party.isLeader(player)

        party.removeMember(player)
        playerParties.remove(player.uniqueId)

        if (party.size == 0) {
            // Party is empty, disband
            parties.remove(party.id)
        } else if (isLeader) {
            // Transfer leadership to the oldest member
            val newLeader = party.members.first()
            party.setLeader(newLeader)
        }

        return true
    }

    // ── Disconnect handling ──

    fun handleDisconnect(player: Player) {
        removeFromParty(player, LeaveReason.DISCONNECTED)
    }

    // ── Cleanup expired invites ──

    fun cleanupInvites() {
        val now = System.currentTimeMillis()
        invites.values.forEach { list ->
            list.removeAll { it.isExpired() }
        }
        invites.entries.removeAll { it.value.isEmpty() }
    }

    // ── Query ──

    fun isInParty(player: Player): Boolean = playerParties.containsKey(player.uniqueId)

    fun getPartySize(player: Player): Int = getParty(player)?.size ?: 0

    fun getAllParties(): Collection<PartyImpl> = parties.values
}
