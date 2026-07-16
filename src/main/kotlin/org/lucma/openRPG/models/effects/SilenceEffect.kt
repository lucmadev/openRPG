package org.lucma.openRPG.models.effects

import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.lucma.openRPG.core.effect.EffectPriority
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Effect
import org.lucma.openRPG.models.types.StackType
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Silencia al objetivo, impidiéndole usar ítems por [duration] segundos.
 * Almacena jugadores silenciados en memoria.
 */
class SilenceEffect(
    val duration: Int = 3
) : Effect {
    override val priority = EffectPriority.CLASS
    override val stackType = StackType.ADDITIVE

    override fun apply(context: EffectContext) {
        val event = context.event
        if (event !is EntityDamageByEntityEvent) return
        val target = event.entity as? LivingEntity ?: return
        silencedPlayers[target.uniqueId] = System.currentTimeMillis() + (duration * 1000L)
    }

    companion object {
        private val silencedPlayers = ConcurrentHashMap<UUID, Long>()

        fun isSilenced(uuid: UUID): Boolean {
            val expiry = silencedPlayers[uuid] ?: return false
            if (System.currentTimeMillis() >= expiry) {
                silencedPlayers.remove(uuid)
                return false
            }
            return true
        }
    }
}
