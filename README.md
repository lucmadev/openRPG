# openRPG

Sistema RPG modular para servidores Paper 1.21+. Añade clases, habilidades pasivas, árbol de talentos, estadísticas y un sistema de progresión por EXP — todo con interfaz gráfica de inventario.

## Características

- **3 clases jugables**: Guerrero, Mago y Asesino, cada una con modificadores únicos
- **Sistema de condiciones**: los efectos se activan según el contexto (cerca de enemigos, de noche, vida baja, agachado)
- **Árbol de talentos**: 6 talentos por clase, con prerequisitos y puntos por nivel
- **Sistema de EXP**: ganas experiencia al matar mobs, subes de nivel y obtienes puntos de talento
- **Estadísticas**: daño, defensa, velocidad, probabilidad de crítico y multiplicador de crítico
- **Interfaz gráfica**: todos los sistemas tienen GUI de inventario (selección de clase, estado, árbol de talentos)
- **Persistencia**: los datos se guardan en el PersistentDataContainer del jugador (no requiere archivos externos)
- **API pública**: otros plugins pueden extender el sistema con clases, condiciones y efectos propios
- **Evento personalizado**: `EffectAppliedEvent` para que otros plugins reaccionen sin acoplarse

## Requisitos

- **Servidor**: Paper 1.21+ (API 26.2+)
- **Java**: 21+ (jvmToolchain 25)

## Instalación

1. Descarga el JAR desde [releases](https://github.com/Lucma/openRPG/releases)
2. Colócalo en la carpeta `plugins/` de tu servidor
3. Reinicia el servidor

## Comandos

| Comando | Alias | Descripción |
|---|---|---|
| `/assignclass [id]` | `/class`, `/clase` | Abre GUI de selección de clase o asigna por ID |
| `/rpg` | `/pg`, `/stats` | Abre GUI de estado del jugador |
| `/talent` | `/talento`, `/skill` | Abre GUI del árbol de talentos |

## Clases

### 🗡️ Guerrero
Bonos cuando hay enemigos cerca:
- +10% daño
- +5% defensa
- +5% velocidad
- +3% prob. crítico
- +0.20 multi. crítico

### 🔮 Mago
Bonos de noche y cuando está herido:
- +15% daño de noche
- +5% prob. crítico de noche
- +10% defensa si vida < 30%
- +0.25 multi. crítico si vida < 30%
- Quema 2s al golpear
- +5% velocidad cerca de enemigos

### 🗡️ Asesino
Bonos al agacharse y cuando está herido:
- +20% daño agachado
- +8% prob. crítico agachado
- +0.30 multi. crítico agachado
- +10% velocidad agachado
- 5% robo de vida si vida < 30%

## Árbol de talentos

Cada nivel otorga **1 punto de talento** (máx 50 niveles). Los puntos se gastan en el árbol de talentos de tu clase.

Ejemplo de nodos del Guerrero:

```
Furia         → +15% daño (sin requisito)
Torbellino    → +25% daño (requiere: Furia)
Fortaleza     → +15% defensa
Instinto asesino → +7% prob. crítico
Golpe brutal  → +0.50 multi. crítico
Regeneración  → Cura 2❤ si vida < 30%
```

Usa `/talent` para abrir el árbol y haz clic en los nodos para aprenderlos.

## API para desarrolladores

openRPG expone una API pública via `ServicesManager` de Bukkit.

### Dependencia

**`plugin.yml`**:
```yaml
depend: [openRPG]
```

### Uso básico

```kotlin
import org.lucma.openRPG.api.OpenRPGAPI
import org.bukkit.Bukkit

val api = Bukkit.getServicesManager().load(OpenRPGAPI::class.java)

// Registrar una clase personalizada
api.registerClass(MyCustomClass())

// Aplicar modifiers temporales (buffos, equipo)
api.applyModifiers(player, event, listOf(
    api.modifier(AlwaysCondition(), DamageBonusEffect(0.10))
))
```

### Escuchar eventos

```kotlin
@EventHandler
fun onEffect(event: EffectAppliedEvent) {
    val player = event.player
    val modifier = event.modifier
    // partículas, sonidos, estadísticas...
}
```

### API completa

| Método | Descripción |
|---|---|
| `registerClass(clazz)` | Registra una clase jugable |
| `getPlayerClass(player)` | Clase actual del jugador |
| `setPlayerClass(player, clazz)` | Asigna clase (persiste en PDC) |
| `getPlayerData(player)` | Nivel, EXP, talentos del jugador |
| `addExp(player, amount)` | Añade EXP (controla level ups) |
| `applyModifiers(player, event, modifiers)` | Aplica modifiers al jugador |
| `registerEffect(id, factory)` | Registra un efecto desde configuración |
| `createEffect(id, config)` | Crea un efecto desde configuración |

## Arquitectura

```
                         ┌──────────────┐
                         │  OpenRPGAPI  │ ← API pública (ServicesManager)
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

### Flujo de un golpe

```
EntityDamageByEntityEvent
  → DamageListener
    → EventDispatcher
      → PlayerClassManager.getPlayerClass()
      → PlayerDataManager.get() (talentos)
      → Fusiona modifiers de clase + talentos
        → EffectEngine.apply(context, modifiers)
          → condition.matches()? → effect.apply() → callEvent(EffectAppliedEvent)
    → event.damage *= multiplier
    → ¿crítico? → event.damage *= critMultiplier
    → ActionBar: "Zombie ❤ 34 (x1.25)"
```

## Compilación

```bash
./gradlew build
```

El JAR se genera en `build/libs/`.

## Licencia

MIT
