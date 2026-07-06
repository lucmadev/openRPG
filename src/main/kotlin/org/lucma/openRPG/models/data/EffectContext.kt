package org.lucma.openRPG.models.data

import org.bukkit.entity.Player
import org.bukkit.event.Event

data class EffectContext(
    val player: Player,
    val event: Event,
    val stats: PlayerStats
)