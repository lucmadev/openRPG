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

    // ═══════════════════════════════════════════
    //  Skills (talentos)
    // ═══════════════════════════════════════════

    /**
     * Registra un skill/talento programáticamente.
     * Útil para plugins que quieran añadir nodos al árbol de talentos
     * sin usar skills.yml.
     *
     * @param node El SkillTreeNode a registrar
     */
    fun registerSkill(node: SkillTreeNode)

    /**
     * Registra un skill/talento desde sus componentes.
     * Versión simplificada que construye el SkillTreeNode internamente.
     *
     * @param id Identificador único del skill
     * @param name Nombre visible
     * @param description Descripción
     * @param className Clase a la que pertenece ("warrior", "mage", "assassin", o una clase registrada)
     * @param condition La condición del skill
     * @param effect El efecto del skill
     * @param material Material para el GUI (por defecto ENCHANTED_BOOK)
     * @param prerequisites Lista de IDs de skills requeridos
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

    /** Obtiene un skill por su ID */
    fun getSkill(id: String): SkillTreeNode?

    /** Obtiene todos los skills registrados */
    fun getSkills(): Collection<SkillTreeNode>

    /** Obtiene los skills de una clase concreta */
    fun getSkillsForClass(className: String): List<SkillTreeNode>

    /**
     * Comprueba si un jugador puede desbloquear un skill.
     * Devuelve un resultado con can=true/false y el motivo.
     */
    fun canUnlockSkill(player: Player, nodeId: String): SkillTree.CanUnlockResult

    /** Obtiene el objeto SkillTree completo para inspección */
    fun getSkillTree(): SkillTree
}
