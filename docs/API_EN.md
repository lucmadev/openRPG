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
