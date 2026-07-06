package org.lucma.openRPG.api

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.lucma.openRPG.models.PlayerClass
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.data.Modifier
import org.lucma.openRPG.models.data.PlayerData
import org.lucma.openRPG.models.data.PlayerStats
import org.lucma.openRPG.models.types.Condition
import org.lucma.openRPG.models.types.Effect

/**
 * Interfaz pública de openRPG.
 * Otros plugins obtienen la instancia via ServicesManager:
 *
 *     val api = Bukkit.getServicesManager().load(OpenRPGAPI::class.java)
 *
 * Dependencia en plugin.yml:
 *     depend: [openRPG]
 *     softdepend: [openRPG]
 */
interface OpenRPGAPI {

    // ═══════════════════════════════════════════
    //  Registros
    // ═══════════════════════════════════════════

    /** Registra una clase jugable */
    fun registerClass(clazz: PlayerClass)

    /** Obtiene todas las clases registradas */
    fun getClasses(): Collection<PlayerClass>

    /** Busca una clase por ID */
    fun getClass(id: String): PlayerClass?

    /** Registra una condición reutilizable por ID */
    fun registerCondition(id: String, factory: (Map<String, Any>) -> Condition)

    /** Crea una condición desde configuración */
    fun createCondition(id: String, config: Map<String, Any>): Condition?

    /** Registra un efecto reutilizable por ID */
    fun registerEffect(id: String, factory: (Map<String, Any>) -> Effect)

    /** Crea un efecto desde configuración */
    fun createEffect(id: String, config: Map<String, Any>): Effect?

    /** Registra un operador de stats por ID */
    fun registerStatModifier(id: String, applicator: (PlayerStats, Map<String, Any>) -> Unit)

    /** Aplica un modificador de stats registrado */
    fun applyStatModifier(id: String, stats: PlayerStats, config: Map<String, Any>): Boolean

    // ═══════════════════════════════════════════
    //  Jugador
    // ═══════════════════════════════════════════

    /** Asigna una clase a un jugador (persiste en PDC) */
    fun setPlayerClass(player: Player, clazz: PlayerClass)

    /** Obtiene la clase actual de un jugador */
    fun getPlayerClass(player: Player): PlayerClass?

    /** Obtiene los datos de progresión del jugador (nivel, EXP, talentos) */
    fun getPlayerData(player: Player): PlayerData?

    /** Añade EXP a un jugador. Devuelve true si subió de nivel */
    fun addExp(player: Player, amount: Int): Boolean

    /** Desbloquea un nodo de talento para el jugador */
    fun allocateTalent(player: Player, nodeId: String): Boolean

    /** Obtiene los IDs de talentos desbloqueados del jugador */
    fun getUnlockedTalents(player: Player): Set<String>

    // ═══════════════════════════════════════════
    //  Modifiers
    // ═══════════════════════════════════════════

    /**
     * Aplica una lista de modifiers a un contexto.
     * Es el mismo método que usa el sistema internamente.
     * Útil para añadir modifiers temporales (buffos, equipo).
     *
     * Ejemplo:
     *     api.applyModifiers(player, EffectContext(player, event, PlayerStats()), listOf(
     *         Modifier(AlwaysCondition(), DamageBonusEffect(0.10))
     *     ))
     */
    fun applyModifiers(player: Player, context: EffectContext, modifiers: List<Modifier>)

    /** Versión simplificada: crea un EffectContext con PlayerStats nuevo */
    fun applyModifiers(player: Player, event: Event, modifiers: List<Modifier>)

    // ═══════════════════════════════════════════
    //  Fábricas rápidas
    // ═══════════════════════════════════════════

    /** Crea un Modifier con condición y efecto ya instanciados */
    fun modifier(condition: Condition, effect: Effect): Modifier

    /** Crea un EffectContext para usar con applyModifiers */
    fun context(player: Player, event: Event): EffectContext
}
