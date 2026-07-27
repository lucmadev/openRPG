# API de openRPG

Otros plugins pueden extender openRPG registrando clases, condiciones, efectos y operadores de stats propios, o leyendo datos de los jugadores.

## Dependencia

**plugin.yml** del plugin externo:

```yaml
depend: [openRPG]
```

## Obtener la API

```kotlin
import org.lucma.openRPG.api.OpenRPGAPI
import org.bukkit.Bukkit

val api = Bukkit.getServicesManager().load(OpenRPGAPI::class.java)
```

## Registro de clases

```kotlin
class Paladin : PlayerClass() {
    override val id = "paladin"
    override val name = "Paladin"
    override val modifiers = listOf(
        Modifier(LowHealthCondition(), DefenseBonusEffect(0.20)),
    )
}

api.registerClass(Paladin())
```

Los jugadores la ven en `/openrpg class` o pueden asignarla directamente:

```
/openrpg class paladin
```

> **Alias:** Todos los comandos `/openrpg` también funcionan con `/rpg` y `/orpg`.
> Ejemplo: `/rpg class`, `/rpg talent`, `/rpg party invite <jugador>`.

## Registro de condiciones

Las condiciones deben implementar `Condition`:

```kotlin
interface Condition {
    fun matches(context: EffectContext): Boolean
}
```

Se registran con una fábrica que recibe `Map<String, Any>` para poder crearse desde `skills.yml`:

```kotlin
api.registerCondition("raining") { config ->
    RainingCondition()
}
```

Desde YAML:

```yaml
condition:
  type: raining
  config: {}
```

## Registro de efectos

Los efectos deben implementar `Effect`:

```kotlin
interface Effect {
    val priority: EffectPriority
    val stackType: StackType
    fun apply(context: EffectContext)
}
```

Registro con fábrica:

```kotlin
api.registerEffect("stun") { config ->
    val duration = (config["duration"] as? Number)?.toInt() ?: 2
    StunEffect(duration)
}
```

Desde YAML:

```yaml
effect:
  type: stun
  config: { duration: 3 }
```

## Registro de operadores de stats

Los operadores modifican `PlayerStats` directamente:

```kotlin
api.registerStatModifier("jump_boost") { stats, config ->
    val value = (config["value"] as? Number)?.toDouble() ?: 1.0
    stats.jumpMultiplier *= value
}
```

## Datos del jugador

```kotlin
// Clase actual
val clazz = api.getPlayerClass(player)
api.setPlayerClass(player, algunaClase)

// Nivel, EXP, talentos
val data = api.getPlayerData(player)
if (data != null) {
    println("Nivel: ${data.level}")
    println("EXP: ${data.exp}/${data.expToNextLevel}")
    println("Puntos de talento: ${data.talentPoints}")
    println("Nodos: ${data.unlockedNodes}")
}

// Dar EXP
api.addExp(player, 50)

// Talentos
api.allocateTalent(player, "war_damage_1")
```

## Modifiers temporales (buffos, equipo)

El sistema aplica modifiers de clase + talentos automáticamente, pero puedes aplicar modifiers extra desde otros plugins:

```kotlin
// Con evento (crea EffectContext automáticamente)
api.applyModifiers(player, event, listOf(
    api.modifier(AlwaysCondition(), DamageBonusEffect(0.10)),
    api.modifier(AlwaysCondition(), SpeedBonusEffect(0.05))
))

// Con EffectContext propio
val ctx = api.context(player, event)
api.applyModifiers(player, ctx, listOf(...))
```

## Evento personalizado

```kotlin
import org.lucma.openRPG.events.EffectAppliedEvent

@EventHandler
fun onEffect(event: EffectAppliedEvent) {
    val player = event.player
    val modifier = event.modifier
    val context = event.context

    // Partículas
    player.world.spawnParticle(Particle.CRIT, player.location, 10)

    // Sonidos
    player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)

    // Estadísticas personalizadas
    // StatsTracker.increment(player, "effects_applied")
}
```

## Registro de skills (talentos)

Puedes registrar skills programáticamente en lugar de usar `skills.yml`:

```kotlin
// Crea una Condition y Effect primero
val condition = api.createCondition("low_health", mapOf("threshold" to 0.30))
val effect = api.createEffect("defense_bonus", mapOf("bonus" to 0.20))

// Registra con parámetros separados (construye SkillTreeNode automáticamente)
api.registerSkill(
    id = "pal_iron_will",
    name = "Voluntad de Hierro",
    description = "+20% defensa cuando tiene poca vida",
    className = "paladin",
    condition = condition!!,
    effect = effect!!,
    material = Material.SHIELD,
    prerequisites = listOf("pal_defense_1")
)

// O construye el SkillTreeNode tú mismo
val node = SkillTreeNode(
    id = "pal_iron_will",
    name = "Voluntad de Hierro",
    description = "+20% defensa cuando tiene poca vida",
    modifier = Modifier(condition, effect),
    material = Material.SHIELD,
    prerequisites = listOf("pal_defense_1")
)
api.registerSkill(node)
```

