package org.lucma.openRPG.core

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.InputStreamReader
import java.util.Properties

/**
 * Internationalization manager.
 * Loads [language].properties files from resources/messages/
 * and resolves messages by key + player locale.
 *
 * Usage:
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
                Bukkit.getLogger().info("[openRPG] Loaded translations: $lang (${props.size} keys)")
            } catch (e: Exception) {
                Bukkit.getLogger().warning("[openRPG] Error loading language $lang: ${e.message}")
            }
        }

        // Load default = english
        val fallback = bundles[FALLBACK]
        if (fallback != null) {
            for (key in fallback.stringPropertyNames()) {
                defaults.setProperty(key, fallback.getProperty(key))
            }
        }
    }

    /** Get a message by key, formatted for the player's locale. */
    fun msg(key: String, player: Player? = null, vararg args: Any?): String {
        val lang = if (player != null) {
            player.locale().language
        } else FALLBACK
        return resolve(key, lang, args)
    }

    /** Get a message by key for a specific locale ("en", "es"). */
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

    /** Check if a key exists in the loaded messages. */
    fun hasKey(key: String): Boolean {
        return defaults.containsKey(key) || bundles.values.any { it.containsKey(key) }
    }

    /** Reload the language files */
    fun reload(plugin: JavaPlugin) {
        bundles.clear()
        defaults.clear()
        init(plugin)
    }
}
