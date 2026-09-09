package org.lucma.openRPG.core

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.`object`.ObjectContents
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
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
    fun msgLocale(key: String, locale: String, vararg args: Any?): String {
        return resolve(key, locale, args)
    }

    /**
     * Localized message as a Component. [Player] args become a head + name mention.
     */
    fun msgComponent(key: String, player: Player, vararg args: Any?): Component {
        val lang = player.locale().language
        val template = raw(key, lang)
        return compose(template, args)
    }

    /** Inline player head followed by the player's name. */
    fun playerMention(player: Player): Component {
        val head = Component.`object`(
            ObjectContents.playerHead()
                .name(player.name)
                .id(player.uniqueId)
                .build()
        )
        return Component.text()
            .append(head)
            .append(Component.space())
            .append(Component.text(player.name))
            .build()
    }

    private fun raw(key: String, locale: String): String {
        val bundle = bundles[locale] ?: bundles[FALLBACK]
        val value = bundle?.getProperty(key) ?: defaults.getProperty(key) ?: "§7{$key}"
        return value.replace('&', '\u00A7')
    }

    private fun resolve(key: String, locale: String, args: Array<out Any?>): String {
        val colored = raw(key, locale)
        return if (args.isEmpty()) colored else format(colored, args)
    }

    private fun compose(template: String, args: Array<out Any?>): Component {
        if (args.isEmpty()) {
            return LegacyComponentSerializer.legacySection().deserialize(template)
        }
        val pattern = Regex("\\{(\\d+)}")
        var last = 0
        var result = Component.empty()
        for (match in pattern.findAll(template)) {
            val before = template.substring(last, match.range.first)
            if (before.isNotEmpty()) {
                result = result.append(LegacyComponentSerializer.legacySection().deserialize(before))
            }
            val index = match.groupValues[1].toInt()
            result = result.append(argToComponent(args.getOrNull(index)))
            last = match.range.last + 1
        }
        if (last < template.length) {
            result = result.append(LegacyComponentSerializer.legacySection().deserialize(template.substring(last)))
        }
        return result
    }

    private fun argToComponent(arg: Any?): Component {
        return when (arg) {
            null -> Component.empty()
            is Component -> arg
            is Player -> playerMention(arg)
            else -> Component.text(arg.toString())
        }
    }

    private fun format(template: String, args: Array<out Any?>): String {
        var result = template
        args.forEachIndexed { i, arg ->
            result = result.replace("{$i}", arg?.toString() ?: "")
        }
        return result
    }

    /** Localized class display name, falling back to [fallback] for custom/API classes. */
    fun classDisplayName(classId: String, player: Player?, fallback: String = classId): String {
        val key = "class.$classId.name"
        return if (hasKey(key)) msg(key, player) else fallback
    }

    /** Localized skill name, falling back to the YAML/API name. */
    fun skillName(id: String, player: Player?, fallback: String): String {
        val key = "skill.$id.name"
        return if (hasKey(key)) msg(key, player) else fallback
    }

    /** Localized skill description, falling back to the YAML/API description. */
    fun skillDesc(id: String, player: Player?, fallback: String): String {
        val key = "skill.$id.desc"
        return if (hasKey(key)) msg(key, player) else fallback
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
