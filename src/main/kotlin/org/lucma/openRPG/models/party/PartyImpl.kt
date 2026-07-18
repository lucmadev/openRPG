package org.lucma.openRPG.models.party

import org.bukkit.entity.Player
import java.util.UUID

class PartyImpl(
    override val id: UUID = UUID.randomUUID(),
    override val leader: Player,
    override val maxSize: Int = 5
) : Party {

    private val _members = mutableListOf(leader)

    override val members: List<Player> get() = _members.toList()
    override val size: Int get() = _members.size
    override val isFull: Boolean get() = size >= maxSize

    fun addMember(player: Player): Boolean {
        if (isFull || player in _members) return false
        _members.add(player)
        return true
    }

    fun removeMember(player: Player): Boolean = _members.remove(player)

    fun setLeader(newLeader: Player): Boolean {
        if (newLeader !in _members) return false
        _members.remove(leader)
        _members.add(0, newLeader)
        return true
    }

    fun contains(player: Player): Boolean = player in _members

    fun isLeader(player: Player): Boolean = player.uniqueId == leader.uniqueId
}
