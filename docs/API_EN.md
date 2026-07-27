# openRPG API

Other plugins can extend openRPG by registering custom classes, conditions, effects and stat modifiers, or by reading player data.

## Dependency

**plugin.yml** of the external plugin:

```yaml
depend: [openRPG]
```

## Getting the API

```kotlin
import org.lucma.openRPG.api.OpenRPGAPI
import org.bukkit.Bukkit

val api = Bukkit.getServicesManager().load(OpenRPGAPI::class.java)
```

## Registering classes

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

Players can select it via `/openrpg class` or directly:

```
/openrpg class paladin
```

> **Alias:** All `/openrpg` commands also work with `/rpg` and `/orpg`.
> Example: `/rpg class`, `/rpg talent`, `/rpg party invite <player>`.

## Registering conditions

Conditions must implement `Condition`:

```kotlin
interface Condition {
    fun matches(context: EffectContext): Boolean
}
```

Register with a factory that takes a `Map<String, Any>` so it can be created from `skills.yml`:

```kotlin
api.registerCondition("raining") { config ->
    RainingCondition()
}
```

From YAML:

```yaml
condition:
  type: raining
  config: {}
```

## Registering effects

Effects must implement `Effect`:

```kotlin
interface Effect {
    val priority: EffectPriority
    val stackType: StackType
    fun apply(context: EffectContext)
}
```

Register with a factory:

```kotlin
api.registerEffect("stun") { config ->
    val duration = (config["duration"] as? Number)?.toInt() ?: 2
    StunEffect(duration)
}
```

From YAML:

```yaml
effect:
  type: stun
  config: { duration: 3 }
```

## Registering stat modifiers

Stat modifiers change `PlayerStats` directly:

```kotlin
api.registerStatModifier("jump_boost") { stats, config ->
    val value = (config["value"] as? Number)?.toDouble() ?: 1.0
    stats.jumpMultiplier *= value
}
```

## Player data

```kotlin
// Current class
val clazz = api.getPlayerClass(player)
api.setPlayerClass(player, someClass)

// Level, EXP, talents
val data = api.getPlayerData(player)
if (data != null) {
    println("Level: ${data.level}")
    println("EXP: ${data.exp}/${data.expToNextLevel}")
    println("Talent points: ${data.talentPoints}")
    println("Nodes: ${data.unlockedNodes}")
}

// Grant EXP
api.addExp(player, 50)

// Talents
api.allocateTalent(player, "war_damage_1")
```

## Temporary modifiers (buffs, equipment)

The system applies class + talent modifiers automatically, but you can also apply extra modifiers from other plugins:

```kotlin
// With event (creates EffectContext automatically)
api.applyModifiers(player, event, listOf(
    api.modifier(AlwaysCondition(), DamageBonusEffect(0.10)),
    api.modifier(AlwaysCondition(), SpeedBonusEffect(0.05))
))

// With custom EffectContext
val ctx = api.context(player, event)
api.applyModifiers(player, ctx, listOf(...))
```

## Custom event

```kotlin
import org.lucma.openRPG.events.EffectAppliedEvent

@EventHandler
fun onEffect(event: EffectAppliedEvent) {
    val player = event.player
    val modifier = event.modifier
    val context = event.context

    // Particles
    player.world.spawnParticle(Particle.CRIT, player.location, 10)

    // Sounds
    player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)

    // Custom stats
    // StatsTracker.increment(player, "effects_applied")
}
```

## Registering skills (talents)

You can register skills programmatically instead of using `skills.yml`:

```kotlin
// Create a Condition and Effect first
val condition = api.createCondition("low_health", mapOf("threshold" to 0.30))
val effect = api.createEffect("defense_bonus", mapOf("bonus" to 0.20))

// Register with separate params (auto-builds SkillTreeNode)
api.registerSkill(
    id = "pal_iron_will",
    name = "Iron Will",
    description = "+20% defense when low HP",
    className = "paladin",
    condition = condition!!,
    effect = effect!!,
    material = Material.SHIELD,
    prerequisites = listOf("pal_defense_1")
)

// Or build the SkillTreeNode yourself
val node = SkillTreeNode(
    id = "pal_iron_will",
    name = "Iron Will",
    description = "+20% defense when low HP",
    modifier = Modifier(condition, effect),
    material = Material.SHIELD,
    prerequisites = listOf("pal_defense_1")
)
api.registerSkill(node)
```

