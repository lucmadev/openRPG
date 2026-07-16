package org.lucma.openRPG.models.conditions

import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Se activa si el jugador ha estado en combate recientemente.
 * @param timeSinceHit segundos desde el último golpe (recibido o dado)
 */
class InCombatCondition(
    private val timeSinceHit: Int = 5
) : Condition {

    override fun matches(context: EffectContext): Boolean {
        val lastHit = lastHitTimes[context.player.uniqueId] ?: return false
        return (System.currentTimeMillis() - lastHit) < (timeSinceHit * 1000L)
    }

    companion object {
        private val lastHitTimes = ConcurrentHashMap<UUID, Long>()

        fun markHit(playerUUID: UUID) {
            lastHitTimes[playerUUID] = System.currentTimeMillis()
        }
    }
}
