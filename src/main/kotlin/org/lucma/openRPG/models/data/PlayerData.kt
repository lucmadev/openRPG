package org.lucma.openRPG.models.data

data class PlayerData(
    var level: Int = 1,
    var exp: Int = 0,
    var talentPoints: Int = 0,
    val unlockedNodes: MutableSet<String> = mutableSetOf()
) {
    val expToNextLevel: Int get() = level * 100
}