## Leer skills

```kotlin
// Todos los skills
val todos: Collection<SkillTreeNode> = api.getSkills()

// Por ID
val skill: SkillTreeNode? = api.getSkill("war_damage_1")

// Por clase
val skillsGuerrero: List<SkillTreeNode> = api.getSkillsForClass("warrior")

// Comprobar desbloqueo
val resultado = api.canUnlockSkill(player, "war_damage_3")
if (resultado.can) {
    api.allocateTalent(player, "war_damage_3")
} else {
    println(resultado.reason) // ej. "Requieres: Fury"
}
```

## Registro de skills (talentos)

Puedes registrar skills programáticamente en lugar de usar `skills.yml`:

```kotlin
// Crea una Condition y Effect primero
val condition = api.createCondition("low_health", mapOf("threshold" to 0.30))
val effect = api.createEffect("defense_bonus", mapOf("bonus" to 0.20))

// Registra con parámetros separados (construye SkillTreeNode automáticamente)
api.registerSkill(
    id = "pal_iron_will",
    name = "Voluntad de Hierro",
    description = "+20% defensa cuando tiene poca vida",
    className = "paladin",
    condition = condition!!,
    effect = effect!!,
    material = Material.SHIELD,
    prerequisites = listOf("pal_defense_1")
)

// O construye el SkillTreeNode tú mismo
val node = SkillTreeNode(
    id = "pal_iron_will",
    name = "Voluntad de Hierro",
    description = "+20% defensa cuando tiene poca vida",
    modifier = Modifier(condition, effect),
    material = Material.SHIELD,
    prerequisites = listOf("pal_defense_1"),
    classId = "paladin"  // Necesario para que aparezca en el GUI de talentos
)
api.registerSkill(node)

// Nota: El campo `classId` es esencial para que el skill aparezca
// en el árbol de talentos de la clase especificada.
// Sin él, el skill se guarda pero nunca se muestra.

// Puedes registrar skills desde otro plugin en cualquier momento:
// Solo llama a registerSkill durante tu onEnable().
```

## Leer skills

```kotlin
// Todos los skills
val todos: Collection<SkillTreeNode> = api.getSkills()

// Por ID
val skill: SkillTreeNode? = api.getSkill("war_damage_1")

// Por clase
val skillsGuerrero: List<SkillTreeNode> = api.getSkillsForClass("warrior")

// Comprobar desbloqueo
val resultado = api.canUnlockSkill(player, "war_damage_3")
if (resultado.can) {
    api.allocateTalent(player, "war_damage_3")
} else {
    println(resultado.reason) // ej. "Requieres: Fury"
}
```

## Party System

OpenRPG incluye un sistema de grupos (Party) que otros plugins pueden consumir.

### Interfaz Party

```kotlin
interface Party {
    val id: UUID
    val leader: Player
    val members: List<Player>
    val isFull: Boolean
    val size: Int
    val maxSize: Int
}
```

### API de Party

```kotlin
// Crear un grupo
val party = api.createParty(player)

// Obtener el grupo de un jugador
val party = api.getParty(player)

// Invitar un jugador (solo líder)
api.inviteToParty(inviter, invited)

// Aceptar / rechazar invitación
api.acceptInvite(player)
api.declineInvite(player)

// Salir del grupo / expulsar miembro (solo líder)
api.leaveParty(player)
api.kickFromParty(leader, target)

// Disolver el grupo (solo líder)
api.disbandParty(leader)

// Transferir liderazgo a otro miembro
api.transferLeadership(leader, newLeader)
```

### Eventos de Party

```kotlin
import org.lucma.openRPG.events.*

@EventHandler
fun onPartyJoin(event: PartyJoinEvent) {
    val party = event.party
    val player = event.player
    // partículas, sonidos, broadcast
}

@EventHandler
fun onPartyLeave(event: PartyLeaveEvent) {
    when (event.reason) {
        LeaveReason.VOLUNTARY -> // se fue voluntariamente
        LeaveReason.KICKED -> // fue expulsado
        LeaveReason.DISCONNECTED -> // se desconectó
        LeaveReason.DISBANDED -> // grupo disuelto
    }
}
```

