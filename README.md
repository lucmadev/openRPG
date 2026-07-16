# openRPG

Modular RPG system for Paper 1.21+ servers. Adds classes, passive skills, talent trees, stats, and an EXP progression system — all with inventory-based GUIs.

## Features

- **3 playable classes**: Warrior, Mage and Assassin, each with unique modifiers
- **Condition system**: effects activate based on context (near enemies, at night, low health, sneaking)
- **Talent tree**: 6 talents per class, with prerequisites and points per level
- **EXP system**: gain experience by killing mobs, level up, and earn talent points
- **Stats**: damage, defense, speed, critical chance, and critical multiplier
- **Graphical interface**: all systems have inventory GUIs (class selection, status, talent tree)
- **Persistence**: data is saved in the player's PersistentDataContainer (no external files required)
- **Public API**: other plugins can extend the system with custom classes, conditions, and effects
- **Custom event**: `EffectAppliedEvent` so other plugins can react without coupling

## Requirements

- **Server**: Paper 1.21+ (API 26.2+)
- **Java**: 21+ (jvmToolchain 25)

## Installation

1. Download the JAR from [releases](https://github.com/Lucma/openRPG/releases)
2. Place it in your server's `plugins/` folder
3. Restart the server

## Commands

| Command | Alias | Description |
|---|---|---|
| `/assignclass [id]` | `/class`, `/clase` | Opens class selection GUI or assigns by ID |
| `/rpg` | `/pg`, `/stats` | Opens player status GUI |
| `/talent` | `/talento`, `/skill` | Opens talent tree GUI |

## Classes

### 🗡️ Warrior
Bonuses when enemies are nearby:
- +10% damage
- +5% defense
- +5% speed
- +3% crit chance
- +0.20 crit multiplier

### 🔮 Mage
Bonuses at night and when injured:
- +15% damage at night
- +5% crit chance at night
- +10% defense if health < 30%
- +0.25 crit multiplier if health < 30%
- Burns 2s on hit
- +5% speed near enemies

### 🗡️ Assassin
Bonuses when sneaking and when injured:
- +20% damage while sneaking
- +8% crit chance while sneaking
- +0.30 crit multiplier while sneaking
- +10% speed while sneaking
- 5% life steal if health < 30%

## Talent tree

Each level grants **1 talent point** (max 50 levels). Points are spent in your class's talent tree.

Example Warrior nodes:

```
Fury           → +15% damage (no prerequisite)
Whirlwind      → +25% damage (requires: Fury)
Fortitude      → +15% defense
Killer Instinct → +7% crit chance
Brutal Strike  → +0.50 crit multiplier
Regeneration   → Heal 2❤ if health < 30%
```

Use `/talent` to open the tree and click on nodes to learn them.

## API for developers

openRPG exposes a public API via Bukkit's `ServicesManager`.

### Dependency

**`plugin.yml`**:
```yaml
depend: [openRPG]
```

### Basic usage

```kotlin
import org.lucma.openRPG.api.OpenRPGAPI
import org.bukkit.Bukkit

val api = Bukkit.getServicesManager().load(OpenRPGAPI::class.java)

// Register a custom class
api.registerClass(MyCustomClass())

// Apply temporary modifiers (buffs, equipment)
api.applyModifiers(player, event, listOf(
    api.modifier(AlwaysCondition(), DamageBonusEffect(0.10))
))
```

### Listening to events

```kotlin
@EventHandler
fun onEffect(event: EffectAppliedEvent) {
    val player = event.player
    val modifier = event.modifier
    // particles, sounds, stats...
}
```

### Complete API

| Method | Description |
|---|---|
| `registerClass(clazz)` | Register a playable class |
| `getPlayerClass(player)` | Player's current class |
| `setPlayerClass(player, clazz)` | Assign class (persists in PDC) |
| `getPlayerData(player)` | Player level, EXP, talents |
| `addExp(player, amount)` | Grant EXP (handles level ups) |
| `applyModifiers(player, event, modifiers)` | Apply modifiers to player |
| `registerEffect(id, factory)` | Register an effect from config |
| `createEffect(id, config)` | Create an effect from config |

## Architecture

```
                         ┌──────────────┐
                         │  OpenRPGAPI  │ ← Public API (ServicesManager)
                         └──────┬───────┘
                                │
┌───────────┐    ┌──────────────┴──────────┐    ┌──────────────┐
│ Commands  │    │     Core (singletons)    │    │   Listeners  │
│ (GUIs)    │◄──►│                          │◄──►│  (Bukkit)    │
└───────────┘    │  EventDispatcher         │    └──────────────┘
                 │  EffectEngine            │
┌───────────┐    │  ConditionEngine         │    ┌──────────────┐
│  Models   │◄──►│  StatEngine              │◄──►│  Managers    │
└───────────┘    │  Registers (×4)          │    │  (PDC)       │
                 └──────────────────────────┘    └──────────────┘
┌───────────┐
│  Events   │── EffectAppliedEvent
└───────────┘
```

### Hit flow

```
EntityDamageByEntityEvent
  → DamageListener
    → EventDispatcher
      → PlayerClassManager.getPlayerClass()
      → PlayerDataManager.get() (talents)
      → Merge class + talent modifiers
        → EffectEngine.apply(context, modifiers)
          → condition.matches()? → effect.apply() → callEvent(EffectAppliedEvent)
    → event.damage *= multiplier
    → critical? → event.damage *= critMultiplier
    → ActionBar: "Zombie ❤ 34 (x1.25)"
```

## Build

```bash
./gradlew build
```

The JAR is generated in `build/libs/`.

## License

MIT
