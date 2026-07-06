package org.lucma.openRPG.core

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.InputStreamReader
import java.util.Properties

/**
 * Gestor de internacionalización.
 * Carga archivos [language].properties desde resources/messages/
 * y resuelve mensajes por clave + locale del jugador.
 *
 * Uso:
 *     LanguageManager.msg("gui.class_selection.title")
 *     LanguageManager.msg("class.warrior.name", player)
 *     LanguageManager.msg("effect.damage_bonus", player, "+25")
 */
object LanguageManager {

    private val bundles = mutableMapOf<String, Properties>()
    private val defaults = Properties()

    private const val FALLBACK = "en"

    fun init(plugin: JavaPlugin) {
        val languages = listOf("en", "es")
        for (lang in languages) {
            val props = Properties()
            try {
                val path = "messages/messages_$lang.properties"
                val stream = plugin.getResource(path) ?: continue
                props.load(InputStreamReader(stream, "UTF-8"))
                bundles[lang] = props
                Bukkit.getLogger().info("[openRPG] Cargadas traducciones: $lang (${props.size} claves)")
            } catch (e: Exception) {
                Bukkit.getLogger().warning("[openRPG] Error cargando idioma $lang: ${e.message}")
            }
        }

        // Cargar default = english
        val fallback = bundles[FALLBACK]
        if (fallback != null) {
            for (key in fallback.stringPropertyNames()) {
                defaults.setProperty(key, fallback.getProperty(key))
            }
        }
    }

    /** Obtiene un mensaje por clave, formateado para el locale del jugador. */
    fun msg(key: String, player: Player? = null, vararg args: Any?): String {
        val lang = if (player != null) {
            player.locale().language
        } else FALLBACK
        return resolve(key, lang, args)
    }

    /** Obtiene un mensaje por clave para un locale específico ("en", "es"). */
    fun msg(key: String, locale: String, vararg args: Any?): String {
        return resolve(key, locale, args)
    }

    private fun resolve(key: String, locale: String, args: Array<out Any?>): String {
        val bundle = bundles[locale] ?: bundles[FALLBACK]
        val raw = bundle?.getProperty(key) ?: defaults.getProperty(key) ?: "§7{$key}"

        val colored = raw.replace('&', '\u00A7')
        return if (args.isEmpty()) colored else format(colored, args)
    }

    private fun format(template: String, args: Array<out Any?>): String {
        var result = template
        args.forEachIndexed { i, arg ->
            result = result.replace("{$i}", arg?.toString() ?: "")
        }
        return result
    }

    /** Verifica si una clave existe en los mensajes cargados. */
    fun hasKey(key: String): Boolean {
        return defaults.containsKey(key) || bundles.values.any { it.containsKey(key) }
    }

    /** Recarga los archivos de idioma */
    fun reload(plugin: JavaPlugin) {
        bundles.clear()
        defaults.clear()
        init(plugin)
    }
}
