package org.lucma.openRPG.managers

import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import org.lucma.openRPG.OpenRPG
import org.lucma.openRPG.models.data.PlayerData
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object PlayerDataManager {

    private val playersData = ConcurrentHashMap<UUID, PlayerData>()

    private var levelKey: NamespacedKey? = null
    private var expKey: NamespacedKey? = null
    private var talentPointsKey: NamespacedKey? = null
    private var talentNodesKey: NamespacedKey? = null

    private fun key(id: String): NamespacedKey {
        val k = NamespacedKey(OpenRPG.instance, id)
        return k
    }

    private fun initKeys() {
        if (levelKey == null) {
            levelKey = key("rpg_level")
            expKey = key("rpg_exp")
            talentPointsKey = key("rpg_tp")
            talentNodesKey = key("rpg_nodes")
        }
    }

    // ── Carga / Guardado ──

    fun load(player: Player): PlayerData {
        initKeys()
        try {
            val pdc = player.persistentDataContainer
            val level = pdc.get(levelKey!!, PersistentDataType.INTEGER) ?: 1
            val exp = pdc.get(expKey!!, PersistentDataType.INTEGER) ?: 0
            val tp = pdc.get(talentPointsKey!!, PersistentDataType.INTEGER) ?: 0
            val nodesRaw = pdc.get(talentNodesKey!!, PersistentDataType.STRING) ?: ""

            val nodes = if (nodesRaw.isBlank()) mutableSetOf()
            else nodesRaw.split(",").filter { it.isNotBlank() }.toMutableSet()

            val data = PlayerData(level, exp, tp, nodes)
            playersData[player.uniqueId] = data

            Bukkit.getLogger().info("[openRPG] " + player.getName() + " data: lv=" + level + " exp=" + exp + " tp=" + tp + " nodes=" + nodes.size)
            return data
        } catch (ex: Exception) {
            Bukkit.getLogger().severe("[openRPG] Error cargando PlayerData: " + ex.message)
            val data = PlayerData()
            playersData[player.uniqueId] = data
            return data
        }
    }

    fun save(player: Player) {
        initKeys()
        try {
            val data = playersData[player.uniqueId] ?: return
            val pdc = player.persistentDataContainer
            pdc.set(levelKey!!, PersistentDataType.INTEGER, data.level)
            pdc.set(expKey!!, PersistentDataType.INTEGER, data.exp)
            pdc.set(talentPointsKey!!, PersistentDataType.INTEGER, data.talentPoints)
            pdc.set(talentNodesKey!!, PersistentDataType.STRING, data.unlockedNodes.joinToString(","))
        } catch (ex: Exception) {
            Bukkit.getLogger().severe("[openRPG] Error guardando PlayerData: " + ex.message)
        }
    }

    fun unload(player: Player) {
        save(player)
        playersData.remove(player.uniqueId)
    }

    fun get(player: Player): PlayerData? = playersData[player.uniqueId]

    fun getOrCreate(player: Player): PlayerData {
        var data = playersData[player.uniqueId]
        if (data == null) {
            data = load(player)
        }
        return data
    }

    // ── EXP ──

    fun addExp(player: Player, amount: Int): Boolean {
        val data = getOrCreate(player)
        data.exp += amount

        var leveledUp = false
        while (data.exp >= data.expToNextLevel && data.level < 50) {
            data.exp -= data.expToNextLevel
            data.level++
            data.talentPoints++
            leveledUp = true
            Bukkit.getLogger().info("[openRPG] " + player.getName() + " subió a nivel " + data.level + " (TP: " + data.talentPoints + ")")
        }
        if (data.level >= 50) data.exp = data.exp.coerceAtMost(data.expToNextLevel)

        save(player)
        return leveledUp
    }

    // ── Talentos ──

    fun allocateNode(player: Player, nodeId: String): Boolean {
        val data = getOrCreate(player)
        if (data.talentPoints <= 0) return false
        if (nodeId in data.unlockedNodes) return false

        val result = org.lucma.openRPG.models.talents.SkillTree.canUnlock(nodeId, data.unlockedNodes)
        if (!result.can) return false

        data.unlockedNodes.add(nodeId)
        data.talentPoints--
        save(player)
        return true
    }
}
