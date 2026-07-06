package org.lucma.openRPG.events

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.data.Modifier

/**
 * Se dispara después de que un [Modifier] (condición + efecto) se aplica
 * sobre un jugador. Otros plugins pueden escucharlo para:
 *
 * - Mostrar partículas o reproducir sonidos
 * - Registrar estadísticas de efectos aplicados
 * - Integrar PlaceholderAPI
 * - Implementar logros o misiones
 * - Añadir efectos visuales secundarios
 */
class EffectAppliedEvent(
    val player: Player,
    val modifier: Modifier,
    val context: EffectContext
) : Event() {

    override fun getHandlers(): HandlerList = handlerList

    companion object {
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = handlerList
    }
}
