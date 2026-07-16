package org.lucma.openRPG.models.conditions

import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Condition

/**
 * Se activa cuando el jugador está en un bioma específico.
 * @param biome nombre del bioma (case-insensitive, ej: "plains", "desert")
 * @param matchType "exact" (default) o "category"
 */
class BiomeCondition(
    private val biome: String,
    private val matchType: String = "exact"
) : Condition {
    override fun matches(context: EffectContext): Boolean {
        val playerBiome = context.player.location.block.biome
        val biomeStr = playerBiome.key.asString().lowercase()
        val search = biome.lowercase().removePrefix("minecraft:")
        return if (matchType == "category") {
            biomeStr.contains(search)
        } else {
            biomeStr == "minecraft:$search" || biomeStr == search
        }
    }
}