| Evento | Atributos | Descripción |
|---|---|---|
| `PartyPreInviteEvent` | `party, inviter, invited` | Cancellable — antes de enviar invitación |
| `PartyInviteEvent` | `party, inviter, invited` | Se envió invitación |
| `PartyJoinEvent` | `party, player` | Jugador se unió |
| `PartyLeaveEvent` | `party, player, reason` | Jugador se fue |
| `PartyDisbandEvent` | `party` | Grupo disuelto |
| `PartyLeaderChangeEvent` | `party, oldLeader, newLeader` | Cambio de líder |

### EXP Compartido

Cuando un jugador mata un mob, todos los miembros del grupo dentro de **50 bloques** reciben la misma cantidad de EXP.
Los miembros del grupo ven `+X EXP (grupo)` en su action bar.

### Ejemplo de integración (Procedural Dungeons)

```kotlin
fun createDungeonFor(player: Player, template: DungeonTemplate): DungeonInstance {
    val members = resolveParty(player)
    return DungeonAPI.createDungeon(template, members)
}

private fun resolveParty(player: Player): List<Player> {
    val openRPG = Bukkit.getServicesManager().load(OpenRPGAPI::class.java)
    return if (openRPG != null) {
        openRPG.getParty(player)?.members ?: listOf(player)
    } else {
        listOf(player) // Solo
    }
}
```

### Comandos

| Comando | Aliases | Descripción |
|---|---|---|
| `/party create` | `/p create`, `/rpg party create` | Crear un grupo |
| `/party invite <jugador>` | `/p invite`, `/rpg party invite` | Invitar jugador (solo líder) |
| `/party accept [jugador]` | `/p accept` | Aceptar invitación |
| `/party decline [jugador]` | `/p decline` | Rechazar invitación |
| `/party leave` | `/p leave` | Abandonar grupo |
| `/party kick <jugador>` | `/p kick`, `/rpg party kick` | Expulsar miembro (solo líder) |
| `/party disband` | `/p disband` | Disolver grupo (solo líder) |
| `/party transfer <jugador>` | `/p transfer` | Transferir liderazgo |
| `/party list` | `/p list` | Listar miembros |
| `/party help` | `/p help` | Ayuda del grupo |

## Fábricas

```kotlin
// Crea un Modifier
val mod = api.modifier(condition, effect)

// Crea un EffectContext
val ctx = api.context(player, event)
```

## Referencia completa de la API

| Método | Descripción |
|---|---|
| `registerClass(clazz)` | Registra una clase jugable |
| `getClasses()` | Todas las clases registradas |
| `getClass(id)` | Busca clase por ID |
| `registerCondition(id, factory)` | Registra condición reutilizable |
| `createCondition(id, config)` | Crea condición desde config |
| `registerEffect(id, factory)` | Registra efecto reutilizable |
| `createEffect(id, config)` | Crea efecto desde config |
| `registerStatModifier(id, applicator)` | Registra operador de stats |
| `applyStatModifier(id, stats, config)` | Aplica operador de stats |
| `setPlayerClass(player, clazz)` | Asigna clase (persiste en PDC) |
| `getPlayerClass(player)` | Clase actual del jugador |
| `getPlayerData(player)` | Nivel, EXP, talentos |
| `addExp(player, amount)` | Añade EXP (controla level ups) |
| `allocateTalent(player, nodeId)` | Desbloquea nodo de talento |
| `getUnlockedTalents(player)` | IDs de talentos desbloqueados |
| `applyModifiers(player, context, modifiers)` | Aplica modifiers |
| `applyModifiers(player, event, modifiers)` | Versión simplificada |
| `modifier(condition, effect)` | Crea un Modifier |
| `context(player, event)` | Crea un EffectContext |
| `registerSkill(node)` | Registra un skill desde un SkillTreeNode |
| `registerSkill(id, name, desc, className, condition, effect, ...)` | Registra un skill desde componentes |
| `getSkill(id)` | Obtiene un skill por su ID |
| `getSkills()` | Todos los skills registrados |
| `getSkillsForClass(className)` | Skills de una clase específica |
| `canUnlockSkill(player, nodeId)` | Comprueba si un jugador puede desbloquear un skill |
| `getSkillTree()` | Obtiene el objeto SkillTree |
| `createParty(leader)` | Crea un grupo |
| `getParty(player)` | Obtiene el grupo del jugador |
| `inviteToParty(inviter, invited)` | Invita un jugador al grupo |
| `acceptInvite(player)` | Acepta invitación pendiente |
| `declineInvite(player)` | Rechaza invitación pendiente |
| `leaveParty(player)` | Abandona el grupo |
| `kickFromParty(leader, target)` | Expulsa un miembro (solo líder) |
| `disbandParty(leader)` | Disuelve el grupo (solo líder) |
| `transferLeadership(leader, newLeader)` | Transfiere liderazgo |
