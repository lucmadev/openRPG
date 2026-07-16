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

### Basic (original)

| type | Parameters | Description |
|---|---|---|
| `close_enemies` | `radius: 5.0` | Nearby enemies |
| `low_health` | `threshold: 0.30` | Health < threshold |
| `night_time` | — | Night time |
| `sneaking` | — | Sneaking |
| `always` | — | Always active |

### Environmental

| type | Parameters | Description |
|---|---|---|
| `raining` | — | Raining |
| `thundering` | — | Thundering |
| `day_time` | — | Day time |
| `underground` | — | Underground (solid block above) |
| `in_water` | — | In water |
| `in_lava` | — | In lava |
| `on_fire` | — | On fire |
| `in_cave` | `maxSkyLight: 7` | In a cave |
| `high_altitude` | `minY: 0`, `maxY: -1` | Between Y levels |
| `biome` | `biome: plains`, `matchType: exact` | Specific biome |

### Player state

| type | Parameters | Description |
|---|---|---|
| `health_above` | `threshold: 0.70` | Health > threshold |
| `full_health` | — | Full health |
| `sprinting` | — | Sprinting |
| `swimming` | — | Swimming |
| `gliding` | — | Gliding (elytra) |
| `on_ground` | — | On ground |
| `airborne` | — | In the air |
| `hunger` | `threshold: 6`, `mode: below` | Hunger above/below |
| `experience_level` | `minLevel: 0`, `maxLevel: -1` | Vanilla XP level |
| `saturation` | `threshold: 5.0`, `mode: below` | Saturation |
| `fall_distance` | `minDistance: 3.0` | Fall distance |

### Special state

| type | Parameters | Description |
|---|---|---|
| `has_potion_effect` | `effectType: ""` (any) | Has potion effect |
| `holding_item` | `material: ""`, `hand: main` | Holding an item |
| `wearing_armor` | `material: ""` (any) | Wearing armor |
| `wearing_full_armor` | — | All 4 armor slots |
| `in_vehicle` | — | Riding a vehicle |
| `sleeping` | — | Sleeping in bed |

### Combat

| type | Parameters | Description |
|---|---|---|
| `in_combat` | `timeSinceHit: 5` | Recent combat |
| `no_enemies` | `radius: 5.0` | No nearby enemies |
| `outnumbered` | `radius: 5.0`, `ratio: 1.5` | Outnumbered |
| `target_low_health` | `threshold: 0.30` | Target low health |
| `target_full_health` | — | Target full health |
| `last_hit_killer` | — | Killing blow |
| `behind_target` | — | Behind the target |
| `blocking` | — | Blocking with shield |
| `recently_hurt` | `timeWindow: 3` | Hurt in last N seconds |

### Compound / Logic

| type | Parameters | Description |
|---|---|---|
| `and` | `conditions: [{type, config}]` | All conditions |
| `or` | `conditions: [{type, config}]` | Any condition |
| `not` | `condition: {type, config}` | Negates condition |
| `random` | `chance: 0.5` | Random probability |
| `cooldown` | `key: "x"`, `seconds: 5` | Cooldown between uses |

### Class / Data

| type | Parameters | Description |
|---|---|---|
| `has_class` | `classId: ""` | Has class (any or specific) |
| `has_talent` | `nodeId: "..."` | Unlocked talent |
| `player_level` | `minLevel: 1`, `maxLevel: -1` | openRPG level |
| `talent_points` | `minPoints: 1` | Available talent points |

### Miscellaneous

| type | Parameters | Description |
|---|---|---|
| `in_region` | `world`, `x1,z1,x2,z2` | Inside a region |
| `moon_phase` | `phase: 0` (0-7) | Moon phase |
| `light_level` | `min: 0`, `max: 15`, `type: any` | Light level |
| `in_world` | `worldName: "world"` | Specific world |

## Available effects

### Basic (original)

| type | Parameters | Description |
|---|---|---|
| `damage_bonus` | `bonus: 0.25` | +% damage dealt |
| `defense_bonus` | `bonus: 0.15` | +% defense |
| `speed_bonus` | `bonus: 0.10` | +% speed |
| `critical_chance` | `chance: 0.08` | +% crit chance |
| `critical_damage` | `bonus: 0.50` | + crit multiplier |
| `heal` | `amount: 2.0` | Flat heal |
| `life_steal` | `percentage: 0.10` | % life steal |
| `fire_aura` | `duration: 3`, `cooldown: 5` | Fire on hit |

### Target status effects

