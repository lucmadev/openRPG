package org.lucma.openRPG.models.conditions

import org.bukkit.block.BlockFace
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition

/**
 * Se activa cuando el jugador está bajo tierra
 * (tiene un bloque sólido sobre la cabeza).
 */
class UndergroundCondition : Condition {
    override fun matches(context: EffectContext): Boolean {
        val blockAbove = context.player.location.block.getRelative(BlockFace.UP)
        return blockAbove.type.isSolid
    }
}
