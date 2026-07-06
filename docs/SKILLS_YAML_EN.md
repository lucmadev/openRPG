# Skills via YAML

All talent tree nodes are loaded from `plugins/openRPG/skills.yml`.  
No need to recompile the plugin to add, modify or remove skills.

## File location

| Location | Purpose |
|---|---|
| `resources/skills.yml` | Default inside the JAR |
| `plugins/openRPG/skills.yml` | Editable copy on the server |

On plugin startup, if the copy in `plugins/` doesn't exist, it is created automatically from the JAR.

## Structure

```yaml
skills:
  <unique_id>:
    name: "<display name>"
    description: "<description>"
    class: warrior|mage|assassin
    material: BUKKIT_MATERIAL
    prerequisites:
      - <required_skill_id>
    condition:
      type: <condition_registry_id>
      config:
        <param>: <value>
    effect:
      type: <effect_registry_id>
      config:
        <param>: <value>
```

## Fields

| Field | Required | Description |
|---|---|---|
| `id` (the YAML key) | Yes | Unique identifier. E.g. `war_damage_3` |
| `name` | Yes | Visible name in the GUI |
| `description` | No | Text in the item lore |
| `class` | Yes | `warrior`, `mage` or `assassin` |
| `material` | No | Bukkit Material. Default: `ENCHANTED_BOOK` |
| `prerequisites` | No | List of IDs that must be unlocked first |
| `condition` | Yes | Condition that must be met for the skill to activate |
| `effect` | Yes | Effect applied when the condition is met |

## Available conditions

### close_enemies

Activates when there are living entities (non-player) near the player.

```yaml
condition:
  type: close_enemies
  config:
    radius: 5.0   # radius in blocks (default: 5.0)
```

### low_health

Activates when the player's health is below a percentage threshold.

```yaml
condition:
  type: low_health
  config:
    threshold: 0.30   # 0.30 = 30% health (default: 0.30)
```

### night_time

Activates when it is night in the player's world (13000–23000 ticks).

```yaml
condition:
  type: night_time
  config: {}   # no parameters
```

### sneaking

Activates when the player is sneaking.

```yaml
condition:
  type: sneaking
  config: {}   # no parameters
```

## Available effects

### damage_bonus

Multiplies damage dealt.

```yaml
effect:
  type: damage_bonus
  config:
    bonus: 0.25   # 0.25 = +25% damage
```

### defense_bonus

Multiplies defense.

```yaml
effect:
  type: defense_bonus
  config:
    bonus: 0.15   # 0.15 = +15% defense
```

### speed_bonus

Multiplies speed.

```yaml
effect:
  type: speed_bonus
  config:
    bonus: 0.10   # 0.10 = +10% speed
```

### critical_chance

Adds critical hit chance (additive, not multiplicative).

```yaml
effect:
  type: critical_chance
  config:
    chance: 0.08   # 0.08 = +8% crit chance
```

### critical_damage

Adds critical hit multiplier (adds to base 1.0).

```yaml
effect:
  type: critical_damage
  config:
    bonus: 0.50   # 0.50 = crit deals x1.50 (base 1.0 + 0.50)
```

### heal

Heals a flat amount of health.

```yaml
effect:
  type: heal
  config:
    amount: 2.0   # 2.0 = 1 heart
```

### life_steal

Converts a percentage of damage dealt into healing.

```yaml
effect:
  type: life_steal
  config:
    percentage: 0.10   # 0.10 = steal 10% of damage as health
```

### fire_aura

Sets the attacked enemy on fire. Supports cooldown.

```yaml
effect:
  type: fire_aura
  config:
    duration: 3       # burn seconds (default: 3)
    cooldown: 5       # cooldown seconds between uses (default: 0 = no cd)
```

## Examples

### Basic skill, no prerequisites

```yaml
war_damage_1:
  name: "Fury"
  description: "+15% damage near enemies"
  class: warrior
  material: IRON_SWORD
  prerequisites: []
  condition:
    type: close_enemies
    config: { radius: 5.0 }
  effect:
    type: damage_bonus
    config: { bonus: 0.15 }
```

### Skill with prerequisite (chain)

```yaml
war_damage_2:
  name: "Whirlwind"
  description: "+25% damage near enemies"
  class: warrior
  material: DIAMOND_SWORD
  prerequisites: [war_damage_1]
  condition:
    type: close_enemies
    config: { radius: 5.0 }
  effect:
    type: damage_bonus
    config: { bonus: 0.25 }
```

### Skill with cooldown

```yaml
mag_fire_1:
  name: "Eternal Flames"
  description: "+3s fire (cooldown 4s)"
  class: mage
  material: BLAZE_ROD
  prerequisites: []
  condition:
    type: close_enemies
    config: { radius: 5.0 }
  effect:
    type: fire_aura
    config: { duration: 3, cooldown: 4 }
```

## Best practices

### IDs

Use a 3-letter class prefix + descriptive name:

| Class | Prefix | Example |
|---|---|---|
| Warrior | `war_` | `war_damage_3` |
| Mage | `mag_` | `mag_fire_2` |
| Assassin | `ass_` | `ass_crit_dmg_2` |

### Materials

Use Bukkit materials that visually represent the skill:

| Skill | Material |
|---|---|
| Melee damage | `IRON_SWORD`, `DIAMOND_SWORD` |
| Defense | `SHIELD` |
| Speed | `FEATHER`, `SUGAR` |
| Critical | `REDSTONE`, `SPIDER_EYE` |
| Healing | `APPLE`, `GOLDEN_APPLE` |
| Fire | `BLAZE_ROD`, `FIRE_CHARGE` |
| Life steal | `FERMENTED_SPIDER_EYE` |
| Magic | `ENDER_PEARL`, `DRAGON_BREATH` |

### Prerequisites

- IDs in `prerequisites` must match the skill key exactly
- Chains of any depth are supported: `A → B → C → D`
- A skill can have multiple prerequisites: `prerequisites: [war_damage_1, war_defense_1]`

## Scaling

To scale to **1328 skills** (or any number):

1. Add entries to `skills.yml`
2. Run `/reload` on the server
3. Players see the new nodes in the talent GUI

No need to:
- ❌ Recompile the plugin
- ❌ Modify any Kotlin file
- ❌ Restart the server (just `/reload`)
- ❌ Register anything in code

## Debugging

On startup, the console shows:

```
[openRPG] Skills: 18 loaded, 0 errors
```

If there are syntax errors or references to non-existent conditions/effects:

```
[openRPG] Error skill 'mag_fire_X': condition 'explosion' not found
[openRPG] Skills: 17 loaded, 1 errors
```

Skills with errors are simply skipped; the rest of the tree works normally.
