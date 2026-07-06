package org.lucma.openRPG.models.talents

import org.lucma.openRPG.models.data.Modifier

object SkillTree {

    private val allNodes = mutableMapOf<String, SkillTreeNode>()
    private val classNodesMap = mutableMapOf<String, MutableList<String>>()

    val classNodes: Map<String, List<String>> get() = classNodesMap

    fun register(node: SkillTreeNode) {
        allNodes[node.id] = node
    }

    fun addToClass(classId: String, nodeId: String) {
        classNodesMap.computeIfAbsent(classId) { mutableListOf() }.add(nodeId)
    }

    fun getNode(id: String): SkillTreeNode? = allNodes[id]

    fun getNodesForClass(classId: String): List<SkillTreeNode> {
        return classNodesMap[classId]?.mapNotNull { allNodes[it] } ?: emptyList()
    }

    fun getModifiers(unlockedIds: Set<String>): List<Modifier> {
        return unlockedIds.mapNotNull { allNodes[it]?.modifier }
    }

    fun canUnlock(nodeId: String, unlockedIds: Set<String>): CanUnlockResult {
        val node = allNodes[nodeId] ?: return CanUnlockResult(false, "Nodo no encontrado")
        if (nodeId in unlockedIds) return CanUnlockResult(false, "Ya tienes este nodo")
        val missing = node.prerequisites.filter { it !in unlockedIds }
        if (missing.isNotEmpty()) {
            val names = missing.mapNotNull { allNodes[it]?.name }.joinToString(", ")
            return CanUnlockResult(false, "Requieres: $names")
        }
        return CanUnlockResult(true, "")
    }

    data class CanUnlockResult(val can: Boolean, val reason: String)
}