| type | Parameters | Description |
|---|---|---|
| `wither` | `duration: 3`, `amplifier: 1` | Applies Wither |
| `poison` | `duration: 3`, `amplifier: 1` | Applies Poison |
| `slowness` | `duration: 3`, `amplifier: 1` | Applies Slowness |
| `weakness` | `duration: 3`, `amplifier: 1` | Applies Weakness |
| `blindness` | `duration: 3` | Applies Blindness |
| `levitation` | `duration: 3`, `amplifier: 1` | Applies Levitation |
| `glow` | `duration: 5` | Makes target glow |
| `hunger_effect` | `duration: 5`, `amplifier: 1` | Applies Hunger |
| `mining_fatigue` | `duration: 5`, `amplifier: 1` | Mining Fatigue |
| `nausea` | `duration: 3` | Applies Nausea |
| `silence` | `duration: 3` | Silences (can't use items) |
| `disarm` | `dropChance: 1.0` | Disarms (drops item) |
| `stun` | `duration: 2` | Stuns (can't move) |
| `bleed` | `duration: 3`, `damagePerTick: 0.5` | Bleeding (DoT) |

### Special damage

| type | Parameters | Description |
|---|---|---|
| `knockback` | `multiplier: 0.5` | +% knockback |
| `execute` | `bonusMultiplier: 0.5`, `threshold: 0.30` | +dmg to low HP |
| `backstab` | `bonusMultiplier: 0.5` | +dmg from behind |
| `charge` | `bonusMultiplier: 0.3` | +dmg while sprinting |
| `area_damage` | `radius: 3.0`, `multiplier: 0.5` | AoE damage |
| `chain_damage` | `radius: 5.0`, `maxTargets: 3`, `multiplier: 0.7` | Chain damage |
| `true_damage` | `percentage: 0.20` | True damage |
| `lightning_strike` | `damage: 0.0` | Lightning + damage |
| `explosion` | `power: 2.0`, `fire: false` | Explosion |
| `shield_breaker` | `bonusMultiplier: 0.30` | Bypasses shields |
| `splash_damage` | `radius: 3.0`, `multiplier: 0.4` | Splash damage |

### Healing / Defense

| type | Parameters | Description |
|---|---|---|
| `absorption` | `hearts: 4.0` | Absorption hearts |
| `regeneration` | `duration: 5`, `amplifier: 1` | Gradual regen |
| `damage_reduction` | `flatReduction: 1.0` | Reduces incoming damage |
| `damage_reflect` | `percentage: 0.10` | Reflects % damage |
| `dodge` | `chance: 0.10` | Dodge chance |
| `shield` | `health: 5.0` | Absorbing shield |
| `fire_resistance` | `duration: 30` | Fire resistance |
| `water_breathing` | `duration: 30` | Underwater breathing |
| `invulnerability` | `duration: 2` | Brief invulnerability |

### Movement

| type | Parameters | Description |
|---|---|---|
| `jump_boost` | `multiplier: 0.5` | +% jump height |
| `slow_fall` | `duration: 5` | Slow falling |
| `leap` | `power: 1.5`, `upward: 0.5` | Leap forward |
| `dash` | `distance: 5.0` | Dash |
| `speed_aura` | `radius: 8.0`, `amplifier: 1` | Speed to allies |
| `web` | `radius: 2`, `duration: 3` | Cobweb trap |

### Buffs (potions)

| type | Parameters | Description |
|---|---|---|
| `strength` | `duration: 10`, `amplifier: 1` | Strength |
| `speed_potion` | `duration: 10`, `amplifier: 1` | Speed |
| `resistance` | `duration: 10`, `amplifier: 1` | Resistance |
| `invisibility` | `duration: 10` | Invisibility |
| `night_vision` | `duration: 30` | Night vision |
| `haste` | `duration: 10`, `amplifier: 1` | Haste |
| `dolphin_grace` | `duration: 15` | Dolphin's Grace |
| `luck_potion` | `duration: 30`, `amplifier: 1` | Luck |

### Resources

| type | Parameters | Description |
|---|---|---|
| `exp_bonus` | `multiplier: 0.5` | +% EXP |
| `loot_bonus` | `multiplier: 0.5` | +% loot |
| `mining_speed` | `multiplier: 0.5` | +% mining speed |
| `health_regen` | `multiplier: 0.5` | +% health regen |
| `mana_regen` | `regenPerSecond: 1.0` | Mana regeneration |
| `saturation` | `amount: 5.0` | Restores saturation |
| `feed` | `hungerRestored: 4` | Restores hunger |

### Visual / Sound

| type | Parameters | Description |
|---|---|---|
| `particle` | `particle: CRIT`, `count: 10`, `speed: 0.1` | Particles |
| `sound` | `sound: ENTITY_PLAYER_LEVELUP`, `volume: 1.0`, `pitch: 1.0` | Sound |
| `title` | `title`, `subtitle`, `fadeIn: 10`, `stay: 40`, `fadeOut: 10` | Screen title |

### Other

| type | Parameters | Description |
|---|---|---|
| `life_steal_multiplier` | `multiplier: 0.5` | Multiplies life steal |

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
