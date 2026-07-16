package org.lucma.openRPG.models.talents

import org.lucma.openRPG.models.data.Modifier

object SkillTree {

    private val nodes = mutableMapOf<String, SkillTreeNode>()
    private val classNodesMap = mutableMapOf<String, MutableList<String>>()

    val allNodes: Map<String, SkillTreeNode> get() = nodes
    val classNodes: Map<String, List<String>> get() = classNodesMap

    fun register(node: SkillTreeNode) {
        nodes[node.id] = node
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

    fun canUnlock(nodeId: String, unlockedIds: Set<String>): CanUnlockResult {
        val node = nodes[nodeId] ?: return CanUnlockResult(false, "Node not found")
        if (nodeId in unlockedIds) return CanUnlockResult(false, "You already have this node")
        val missing = node.prerequisites.filter { it !in unlockedIds }
        if (missing.isNotEmpty()) {
            val names = missing.mapNotNull { nodes[it]?.name }.joinToString(", ")
            return CanUnlockResult(false, "Requires: $names")
        }
        return CanUnlockResult(true, "")
    }

    data class CanUnlockResult(val can: Boolean, val reason: String)
}
