package org.lucma.openRPG.models.talents

import org.bukkit.Material
import org.lucma.openRPG.models.conditions.CloseEnemiesCondition
import org.lucma.openRPG.models.conditions.LowHealthCondition
import org.lucma.openRPG.models.conditions.NightTimeCondition
import org.lucma.openRPG.models.conditions.SneakingCondition
import org.lucma.openRPG.models.data.Modifier
import org.lucma.openRPG.models.effects.CriticalChanceEffect
import org.lucma.openRPG.models.effects.CriticalDamageEffect
import org.lucma.openRPG.models.effects.DamageBonusEffect
import org.lucma.openRPG.models.effects.DefenseBonusEffect
import org.lucma.openRPG.models.effects.FireAuraEffect
import org.lucma.openRPG.models.effects.HealEffect
import org.lucma.openRPG.models.effects.LifeStealEffect
import org.lucma.openRPG.models.effects.SpeedBonusEffect

object SkillTree {

    private val allNodes = mutableMapOf<String, SkillTreeNode>()

    val classNodes: Map<String, List<String>> = mapOf(
        "warrior"  to listOf("war_damage_1", "war_damage_2", "war_defense_1", "war_crit_rate_1", "war_crit_dmg_1", "war_heal_1"),
        "mage"     to listOf("mag_damage_1", "mag_damage_2", "mag_crit_rate_1", "mag_crit_dmg_1", "mag_fire_1", "mag_speed_1"),
        "assassin" to listOf("ass_damage_1", "ass_damage_2", "ass_crit_rate_1", "ass_crit_dmg_1", "ass_speed_1", "ass_lifesteal_1")
    )

    init {
        // ── Guerrero ──
        register("war_damage_1",    "Furia",           "+15% daño cerca de enemigos",       Modifier(CloseEnemiesCondition(), DamageBonusEffect(0.15)),       Material.IRON_SWORD)
        register("war_damage_2",    "Torbellino",      "+25% daño cerca de enemigos",       Modifier(CloseEnemiesCondition(), DamageBonusEffect(0.25)),       Material.DIAMOND_SWORD,       prerequisites = listOf("war_damage_1"))
        register("war_defense_1",   "Fortaleza",       "+15% defensa cerca de enemigos",    Modifier(CloseEnemiesCondition(), DefenseBonusEffect(0.15)),      Material.SHIELD)
        register("war_crit_rate_1", "Instinto asesino","+7% prob. crítico",                 Modifier(CloseEnemiesCondition(), CriticalChanceEffect(0.07)),   Material.REDSTONE)
        register("war_crit_dmg_1",  "Golpe brutal",    "+0.50 multi. crítico",               Modifier(CloseEnemiesCondition(), CriticalDamageEffect(0.50)),   Material.NETHERITE_AXE)
        register("war_heal_1",      "Regeneración",    "Cura 2❤ al golpear si vida < 30%",  Modifier(LowHealthCondition(),    HealEffect(2.0)),             Material.APPLE)

        // ── Mago ──
        register("mag_damage_1",    "Poder estelar",     "+20% daño de noche",                Modifier(NightTimeCondition(), DamageBonusEffect(0.20)),          Material.ENDER_PEARL)
        register("mag_damage_2",    "Lluvia de estrellas","+35% daño de noche",               Modifier(NightTimeCondition(), DamageBonusEffect(0.35)),          Material.DRAGON_BREATH,       prerequisites = listOf("mag_damage_1"))
        register("mag_crit_rate_1", "Ojo de la noche",   "+8% prob. crítico de noche",        Modifier(NightTimeCondition(), CriticalChanceEffect(0.08)),      Material.SPYGLASS)
        register("mag_crit_dmg_1",  "Explosión arcana",  "+0.75 multi. crítico si baja vida", Modifier(LowHealthCondition(), CriticalDamageEffect(0.75)),      Material.FIREWORK_STAR)
        register("mag_fire_1",      "Llamas eternas",    "+3s ígneo (cooldown 4s)",           Modifier(CloseEnemiesCondition(), FireAuraEffect(3, 4)),         Material.BLAZE_ROD)
        register("mag_speed_1",     "Viento mágico",     "+15% velocidad al tener enemigos",  Modifier(CloseEnemiesCondition(), SpeedBonusEffect(0.15)),       Material.FEATHER)

        // ── Asesino ──
        register("ass_damage_1",    "Puñalada trapera",  "+25% daño agachado",                Modifier(SneakingCondition(), DamageBonusEffect(0.25)),          Material.STONE_SWORD)
        register("ass_damage_2",    "Golpe en la sombra","+45% daño agachado",                Modifier(SneakingCondition(), DamageBonusEffect(0.45)),          Material.IRON_SWORD,          prerequisites = listOf("ass_damage_1"))
        register("ass_crit_rate_1", "Ojo crítico",       "+12% prob. crítico agachado",       Modifier(SneakingCondition(), CriticalChanceEffect(0.12)),      Material.SPIDER_EYE)
        register("ass_crit_dmg_1",  "Degollar",          "+0.80 multi. crítico agachado",     Modifier(SneakingCondition(), CriticalDamageEffect(0.80)),      Material.NETHERITE_SWORD)
        register("ass_speed_1",     "Sombras veloces",   "+20% velocidad agachado",           Modifier(SneakingCondition(), SpeedBonusEffect(0.20)),          Material.SUGAR)
        register("ass_lifesteal_1", "Vampirismo",        "10% robo de vida si está herido",   Modifier(LowHealthCondition(), LifeStealEffect(0.10)),          Material.FERMENTED_SPIDER_EYE)
    }

    private fun register(id: String, name: String, description: String, modifier: Modifier, material: Material = Material.ENCHANTED_BOOK, prerequisites: List<String> = emptyList()) {
        allNodes[id] = SkillTreeNode(id, name, description, modifier, material, prerequisites)
    }

    fun getNode(id: String): SkillTreeNode? = allNodes[id]

    fun getNodesForClass(classId: String): List<SkillTreeNode> {
        return classNodes[classId]?.mapNotNull { allNodes[it] } ?: emptyList()
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
