package org.lucma.openRPG.models.effects

import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.lucma.openRPG.core.effect.EffectPriority
import org.lucma.openRPG.models.data.EffectContext
import org.lucma.openRPG.models.types.Effect
import org.lucma.openRPG.models.types.StackType
import java.time.Duration

/**
 * Muestra un título en pantalla al jugador.
 * @param title texto del título principal (soporta & para colores)
 * @param subtitle texto del subtítulo (soporta & para colores)
 * @param fadeIn ticks de aparición
 * @param stay ticks de permanencia
 * @param fadeOut ticks de desaparición
 */
class TitleEffect(
    val title: String = "",
    val subtitle: String = "",
    val fadeIn: Int = 10,
    val stay: Int = 40,
    val fadeOut: Int = 10
) : Effect {
    override val priority = EffectPriority.CLASS
    override val stackType = StackType.ADDITIVE

    override fun apply(context: EffectContext) {
        if (title.isBlank() && subtitle.isBlank()) return

        val titleComponent = if (title.isNotBlank())
            Component.text(title.replace("&", "§"))
        else Component.empty()

        val subtitleComponent = if (subtitle.isNotBlank())
            Component.text(subtitle.replace("&", "§"))
        else Component.empty()

        val titleObj = Title.title(
            titleComponent,
            subtitleComponent,
            Title.Times.times(
                Duration.ofMillis(fadeIn * 50L),
                Duration.ofMillis(stay * 50L),
                Duration.ofMillis(fadeOut * 50L)
            )
        )

        context.player.showTitle(titleObj)
    }
}
