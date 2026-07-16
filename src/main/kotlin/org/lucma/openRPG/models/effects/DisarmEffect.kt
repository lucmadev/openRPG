package org.lucma.openRPG.models.effects

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.lucma.openRPG.core.effect.EffectPriority
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Effect
import org.lucma.openRPG.models.types.StackType

/**
 * Desarma al objetivo, soltando el ítem de su mano principal.
 */
class DisarmEffect(
    val dropChance: Double = 1.0
) : Effect {
    override val priority = EffectPriority.CLASS
    override val stackType = StackType.ADDITIVE

    override fun apply(context: EffectContext) {
        val event = context.event
        if (event !is EntityDamageByEntityEvent) return
        val target = event.entity as? Player ?: return
        if (kotlin.random.Random.nextDouble() >= dropChance) return

        val item = target.inventory.itemInMainHand
        if (item.type.isAir) return

        target.world.dropItemNaturally(target.location, item)
        target.inventory.setItemInMainHand(org.bukkit.inventory.ItemStack(org.bukkit.Material.AIR))
    }
}
