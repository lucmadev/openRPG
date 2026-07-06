package org.lucma.openRPG.models.talents

import org.bukkit.Material
import org.lucma.openRPG.models.data.Modifier

data class SkillTreeNode(
    val id: String,
    val name: String,
    val description: String,
    val modifier: Modifier,
    val material: Material = Material.ENCHANTED_BOOK,
    val prerequisites: List<String> = emptyList(),
    val maxLevel: Int = 1,
    val x: Int = 0,
    val y: Int = 0
)
