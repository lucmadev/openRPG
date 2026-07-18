package org.lucma.openRPG.core

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import org.lucma.openRPG.core.registry.ConditionRegistry
import org.lucma.openRPG.core.registry.EffectRegistry
import org.lucma.openRPG.models.data.Modifier
import org.lucma.openRPG.models.talents.SkillTree
import org.lucma.openRPG.models.talents.SkillTreeNode

object SkillLoader {

    private const val FILE_NAME = "skills.yml"

    fun load(plugin: JavaPlugin) {
        val file = java.io.File(plugin.dataFolder, FILE_NAME)
        if (!file.exists()) {
            plugin.saveResource(FILE_NAME, false)
            Bukkit.getLogger().info("[openRPG] Created $FILE_NAME")
        }
        val config = YamlConfiguration.loadConfiguration(file)
        val sec = config.getConfigurationSection("skills") ?: run {
            Bukkit.getLogger().warning("[openRPG] No 'skills' section in $FILE_NAME"); return
        }
        var ok = 0;
        var err = 0
        for (key in sec.getKeys(false)) {
            val n = sec.getConfigurationSection(key) ?: run { err++; continue }
            try {
                val node = parseNode(key, n)
                if (node != null) {
                    SkillTree.register(node); ok++
                } else err++
            } catch (e: Exception) {
                err++; Bukkit.getLogger().warning("[openRPG] Error '$key': ${e.message}")
            }
        }
        Bukkit.getLogger().info("[openRPG] Skills: $ok loaded, $err errors")
    }

    private fun parseNode(id: String, s: ConfigurationSection): SkillTreeNode? {
        val name = s.getString("name") ?: return null
        val desc = s.getString("description") ?: ""
        val classId = s.getString("class") ?: return null
        val mat = try {
            Material.valueOf(s.getString("material", "ENCHANTED_BOOK")!!.uppercase())
        } catch (_: Exception) {
            Material.ENCHANTED_BOOK
        }
        val prereqs = s.getStringList("prerequisites")
        val cond = buildCond(s.getConfigurationSection("condition")) ?: return null
        val eff = buildEff(s.getConfigurationSection("effect")) ?: return null
        SkillTree.addToClass(classId, id)
        return SkillTreeNode(id, name, desc, Modifier(cond, eff), mat, prereqs, classId = classId)
    }

    private fun buildCond(s: ConfigurationSection?): org.lucma.openRPG.models.types.Condition? {
        val type = s?.getString("type") ?: return null
        val cfg = s.getConfigurationSection("config")
        val map = cfg?.getValues(false)?.mapValues { it.value as Any } ?: emptyMap()
        return ConditionRegistry.create(type, map)
    }

    private fun buildEff(s: ConfigurationSection?): org.lucma.openRPG.models.types.Effect? {
        val type = s?.getString("type") ?: return null
        val cfg = s.getConfigurationSection("config")
        val map = cfg?.getValues(false)?.mapValues { it.value as Any } ?: emptyMap()
        return EffectRegistry.create(type, map)
    }
}
