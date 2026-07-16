package org.lucma.openRPG.models.conditions

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition

/**
 * Se activa cuando hay más enemigos que aliados cerca.
 * @param radius radio de búsqueda
 * @param ratio ratio mínimo enemigos:aliados (ej: 2.0 = al menos el doble de enemigos)
 */
class OutnumberedCondition(
    private val radius: Double = 5.0,
    private val ratio: Double = 1.5
) : Condition {

    override fun matches(context: EffectContext): Boolean {
        val nearbyEntities = context.player.getNearbyEntities(radius, radius, radius)
        var enemies = 0
        var allies = 0
        for (entity in nearbyEntities) {
            if (entity is Player) allies++
            else if (entity is LivingEntity) enemies++
        }
        return enemies > 0 && (enemies.toDouble() / (allies + 1).toDouble()) >= ratio
    }
}
