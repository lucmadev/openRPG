# Skills via YAML

Todos los nodos del árbol de talentos se cargan desde `plugins/openRPG/skills.yml`.  
No hace falta recompilar el plugin para agregar, modificar o eliminar skills.

## Ubicación del archivo

| Ubicación | Propósito |
|---|---|
| `resources/skills.yml` | Por defecto dentro del JAR |
| `plugins/openRPG/skills.yml` | Copia editable en el servidor |

Al iniciar el plugin, si no existe la copia en `plugins/`, se crea automáticamente desde el JAR.

## Estructura

```yaml
skills:
  <id_unico>:
    name: "<nombre visible>"
    description: "<descripción>"
    class: warrior|mage|assassin
    material: MATERIAL_BUKKIT
    prerequisites:
      - <id_del_skill_requerido>
    condition:
      type: <id_registro_condición>
      config:
        <param>: <valor>
    effect:
      type: <id_registro_efecto>
      config:
        <param>: <valor>
```

## Campos

| Campo | Obligatorio | Descripción |
|---|---|---|
| `id` (la clave YAML) | Sí | Identificador único. Ej: `war_damage_3` |
| `name` | Sí | Nombre visible en el GUI |
| `description` | No | Texto en el lore del item |
| `class` | Sí | `warrior`, `mage` o `assassin` |
| `material` | No | Material Bukkit. Default: `ENCHANTED_BOOK` |
| `prerequisites` | No | Lista de IDs que deben estar desbloqueados primero |
| `condition` | Sí | Condición que debe cumplirse para activar el skill |
| `effect` | Sí | Efecto que se aplica cuando la condición se cumple |

## Condiciones disponibles

### Básicas (originales)

| type | Parámetros | Descripción |
|---|---|---|
| `close_enemies` | `radius: 5.0` | Enemigos cerca |
| `low_health` | `threshold: 0.30` | Vida < umbral |
| `night_time` | — | Es de noche |
| `sneaking` | — | Agachado |
| `always` | — | Siempre activo |

### Ambientales

| type | Parámetros | Descripción |
|---|---|---|
| `raining` | — | Está lloviendo |
| `thundering` | — | Tormenta eléctrica |
| `day_time` | — | Es de día |
| `underground` | — | Bajo tierra (bloque sólido arriba) |
| `in_water` | — | En agua |
| `in_lava` | — | En lava |
| `on_fire` | — | En llamas |
| `in_cave` | `maxSkyLight: 7` | En cueva (poca luz de cielo) |
| `high_altitude` | `minY: 0`, `maxY: -1` | Entre Y levels |
| `biome` | `biome: plains`, `matchType: exact` | En bioma específico |

### Estado del jugador

| type | Parámetros | Descripción |
|---|---|---|
| `health_above` | `threshold: 0.70` | Vida > umbral |
| `full_health` | — | Vida al 100% |
| `sprinting` | — | Esprintando |
| `swimming` | — | Nadando |
| `gliding` | — | Planeando (elytra) |
| `on_ground` | — | En el suelo |
| `airborne` | — | En el aire |
| `hunger` | `threshold: 6`, `mode: below` | Hambre por debajo/encima |
| `experience_level` | `minLevel: 0`, `maxLevel: -1` | Nivel vanilla |
| `saturation` | `threshold: 5.0`, `mode: below` | Saturación |
| `fall_distance` | `minDistance: 3.0` | Distancia de caída |

### Estado especial

| type | Parámetros | Descripción |
|---|---|---|
| `has_potion_effect` | `effectType: ""` (cualquiera) | Tiene efecto de poción |
| `holding_item` | `material: ""`, `hand: main` | Sostiene ítem |
| `wearing_armor` | `material: ""` (cualquiera) | Usa armadura |
| `wearing_full_armor` | — | 4 piezas de armadura |
| `in_vehicle` | — | Montando algo |
| `sleeping` | — | En cama |

### Combate

| type | Parámetros | Descripción |
|---|---|---|
| `in_combat` | `timeSinceHit: 5` | En combate reciente |
| `no_enemies` | `radius: 5.0` | Sin enemigos cerca |
| `outnumbered` | `radius: 5.0`, `ratio: 1.5` | Superado en número |
| `target_low_health` | `threshold: 0.30` | Objetivo con vida baja |
| `target_full_health` | — | Objetivo vida al 100% |
| `last_hit_killer` | — | Golpe letal |
| `behind_target` | — | Detrás del objetivo |
| `blocking` | — | Bloqueando con escudo |
| `recently_hurt` | `timeWindow: 3` | Daño recibido hace N s |

### Compuestas / Lógicas

| type | Parámetros | Descripción |
|---|---|---|
| `and` | `conditions: [{type, config}]` | Todas las condiciones |
| `or` | `conditions: [{type, config}]` | Alguna condición |
| `not` | `condition: {type, config}` | Niega la condición |
| `random` | `chance: 0.5` | Probabilidad aleatoria |
| `cooldown` | `key: "x"`, `seconds: 5` | Cooldown entre usos |

