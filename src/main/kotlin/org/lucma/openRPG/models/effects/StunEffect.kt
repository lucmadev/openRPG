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
 * Aturde al objetivo, impidiéndole moverse por [duration] segundos.
 */
class StunEffect(
    val duration: Int = 2
) : Effect {
    override val priority = EffectPriority.CLASS
    override val stackType = StackType.ADDITIVE

    override fun apply(context: EffectContext) {
        val event = context.event
        if (event !is EntityDamageByEntityEvent) return
        val target = event.entity as? LivingEntity ?: return

        stunnedEntities[target.uniqueId] = System.currentTimeMillis() + (duration * 1000L)
    }

    companion object {
        private val stunnedEntities = ConcurrentHashMap<UUID, Long>()

        fun isStunned(uuid: UUID): Boolean {
            val expiry = stunnedEntities[uuid] ?: return false
            if (System.currentTimeMillis() >= expiry) {
                stunnedEntities.remove(uuid)
                return false
            }
            return true
        }
    }
}
