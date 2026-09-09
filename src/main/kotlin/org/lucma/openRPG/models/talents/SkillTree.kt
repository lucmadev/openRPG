package org.lucma.openRPG.models.talents

import org.bukkit.entity.Player
import org.lucma.openRPG.core.LanguageManager.msg
import org.lucma.openRPG.core.LanguageManager.skillName
import org.lucma.openRPG.models.data.Modifier

object SkillTree {

    private val nodes = mutableMapOf<String, SkillTreeNode>()
    private val classNodesMap = mutableMapOf<String, MutableList<String>>()

    val allNodes: Map<String, SkillTreeNode> get() = nodes
    val classNodes: Map<String, List<String>> get() = classNodesMap

    fun register(node: SkillTreeNode) {
        nodes[node.id] = node
        if (node.classId.isNotBlank()) {
            addToClass(node.classId, node.id)
        }
    }

    fun addToClass(classId: String, nodeId: String) {
        classNodesMap.computeIfAbsent(classId) { mutableListOf() }.add(nodeId)
    }

    fun getNode(id: String): SkillTreeNode? = nodes[id]

    fun getNodesForClass(classId: String): List<SkillTreeNode> {
        return classNodesMap[classId]?.mapNotNull { nodes[it] } ?: emptyList()
    }

    fun getModifiers(unlockedIds: Set<String>): List<Modifier> {
        return unlockedIds.mapNotNull { nodes[it]?.modifier }
    }

    fun canUnlock(nodeId: String, unlockedIds: Set<String>, player: Player? = null): CanUnlockResult {
        val node = nodes[nodeId] ?: return CanUnlockResult(false, msg("skill.unlock.not_found", player))
        if (nodeId in unlockedIds) return CanUnlockResult(false, msg("skill.unlock.already_have", player))
        val missing = node.prerequisites.filter { it !in unlockedIds }
        if (missing.isNotEmpty()) {
            val names = missing.mapNotNull { id ->
                nodes[id]?.let { skillName(it.id, player, it.name) }
            }.joinToString(", ")
            return CanUnlockResult(false, msg("skill.unlock.requires", player, names))
        }
        return CanUnlockResult(true, "")
    }

    data class CanUnlockResult(val can: Boolean, val reason: String)
}