## Reading skills

```kotlin
// All skills
val all: Collection<SkillTreeNode> = api.getSkills()

// By ID
val skill: SkillTreeNode? = api.getSkill("war_damage_1")

// By class
val warriorSkills: List<SkillTreeNode> = api.getSkillsForClass("warrior")

// Check unlock
val result = api.canUnlockSkill(player, "war_damage_3")
if (result.can) {
    // Player can unlock this skill
    api.allocateTalent(player, "war_damage_3")
} else {
    println(result.reason) // e.g. "Requires: Fury"
}
```

## Registering skills (talents)

You can register skills programmatically instead of using `skills.yml`:

```kotlin
// Create a Condition and Effect first
val condition = api.createCondition("low_health", mapOf("threshold" to 0.30))
val effect = api.createEffect("defense_bonus", mapOf("bonus" to 0.20))

// Register with separate params (auto-builds SkillTreeNode)
api.registerSkill(
    id = "pal_iron_will",
    name = "Iron Will",
    description = "+20% defense when low HP",
    className = "paladin",
    condition = condition!!,
    effect = effect!!,
    material = Material.SHIELD,
    prerequisites = listOf("pal_defense_1")
)

// Or build the SkillTreeNode yourself
val node = SkillTreeNode(
    id = "pal_iron_will",
    name = "Iron Will",
    description = "+20% defense when low HP",
    modifier = Modifier(condition, effect),
    material = Material.SHIELD,
    prerequisites = listOf("pal_defense_1"),
    classId = "paladin"  // Required to show in the talent GUI
)
api.registerSkill(node)

// Note: The `classId` field is essential for the skill to appear
// in the talent tree GUI for the specified class.
// Without it, the skill is stored but never displayed.

// You can also register skills from another plugin at any time:
// Just call registerSkill during your plugin's onEnable().
```

## Reading skills

```kotlin
// All skills
val all: Collection<SkillTreeNode> = api.getSkills()

// By ID
val skill: SkillTreeNode? = api.getSkill("war_damage_1")

// By class
val warriorSkills: List<SkillTreeNode> = api.getSkillsForClass("warrior")

// Check unlock
val result = api.canUnlockSkill(player, "war_damage_3")
if (result.can) {
    // Player can unlock this skill
    api.allocateTalent(player, "war_damage_3")
} else {
    println(result.reason) // e.g. "Requires: Fury"
}
```

## Party System

OpenRPG includes a full party (group) system that other plugins can consume.

### Party Interface

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

### Party API

```kotlin
// Create a party
val party = api.createParty(player)

// Get a player's party
val party = api.getParty(player)

// Invite a player (leader only)
api.inviteToParty(inviter, invited)

// Accept / decline a pending invitation
api.acceptInvite(player)
api.declineInvite(player)

// Leave the party / kick a member (leader only)
api.leaveParty(player)
api.kickFromParty(leader, target)

// Disband the party (leader only)
api.disbandParty(leader)

// Transfer leadership to another member
api.transferLeadership(leader, newLeader)
```

### Party Events

```kotlin
import org.lucma.openRPG.events.*

@EventHandler
fun onPartyJoin(event: PartyJoinEvent) {
    val party = event.party
    val player = event.player
    // particles, sounds, broadcast
}

@EventHandler
fun onPartyLeave(event: PartyLeaveEvent) {
    when (event.reason) {
        LeaveReason.VOLUNTARY -> // left voluntarily
        LeaveReason.KICKED -> // was kicked
        LeaveReason.DISCONNECTED -> // disconnected
        LeaveReason.DISBANDED -> // party disbanded
    }
}
```

