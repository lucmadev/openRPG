package org.lucma.openRPG.models.conditions

import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Se activa si el jugador recibió daño en los últimos N segundos.
 * @param timeWindow segundos hacia atrás para considerar
 */
class RecentlyHurtCondition(
    private val timeWindow: Int = 3
) : Condition {

    override fun matches(context: EffectContext): Boolean {
        val lastTime = lastHurtTimes[context.player.uniqueId] ?: return false
        return (System.currentTimeMillis() - lastTime) < (timeWindow * 1000L)
    }

    companion object {
        private val lastHurtTimes = ConcurrentHashMap<UUID, Long>()

        fun markHurt(playerUUID: UUID) {
            lastHurtTimes[playerUUID] = System.currentTimeMillis()
        }
    }
}
