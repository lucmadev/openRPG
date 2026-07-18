package org.lucma.openRPG.api

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.lucma.openRPG.core.effect.EffectEngine
import org.lucma.openRPG.core.registry.ClassRegistry
import org.lucma.openRPG.core.registry.ConditionRegistry
import org.lucma.openRPG.core.registry.EffectRegistry
import org.lucma.openRPG.core.registry.StatRegistry
import org.lucma.openRPG.managers.PlayerClassManager
import org.lucma.openRPG.managers.PlayerDataManager
import org.bukkit.Material
import org.lucma.openRPG.models.PlayerClass
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.data.Modifier
import org.lucma.openRPG.models.data.PlayerData
import org.lucma.openRPG.models.data.PlayerStats
import org.lucma.openRPG.models.talents.SkillTree
import org.lucma.openRPG.models.talents.SkillTreeNode
import org.lucma.openRPG.core.PartyManager
import org.lucma.openRPG.models.party.Party
import org.lucma.openRPG.models.types.Condition
import org.lucma.openRPG.models.types.Effect

class OpenRPGAPIImpl : OpenRPGAPI {

    // ════════════════════════ Registries ════════════════════════

    override fun registerClass(clazz: PlayerClass) {
        ClassRegistry.register(clazz)
        Bukkit.getLogger().info("[openRPG-API] Class registered: " + clazz.id)
    }

    override fun getClasses(): Collection<PlayerClass> = ClassRegistry.all()

    override fun getClass(id: String): PlayerClass? = ClassRegistry.get(id)

    override fun registerCondition(id: String, factory: (Map<String, Any>) -> Condition) {
        ConditionRegistry.register(id, factory)
    }

    override fun createCondition(id: String, config: Map<String, Any>): Condition? =
        ConditionRegistry.create(id, config)

    override fun registerEffect(id: String, factory: (Map<String, Any>) -> Effect) {
        EffectRegistry.register(id, factory)
    }

    override fun createEffect(id: String, config: Map<String, Any>): Effect? =
        EffectRegistry.create(id, config)

    override fun registerStatModifier(id: String, applicator: (PlayerStats, Map<String, Any>) -> Unit) {
        StatRegistry.register(id, applicator)
    }

    override fun applyStatModifier(id: String, stats: PlayerStats, config: Map<String, Any>): Boolean =
        StatRegistry.apply(id, stats, config)

    // ════════════════════════ Player ════════════════════════

    override fun setPlayerClass(player: Player, clazz: PlayerClass) {
        PlayerClassManager.setPlayerClass(player, clazz)
    }

    override fun getPlayerClass(player: Player): PlayerClass? =
        PlayerClassManager.getPlayerClass(player)

    override fun getPlayerData(player: Player): PlayerData? =
        PlayerDataManager.get(player)

    override fun addExp(player: Player, amount: Int): Boolean =
        PlayerDataManager.addExp(player, amount)

    override fun allocateTalent(player: Player, nodeId: String): Boolean =
        PlayerDataManager.allocateNode(player, nodeId)

    override fun getUnlockedTalents(player: Player): Set<String> {
        val data = PlayerDataManager.get(player) ?: return emptySet()
        return data.unlockedNodes.toSet()
    }

    // ════════════════════════ Modifiers ════════════════════════

    override fun applyModifiers(player: Player, context: EffectContext, modifiers: List<Modifier>) {
        EffectEngine.apply(context, modifiers)
    }

    override fun applyModifiers(player: Player, event: Event, modifiers: List<Modifier>) {
        val context = EffectContext(player, event, PlayerStats())
        EffectEngine.apply(context, modifiers)

        // Apply stats to the event if it is damage-related
        if (event is org.bukkit.event.entity.EntityDamageByEntityEvent) {
            event.damage *= context.stats.damageMultiplier
        }
    }

    // ════════════════════════ Factories ════════════════════════

    override fun modifier(condition: Condition, effect: Effect): Modifier =
        Modifier(condition, effect)

    override fun context(player: Player, event: Event): EffectContext =
        EffectContext(player, event, PlayerStats())

    // ════════════════════════ Skills ════════════════════════

    override fun registerSkill(node: SkillTreeNode) {
        SkillTree.register(node)
        Bukkit.getLogger().info("[openRPG-API] Skill registered: " + node.id + " (" + node.name + ")")
    }

    override fun registerSkill(
        id: String,
        name: String,
        description: String,
        className: String,
        condition: Condition,
        effect: Effect,
        material: Material,
        prerequisites: List<String>
    ) {
        val node = SkillTreeNode(id, name, description, Modifier(condition, effect), material, prerequisites)
        SkillTree.register(node)
        SkillTree.addToClass(className, id)
        Bukkit.getLogger().info("[openRPG-API] Skill registered: $id ($name) for class $className")
    }

    override fun getSkill(id: String): SkillTreeNode? = SkillTree.getNode(id)

    override fun getSkills(): Collection<SkillTreeNode> = SkillTree.allNodes.values

    override fun getSkillsForClass(className: String): List<SkillTreeNode> =
        SkillTree.getNodesForClass(className)

    override fun canUnlockSkill(player: Player, nodeId: String): SkillTree.CanUnlockResult {
        val data = PlayerDataManager.get(player) ?: return SkillTree.CanUnlockResult(false, "Player not found")
        return SkillTree.canUnlock(nodeId, data.unlockedNodes)
    }

    override fun getSkillTree(): SkillTree = SkillTree

    // ════════════════════════ Party ════════════════════════

    override fun createParty(leader: Player): Party {
        return PartyManager.createParty(leader)
    }

    override fun getParty(player: Player): Party? {
        return PartyManager.getParty(player)
    }

    override fun inviteToParty(inviter: Player, invited: Player): Boolean {
        return PartyManager.invitePlayer(inviter, invited)
    }

    override fun acceptInvite(player: Player): Boolean {
        val invites = PartyManager.getInvites(player)
        if (invites.isEmpty()) return false
        return PartyManager.acceptInvite(player, invites.first().party.id)
    }

    override fun declineInvite(player: Player): Boolean {
        val invites = PartyManager.getInvites(player)
        if (invites.isEmpty()) return false
        return PartyManager.declineInvite(player, invites.first().party.id)
    }

    override fun leaveParty(player: Player): Boolean {
        return PartyManager.leaveParty(player)
    }

    override fun kickFromParty(leader: Player, target: Player): Boolean {
        return PartyManager.kickPlayer(leader, target)
    }

    override fun disbandParty(leader: Player): Boolean {
        return PartyManager.disbandParty(leader)
    }

    override fun transferLeadership(leader: Player, newLeader: Player): Boolean {
        return PartyManager.transferLeadership(leader, newLeader)
    }
}
