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
import org.lucma.openRPG.models.PlayerClass
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.data.Modifier
import org.lucma.openRPG.models.data.PlayerData
import org.lucma.openRPG.models.data.PlayerStats
import org.lucma.openRPG.models.types.Condition
import org.lucma.openRPG.models.types.Effect

class OpenRPGAPIImpl : OpenRPGAPI {

    // ════════════════════════ Registros ════════════════════════

    override fun registerClass(clazz: PlayerClass) {
        ClassRegistry.register(clazz)
        Bukkit.getLogger().info("[openRPG-API] Clase registrada: " + clazz.id)
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

    // ════════════════════════ Jugador ════════════════════════

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

        // Aplicar stats al evento si es de daño
        if (event is org.bukkit.event.entity.EntityDamageByEntityEvent) {
            event.damage *= context.stats.damageMultiplier
        }
    }

    // ════════════════════════ Fábricas ════════════════════════

    override fun modifier(condition: Condition, effect: Effect): Modifier =
        Modifier(condition, effect)

    override fun context(player: Player, event: Event): EffectContext =
        EffectContext(player, event, PlayerStats())
}
