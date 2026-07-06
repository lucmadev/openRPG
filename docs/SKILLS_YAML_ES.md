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

### close_enemies

Se activa cuando hay entidades vivas (no jugadores) cerca del jugador.

```yaml
condition:
  type: close_enemies
  config:
    radius: 5.0   # radio en bloques (default: 5.0)
```

### low_health

Se activa cuando la vida del jugador está por debajo de un porcentaje.

```yaml
condition:
  type: low_health
  config:
    threshold: 0.30   # 0.30 = 30% vida (default: 0.30)
```

### night_time

Se activa cuando es de noche en el mundo del jugador (13000–23000 ticks).

```yaml
condition:
  type: night_time
  config: {}   # sin parámetros
```

### sneaking

Se activa cuando el jugador está agachado.

```yaml
condition:
  type: sneaking
  config: {}   # sin parámetros
```

## Efectos disponibles

### damage_bonus

Multiplica el daño infligido.

```yaml
effect:
  type: damage_bonus
  config:
    bonus: 0.25   # 0.25 = +25% daño
```

### defense_bonus

Multiplica la defensa.

```yaml
effect:
  type: defense_bonus
  config:
    bonus: 0.15   # 0.15 = +15% defensa
```

### speed_bonus

Multiplica la velocidad.

```yaml
effect:
  type: speed_bonus
  config:
    bonus: 0.10   # 0.10 = +10% velocidad
```

### critical_chance

Añade probabilidad de golpe crítico (aditivo, no multiplicativo).

```yaml
effect:
  type: critical_chance
  config:
    chance: 0.08   # 0.08 = +8% prob. crítico
```

### critical_damage

Añade multiplicador de daño crítico (se suma al base 1.0).

```yaml
effect:
  type: critical_damage
  config:
    bonus: 0.50   # 0.50 = crítico hace x1.50 (base 1.0 + 0.50)
```

### heal

Cura una cantidad fija de vida.

```yaml
effect:
  type: heal
  config:
    amount: 2.0   # 2.0 = 1 corazón
```

### life_steal

Convierte un porcentaje del daño infligido en curación.

```yaml
effect:
  type: life_steal
  config:
    percentage: 0.10   # 0.10 = roba 10% del daño como vida
```

### fire_aura

Prende fuego al enemigo golpeado. Soporta cooldown.

```yaml
effect:
  type: fire_aura
  config:
    duration: 3       # segundos de combustión (default: 3)
    cooldown: 5       # segundos de cooldown entre usos (default: 0 = sin cd)
```

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