### Clase / Datos

| type | Parámetros | Descripción |
|---|---|---|
| `has_class` | `classId: ""` | Tiene clase (específica o cualquiera) |
| `has_talent` | `nodeId: "..."` | Talento desbloqueado |
| `player_level` | `minLevel: 1`, `maxLevel: -1` | Nivel openRPG |
| `talent_points` | `minPoints: 1` | Puntos de talento disponibles |

### Misceláneas

| type | Parámetros | Descripción |
|---|---|---|
| `in_region` | `world`, `x1,z1,x2,z2` | Dentro de región |
| `moon_phase` | `phase: 0` (0-7) | Fase lunar |
| `light_level` | `min: 0`, `max: 15`, `type: any` | Nivel de luz |
| `in_world` | `worldName: "world"` | En mundo específico |

## Efectos disponibles

### Básicos (originales)

| type | Parámetros | Descripción |
|---|---|---|
| `damage_bonus` | `bonus: 0.25` | +% daño infligido |
| `defense_bonus` | `bonus: 0.15` | +% defensa |
| `speed_bonus` | `bonus: 0.10` | +% velocidad |
| `critical_chance` | `chance: 0.08` | +% prob. crítico |
| `critical_damage` | `bonus: 0.50` | + multi. crítico |
| `heal` | `amount: 2.0` | Curación fija |
| `life_steal` | `percentage: 0.10` | Robo de vida % |
| `fire_aura` | `duration: 3`, `cooldown: 5` | Ígneo al golpear |

### Estado del objetivo

| type | Parámetros | Descripción |
|---|---|---|
| `wither` | `duration: 3`, `amplifier: 1` | Aplica Wither |
| `poison` | `duration: 3`, `amplifier: 1` | Aplica Poison |
| `slowness` | `duration: 3`, `amplifier: 1` | Aplica lentitud |
| `weakness` | `duration: 3`, `amplifier: 1` | Aplica debilidad |
| `blindness` | `duration: 3` | Aplica ceguera |
| `levitation` | `duration: 3`, `amplifier: 1` | Aplica levitación |
| `glow` | `duration: 5` | Hace brillar al objetivo |
| `hunger_effect` | `duration: 5`, `amplifier: 1` | Aplica hambre |
| `mining_fatigue` | `duration: 5`, `amplifier: 1` | Fatiga de minería |
| `nausea` | `duration: 3` | Aplica náusea |
| `silence` | `duration: 3` | Silencia (no usa ítems) |
| `disarm` | `dropChance: 1.0` | Desarma (suelta ítem) |
| `stun` | `duration: 2` | Aturde (no se mueve) |
| `bleed` | `duration: 3`, `damagePerTick: 0.5` | Sangrado (DoT) |

### Daño especial

| type | Parámetros | Descripción |
|---|---|---|
| `knockback` | `multiplier: 0.5` | +% retroceso |
| `execute` | `bonusMultiplier: 0.5`, `threshold: 0.30` | +daño a vida baja |
| `backstab` | `bonusMultiplier: 0.5` | +daño por detrás |
| `charge` | `bonusMultiplier: 0.3` | +daño esprintando |
| `area_damage` | `radius: 3.0`, `multiplier: 0.5` | Daño en área |
| `chain_damage` | `radius: 5.0`, `maxTargets: 3`, `multiplier: 0.7` | Daño en cadena |
| `true_damage` | `percentage: 0.20` | Daño verdadero |
| `lightning_strike` | `damage: 0.0` | Rayo + daño |
| `explosion` | `power: 2.0`, `fire: false` | Explosión |
| `shield_breaker` | `bonusMultiplier: 0.30` | Rompescudos |
| `splash_damage` | `radius: 3.0`, `multiplier: 0.4` | Salpicadura |

### Curación / Defensa

| type | Parámetros | Descripción |
|---|---|---|
| `absorption` | `hearts: 4.0` | Corazones de absorción |
| `regeneration` | `duration: 5`, `amplifier: 1` | Regeneración gradual |
| `damage_reduction` | `flatReduction: 1.0` | Reduce daño recibido |
| `damage_reflect` | `percentage: 0.10` | Refleja % daño |
| `dodge` | `chance: 0.10` | Prob. de esquivar |
| `shield` | `health: 5.0` | Escudo absorbente |
| `fire_resistance` | `duration: 30` | Resistencia al fuego |
| `water_breathing` | `duration: 30` | Respiración acuática |
| `invulnerability` | `duration: 2` | Invulnerabilidad breve |

### Movimiento

