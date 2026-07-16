package org.lucma.openRPG.api

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.Material
import org.lucma.openRPG.models.PlayerClass
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.data.Modifier
import org.lucma.openRPG.models.data.PlayerData
import org.lucma.openRPG.models.data.PlayerStats
import org.lucma.openRPG.models.talents.SkillTree
import org.lucma.openRPG.models.talents.SkillTreeNode
import org.lucma.openRPG.models.types.Condition
import org.lucma.openRPG.models.types.Effect

/**
 * Public openRPG API interface.
 * Other plugins obtain the instance via ServicesManager:
 *
 *     val api = Bukkit.getServicesManager().load(OpenRPGAPI::class.java)
 *
 * Dependency in plugin.yml:
 *     depend: [openRPG]
 *     softdepend: [openRPG]
 */
interface OpenRPGAPI {

    // ═══════════════════════════════════════════
    //  Registries
    // ═══════════════════════════════════════════

    /** Register a playable class */
    fun registerClass(clazz: PlayerClass)

    /** Get all registered classes */
    fun getClasses(): Collection<PlayerClass>

    /** Find a class by ID */
    fun getClass(id: String): PlayerClass?

    /** Register a reusable condition by ID */
    fun registerCondition(id: String, factory: (Map<String, Any>) -> Condition)

    /** Create a condition from config */
    fun createCondition(id: String, config: Map<String, Any>): Condition?

    /** Register a reusable effect by ID */
    fun registerEffect(id: String, factory: (Map<String, Any>) -> Effect)

    /** Create an effect from config */
    fun createEffect(id: String, config: Map<String, Any>): Effect?

    /** Register a stat modifier by ID */
    fun registerStatModifier(id: String, applicator: (PlayerStats, Map<String, Any>) -> Unit)

    /** Apply a registered stat modifier */
    fun applyStatModifier(id: String, stats: PlayerStats, config: Map<String, Any>): Boolean

    // ═══════════════════════════════════════════
    //  Player
    // ═══════════════════════════════════════════

    /** Assign a class to a player (persists in PDC) */
    fun setPlayerClass(player: Player, clazz: PlayerClass)

    /** Get the player's current class */
    fun getPlayerClass(player: Player): PlayerClass?

    /** Get the player's progression data (level, EXP, talents) */
    fun getPlayerData(player: Player): PlayerData?

    /** Grant EXP to a player. Returns true if they leveled up */
    fun addExp(player: Player, amount: Int): Boolean

    /** Unlock a talent node for the player */
    fun allocateTalent(player: Player, nodeId: String): Boolean

    /** Get the player's unlocked talent IDs */
    fun getUnlockedTalents(player: Player): Set<String>

    // ═══════════════════════════════════════════
    //  Modifiers
    // ═══════════════════════════════════════════

    /**
     * Apply a list of modifiers to a context.
     * This is the same method used internally.
     * Useful for temporary modifiers (buffs, equipment).
     *
     * Example:
     *     api.applyModifiers(player, EffectContext(player, event, PlayerStats()), listOf(
     *         Modifier(AlwaysCondition(), DamageBonusEffect(0.10))
     *     ))
     */
    fun applyModifiers(player: Player, context: EffectContext, modifiers: List<Modifier>)

    /** Simplified version: creates an EffectContext with a new PlayerStats */
    fun applyModifiers(player: Player, event: Event, modifiers: List<Modifier>)

    // ═══════════════════════════════════════════
    //  Quick factories
    // ═══════════════════════════════════════════

    /** Create a Modifier with already instantiated condition and effect */
    fun modifier(condition: Condition, effect: Effect): Modifier

    /** Create an EffectContext for use with applyModifiers */
    fun context(player: Player, event: Event): EffectContext

    // ═══════════════════════════════════════════
    //  Skills (talents)
    // ═══════════════════════════════════════════

    /**
     * Register a skill/talent programmatically.
     * Useful for plugins that want to add nodes to the talent tree
     * without using skills.yml.
     *
     * @param node The SkillTreeNode to register
     */
    fun registerSkill(node: SkillTreeNode)

    /**
     * Register a skill/talent from its components.
     * Simplified version that builds the SkillTreeNode internally.
     *
     * @param id Unique skill identifier
     * @param name Display name
     * @param description Description text
     * @param className Class it belongs to ("warrior", "mage", "assassin", or a registered class)
     * @param condition The skill's condition
     * @param effect The skill's effect
     * @param material GUI material (default ENCHANTED_BOOK)
     * @param prerequisites List of required skill IDs
     */
    fun registerSkill(
        id: String,
        name: String,
        description: String,
        className: String,
        condition: Condition,
        effect: Effect,
        material: Material = Material.ENCHANTED_BOOK,
        prerequisites: List<String> = emptyList()
    )

    /** Get a skill by its ID */
    fun getSkill(id: String): SkillTreeNode?

    /** Get all registered skills */
    fun getSkills(): Collection<SkillTreeNode>

    /** Get the skills for a specific class */
    fun getSkillsForClass(className: String): List<SkillTreeNode>

    /**
     * Check if a player can unlock a skill.
     * Returns a result with can=true/false and the reason.
     */
    fun canUnlockSkill(player: Player, nodeId: String): SkillTree.CanUnlockResult

    /** Get the full SkillTree object for inspection */
    fun getSkillTree(): SkillTree
}
