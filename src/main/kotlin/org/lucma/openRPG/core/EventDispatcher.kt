package org.lucma.openRPG.core

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.lucma.openRPG.core.effect.EffectEngine
import org.lucma.openRPG.managers.PlayerClassManager
import org.lucma.openRPG.managers.PlayerDataManager
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.talents.SkillTree

object EventDispatcher {

    private val serializer = PlainTextComponentSerializer.plainText()

    fun dispatch(context: EffectContext) {
        try {
            val playerName = serializer.serialize(context.player.name())
            val playerClass =
                PlayerClassManager.getPlayerClass(context.player)
                    ?: run {
                        Bukkit.getLogger().fine("[openRPG] " + playerName + " has no class assigned")
                        return
                    }

            val classModifiers = playerClass.modifiers

            // ── Merge class modifiers + talent modifiers ──
            val playerData = PlayerDataManager.get(context.player)
            val talentModifiers = if (playerData != null) {
                SkillTree.getModifiers(playerData.unlockedNodes)
            } else emptyList()

            val allModifiers = classModifiers + talentModifiers

            Bukkit.getLogger()
                .fine("[openRPG] Firing " + allModifiers.size + " effects for " + playerName + " (class=" + playerClass.id + " | talents=" + talentModifiers.size + ")")

            EffectEngine.apply(context, allModifiers)

        } catch (ex: Exception) {
            Bukkit.getLogger()
                .severe("[openRPG] Error in EventDispatcher: " + ex.javaClass.simpleName + ": " + ex.message)
            ex.printStackTrace()
        }
    }

}
