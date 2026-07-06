package org.lucma.openRPG.managers

import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import org.lucma.openRPG.OpenRPG
import org.lucma.openRPG.core.registry.ClassRegistry
import org.lucma.openRPG.models.PlayerClass
import java.util.HashMap
import java.util.UUID

object PlayerClassManager {

    private val playerClasses = HashMap<UUID, PlayerClass>()

    private var classKey: NamespacedKey? = null

    private fun getClassKey(): NamespacedKey {
        val key = classKey
        if (key != null) return key
        val created = NamespacedKey(OpenRPG.instance, "player_class")
        classKey = created
        Bukkit.getLogger().info("[openRPG] PDC key creado: " + created.namespace + ":" + created.key)
        return created
    }

    // ── Memoria (runtime) ──

    fun setPlayerClass(player: Player, clazz: PlayerClass) {
        playerClasses[player.uniqueId] = clazz
        saveToPDC(player, clazz.id)
        Bukkit.getLogger().info("[openRPG] Clase asignada a " + player.getName() + ": " + clazz.id + " (guardada en PDC)")
    }

    fun getPlayerClass(player: Player): PlayerClass? {
        return playerClasses[player.uniqueId]
    }

    fun removeFromMemory(player: Player) {
        val prev = playerClasses.remove(player.uniqueId)
        if (prev != null) {
            Bukkit.getLogger().fine("[openRPG] Clase eliminada de memoria para " + player.getName())
        }
    }

    // ── Persistencia (PersistentDataContainer) ──

    fun loadFromPDC(player: Player) {
        try {
            val key = getClassKey()
            val classId = player.persistentDataContainer.get(key, PersistentDataType.STRING)

            if (classId == null) {
                Bukkit.getLogger().fine("[openRPG] " + player.getName() + " no tiene clase guardada en PDC")
                return
            }

            Bukkit.getLogger().info("[openRPG] PDC read para " + player.getName() + ": \"" + classId + "\"")

            val clazz = ClassRegistry.get(classId)
            if (clazz != null) {
                playerClasses[player.uniqueId] = clazz
                Bukkit.getLogger().info("[openRPG] " + player.getName() + " cargó su clase: " + clazz.id)
            } else {
                Bukkit.getLogger().warning("[openRPG] Clase '" + classId + "' no encontrada en registry, limpiando PDC")
                player.persistentDataContainer.remove(key)
            }
        } catch (ex: Exception) {
            Bukkit.getLogger().severe("[openRPG] Error cargando clase de PDC para " + player.getName() + ": " + ex.message)
            ex.printStackTrace()
        }
    }

    fun saveToPDC(player: Player, classId: String) {
        try {
            val key = getClassKey()
            player.persistentDataContainer.set(key, PersistentDataType.STRING, classId)

            // Verificar que se guardó
            val verify = player.persistentDataContainer.get(key, PersistentDataType.STRING)
            Bukkit.getLogger().info("[openRPG] PDC write para " + player.getName() + ": \"" + classId + "\" (verify: \"" + verify + "\")")
        } catch (ex: Exception) {
            Bukkit.getLogger().severe("[openRPG] Error guardando clase en PDC para " + player.getName() + ": " + ex.message)
            ex.printStackTrace()
        }
    }
}
