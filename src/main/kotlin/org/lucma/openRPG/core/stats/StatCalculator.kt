package org.lucma.openRPG.core.stats

import org.bukkit.entity.Player
import org.lucma.openRPG.managers.PlayerClassManager
import org.lucma.openRPG.managers.PlayerDataManager
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.data.PlayerStats
import org.lucma.openRPG.models.effects.*
import org.lucma.openRPG.models.talents.SkillTree
import org.lucma.openRPG.models.types.Effect
import org.lucma.openRPG.models.types.StackType

/**
 * Computes the player's current stats for GUI display.
 * Evaluates live conditions without applying world-changing effects or firing events.
 */
object StatCalculator {

    fun preview(player: Player): PlayerStats {
        val stats = PlayerStats()
        val clazz = PlayerClassManager.getPlayerClass(player) ?: return stats
        val data = PlayerDataManager.get(player)
        val talentMods = if (data != null) SkillTree.getModifiers(data.unlockedNodes) else emptyList()
        val modifiers = clazz.modifiers + talentMods
        val context = EffectContext(player, event = null, stats = stats)

        val grouped = modifiers.groupBy { it.effect.stackType }
        StackType.entries.forEach { stackType ->
            val sorted = grouped[stackType]?.sortedByDescending { it.effect.priority } ?: return@forEach
            sorted.forEach { modifier ->
                if (isStatPreviewSafe(modifier.effect) && modifier.condition.matches(context)) {
                    modifier.effect.apply(context)
                }
            }
        }
        return stats
    }

    private fun isStatPreviewSafe(effect: Effect): Boolean {
        return effect is DamageBonusEffect ||
            effect is DefenseBonusEffect ||
            effect is SpeedBonusEffect ||
            effect is CriticalChanceEffect ||
            effect is CriticalDamageEffect ||
            effect is KnockbackEffect ||
            effect is DodgeEffect ||
            effect is DamageReductionEffect ||
            effect is DamageReflectEffect ||
            effect is ShieldEffect ||
            effect is ExpBonusEffect ||
            effect is LootBonusEffect ||
            effect is MiningSpeedEffect ||
            effect is HealthRegenEffect ||
            effect is JumpBoostEffect ||
            effect is ManaRegenEffect ||
            effect is LifeStealMultiplierEffect
    }
}
