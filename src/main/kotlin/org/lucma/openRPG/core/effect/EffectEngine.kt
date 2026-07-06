package org.lucma.openRPG.core.effect

import org.bukkit.Bukkit
import org.lucma.openRPG.events.EffectAppliedEvent
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.data.Modifier
import org.lucma.openRPG.models.types.StackType

object EffectEngine {

    fun apply(context: EffectContext, modifiers: List<Modifier>) {
        try {
            val grouped = modifiers.groupBy { it.effect.stackType }

            StackType.entries.forEach { stackType ->

                val sorted = grouped[stackType]
                    ?.sortedByDescending { it.effect.priority }
                    ?: return@forEach

                Bukkit.getLogger().fine("[openRPG] Procesando stackType=$stackType (${sorted.size} modificadores)")

                sorted.forEach { modifier ->

                    val conditionMet = modifier.condition.matches(context)
                    Bukkit.getLogger().fine("[openRPG]   condition=${modifier.condition::class.simpleName} | matches=$conditionMet | effect=${modifier.effect::class.simpleName}")

                    if (conditionMet) {
                        modifier.effect.apply(context)

                        Bukkit.getPluginManager().callEvent(
                            EffectAppliedEvent(
                                player = context.player,
                                modifier = modifier,
                                context = context
                            )
                        )
                    }

                }

            }

        } catch (ex: Exception) {
            Bukkit.getLogger().severe("[openRPG] Error en EffectEngine: ${ex.javaClass.simpleName}: ${ex.message}")
            ex.printStackTrace()
        }
    }

}
