package org.lucma.openRPG.models.party

import org.bukkit.entity.Player
import java.util.UUID

interface Party {
    val id: UUID
    val leader: Player
    val members: List<Player>
    val isFull: Boolean
    val size: Int
    val maxSize: Int
}
