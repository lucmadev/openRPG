package org.lucma.openRPG.core

import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Gestiona cooldowns por jugador + clave.
 * Útil para efectos que no deben repetirse hasta pasado un tiempo.
 *
 * Uso:
 *     if (CooldownManager.isOnCooldown(player, "fire_aura")) return
 *     CooldownManager.set(player, "fire_aura", 5) // 5 segundos
 */
object CooldownManager {

    private val data = ConcurrentHashMap<UUID, MutableMap<String, Long>>()

    /** Verifica si un cooldown está activo */
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

    /** Obtiene segundos restantes de cooldown (0 si no está activo) */
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

    /** Activa un cooldown de [seconds] segundos */
    fun set(player: Player, key: String, seconds: Int) {
        val expires = System.currentTimeMillis() + (seconds * 1000L)
        data.computeIfAbsent(player.uniqueId) { ConcurrentHashMap() }[key] = expires
    }

    /** Limpia todos los cooldowns de un jugador */
    fun clear(player: Player) {
        data.remove(player.uniqueId)
    }

    /** Limpia un cooldown específico */
    fun remove(player: Player, key: String) {
        data[player.uniqueId]?.remove(key)
    }
}