| Event | Attributes | Description |
|---|---|---|
| `PartyPreInviteEvent` | `party, inviter, invited` | Cancellable — fired before invite is sent |
| `PartyInviteEvent` | `party, inviter, invited` | Fired when an invite is sent |
| `PartyJoinEvent` | `party, player` | Fired when a player joins |
| `PartyLeaveEvent` | `party, player, reason` | Fired when a player leaves |
| `PartyDisbandEvent` | `party` | Fired when the party is disbanded |
| `PartyLeaderChangeEvent` | `party, oldLeader, newLeader` | Fired on leadership transfer |

### EXP Sharing

When a player kills a mob, all party members within **50 blocks** receive the same amount of EXP.
Party members see `+X EXP (party share)` in their action bar.

### Integration example (Procedural Dungeons)

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
        listOf(player) // Solo fallback
    }
}
```

### Commands

| Command | Aliases | Description |
|---|---|---|
| `/party create` | `/p create`, `/rpg party create` | Create a party |
| `/party invite <player>` | `/p invite`, `/rpg party invite` | Invite a player (leader only) |
| `/party accept [player]` | `/p accept` | Accept an invitation |
| `/party decline [player]` | `/p decline` | Decline an invitation |
| `/party leave` | `/p leave` | Leave the party |
| `/party kick <player>` | `/p kick`, `/rpg party kick` | Kick a member (leader only) |
| `/party disband` | `/p disband` | Disband the party (leader only) |
| `/party transfer <player>` | `/p transfer` | Transfer leadership |
| `/party list` | `/p list` | List party members |
| `/party help` | `/p help` | Show party help |

## Factories

```kotlin
// Create a Modifier
val mod = api.modifier(condition, effect)

// Create an EffectContext
val ctx = api.context(player, event)
```

## Complete API reference

| Method | Description |
|---|---|
| `registerClass(clazz)` | Register a playable class |
| `getClasses()` | All registered classes |
| `getClass(id)` | Find class by ID |
| `registerCondition(id, factory)` | Register reusable condition |
| `createCondition(id, config)` | Create condition from config |
| `registerEffect(id, factory)` | Register reusable effect |
| `createEffect(id, config)` | Create effect from config |
| `registerStatModifier(id, applicator)` | Register stat operator |
| `applyStatModifier(id, stats, config)` | Apply stat operator |
| `setPlayerClass(player, clazz)` | Assign class (persists to PDC) |
| `getPlayerClass(player)` | Get player's current class |
| `getPlayerData(player)` | Level, EXP, talents |
| `addExp(player, amount)` | Grant EXP (handles level ups) |
| `allocateTalent(player, nodeId)` | Unlock talent node |
| `getUnlockedTalents(player)` | Unlocked node IDs |
| `applyModifiers(player, context, modifiers)` | Apply modifiers |
| `applyModifiers(player, event, modifiers)` | Simplified version |
| `modifier(condition, effect)` | Create a Modifier |
| `context(player, event)` | Create an EffectContext |
| `registerSkill(node)` | Register a skill from a SkillTreeNode |
| `registerSkill(id, name, desc, className, condition, effect, ...)` | Register a skill from components |
| `getSkill(id)` | Get a skill by ID |
| `getSkills()` | All registered skills |
| `getSkillsForClass(className)` | Skills for a specific class |
| `canUnlockSkill(player, nodeId)` | Check if a player can unlock a skill |
| `getSkillTree()` | Get the SkillTree object |
| `createParty(leader)` | Create a party |
| `getParty(player)` | Get a player's party |
| `inviteToParty(inviter, invited)` | Invite a player to the party |
| `acceptInvite(player)` | Accept pending invitation |
| `declineInvite(player)` | Decline pending invitation |
| `leaveParty(player)` | Leave the party |
| `kickFromParty(leader, target)` | Kick a member (leader only) |
| `disbandParty(leader)` | Disband the party (leader only) |
| `transferLeadership(leader, newLeader)` | Transfer party leadership |
