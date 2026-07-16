package org.lucma.openRPG.core

import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages cooldowns per player + key.
 * Useful for effects that should not repeat until some time has passed.
 *
 * Usage:
 *     if (CooldownManager.isOnCooldown(player, "fire_aura")) return
 *     CooldownManager.set(player, "fire_aura", 5) // 5 seconds
 */
object CooldownManager {

    private val data = ConcurrentHashMap<UUID, MutableMap<String, Long>>()

    /** Check if a cooldown is active */
    fun isOnCooldown(player: Player, key: String): Boolean {
        val now = System.currentTimeMillis()
        val playerMap = data[player.uniqueId] ?: return false
        val expires = playerMap[key] ?: return false
        if (now >= expires) {
            playerMap.remove(key)
            return false
        }
        return true
    }

    /** Get remaining cooldown seconds (0 if not active) */
    fun getRemaining(player: Player, key: String): Int {
        val now = System.currentTimeMillis()
        val playerMap = data[player.uniqueId] ?: return 0
        val expires = playerMap[key] ?: return 0
        val remaining = ((expires - now) / 1000).toInt()
        if (remaining <= 0) {
            playerMap.remove(key)
            return 0
        }
        return remaining
    }

    /** Set a cooldown of [seconds] seconds */
    fun set(player: Player, key: String, seconds: Int) {
        val expires = System.currentTimeMillis() + (seconds * 1000L)
        data.computeIfAbsent(player.uniqueId) { ConcurrentHashMap() }[key] = expires
    }

    /** Clear all cooldowns for a player */
    fun clear(player: Player) {
        data.remove(player.uniqueId)
    }

    /** Clear a specific cooldown */
    fun remove(player: Player, key: String) {
        data[player.uniqueId]?.remove(key)
    }
}