| type | Parámetros | Descripción |
|---|---|---|
| `jump_boost` | `multiplier: 0.5` | +% salto |
| `slow_fall` | `duration: 5` | Caída lenta |
| `leap` | `power: 1.5`, `upward: 0.5` | Impulso adelante |
| `dash` | `distance: 5.0` | Dash |
| `speed_aura` | `radius: 8.0`, `amplifier: 1` | Velocidad a aliados |
| `web` | `radius: 2`, `duration: 3` | Telarañas |

### Buffs (pociones)

| type | Parámetros | Descripción |
|---|---|---|
| `strength` | `duration: 10`, `amplifier: 1` | Fuerza |
| `speed_potion` | `duration: 10`, `amplifier: 1` | Velocidad |
| `resistance` | `duration: 10`, `amplifier: 1` | Resistencia |
| `invisibility` | `duration: 10` | Invisibilidad |
| `night_vision` | `duration: 30` | Visión nocturna |
| `haste` | `duration: 10`, `amplifier: 1` | Prisa |
| `dolphin_grace` | `duration: 15` | Gracia del delfín |
| `luck_potion` | `duration: 30`, `amplifier: 1` | Suerte |

### Recursos

| type | Parámetros | Descripción |
|---|---|---|
| `exp_bonus` | `multiplier: 0.5` | +% EXP |
| `loot_bonus` | `multiplier: 0.5` | +% botín |
| `mining_speed` | `multiplier: 0.5` | +% minería |
| `health_regen` | `multiplier: 0.5` | +% regeneración |
| `mana_regen` | `regenPerSecond: 1.0` | Regeneración de maná |
| `saturation` | `amount: 5.0` | Restaura saturación |
| `feed` | `hungerRestored: 4` | Restaura hambre |

### Visuales / Sonido

| type | Parámetros | Descripción |
|---|---|---|
| `particle` | `particle: CRIT`, `count: 10`, `speed: 0.1` | Partículas |
| `sound` | `sound: ENTITY_PLAYER_LEVELUP`, `volume: 1.0`, `pitch: 1.0` | Sonido |
| `title` | `title`, `subtitle`, `fadeIn: 10`, `stay: 40`, `fadeOut: 10` | Título en pantalla |

### Otros

| type | Parámetros | Descripción |
|---|---|---|
| `life_steal_multiplier` | `multiplier: 0.5` | Multiplica robo de vida |

## Ejemplos

### Skill básico sin requisitos

```yaml
war_damage_1:
  name: "Furia"
  description: "+15% daño cerca de enemigos"
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

### Skill con requisito (cadena)

```yaml
war_damage_2:
  name: "Torbellino"
  description: "+25% daño cerca de enemigos"
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

### Skill con cooldown

```yaml
mag_fire_1:
  name: "Llamas eternas"
  description: "+3s ígneo (cooldown 4s)"
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

## Buenas prácticas

### IDs

Usa un prefijo de 3 letras para la clase + nombre descriptivo:

| Clase | Prefijo | Ejemplo |
|---|---|---|
| Guerrero | `war_` | `war_damage_3` |
| Mago | `mag_` | `mag_fire_2` |
| Asesino | `ass_` | `ass_crit_dmg_2` |

### Materiales

Usa materiales de Bukkit que representen visualmente el skill:

| Skill | Material |
|---|---|
| Daño cuerpo a cuerpo | `IRON_SWORD`, `DIAMOND_SWORD` |
| Defensa | `SHIELD` |
| Velocidad | `FEATHER`, `SUGAR` |
| Crítico | `REDSTONE`, `SPIDER_EYE` |
| Curación | `APPLE`, `GOLDEN_APPLE` |
| Fuego | `BLAZE_ROD`, `FIRE_CHARGE` |
| Robo de vida | `FERMENTED_SPIDER_EYE` |
| Magia | `ENDER_PEARL`, `DRAGON_BREATH` |

### Prerequisites

- Los IDs en `prerequisites` deben coincidir exactamente con la clave del skill requerido
- Se pueden crear cadenas de cualquier profundidad: `A → B → C → D`
- Un skill puede tener múltiples prerequisitos: `prerequisites: [war_damage_1, war_defense_1]`

## Escalabilidad

Para escalar a **1328 skills** (o los que sean):

1. Agregar las entradas en `skills.yml`
2. Hacer `/reload` en el servidor
3. Los jugadores ven los nuevos nodos en el GUI de talentos

No se necesita:
- ❌ Recompilar el plugin
- ❌ Modificar ningún archivo Kotlin
- ❌ Reiniciar el servidor (solo `/reload`)
- ❌ Registrar nada en código

## Depuración

Al cargar, la consola muestra:

```
[openRPG] Skills: 18 cargados, 0 errores
```

Si hay errores de sintaxis o referencias a condiciones/efectos inexistentes:

```
[openRPG] Error skill 'mag_fire_X': condición 'explosion' no encontrada
[openRPG] Skills: 17 cargados, 1 errores
```

Los skills con error simplemente se saltan; el resto del árbol funciona normalmente.
