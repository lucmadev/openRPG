package org.lucma.openRPG.core.registry

import org.lucma.openRPG.models.PlayerClass

object ClassRegistry {

    private val classes = mutableMapOf<String, PlayerClass>()

    fun register(playerClass: PlayerClass) {
        classes[playerClass.id] = playerClass
    }

    fun get(id: String): PlayerClass? {
        return classes[id]
    }

    fun all(): Collection<PlayerClass> {
        return classes.values
    }

}