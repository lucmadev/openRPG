package org.lucma.openRPG

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.lucma.openRPG.api.OpenRPGAPI
import org.lucma.openRPG.api.OpenRPGAPIImpl
import org.lucma.openRPG.commands.OpenRPGCommand
import org.lucma.openRPG.core.LanguageManager
import org.lucma.openRPG.core.SkillLoader
import org.lucma.openRPG.core.registry.ClassRegistry
import org.lucma.openRPG.core.registry.ConditionRegistry
import org.lucma.openRPG.core.registry.EffectRegistry
import org.lucma.openRPG.core.registry.StatRegistry
import org.lucma.openRPG.gui.ClassSelectionGUI
import org.lucma.openRPG.gui.StatusGUI
import org.lucma.openRPG.gui.TalentGUI
import org.lucma.openRPG.listeners.DamageListener
import org.lucma.openRPG.listeners.EffectVisualizer
import org.lucma.openRPG.listeners.ExpListener
import org.lucma.openRPG.listeners.PlayerConnectionListener
import org.lucma.openRPG.managers.PlayerClassManager
import org.lucma.openRPG.managers.PlayerDataManager
import org.lucma.openRPG.models.classes.Assassin
import org.lucma.openRPG.models.classes.Mage
import org.lucma.openRPG.models.classes.Warrior

class OpenRPG : JavaPlugin() {

    override fun onEnable() {
        instance = this

        // ── Language ──
        LanguageManager.init(this)

        // ── Registries ──
        ClassRegistry.register(Warrior())
        ClassRegistry.register(Mage())
        ClassRegistry.register(Assassin())
        ConditionRegistry.registerDefaults()
        EffectRegistry.registerDefaults()
        StatRegistry.registerDefaults()

        // ── Skills (desde YAML) ──
        SkillLoader.load(this)

        // ── Listeners ──
        server.pluginManager.registerEvents(DamageListener(), this)
        server.pluginManager.registerEvents(EffectVisualizer, this)
        server.pluginManager.registerEvents(PlayerConnectionListener, this)
        server.pluginManager.registerEvents(ExpListener, this)
        server.pluginManager.registerEvents(TalentGUI, this)
        server.pluginManager.registerEvents(ClassSelectionGUI, this)
        server.pluginManager.registerEvents(StatusGUI, this)

        // ── Commands ──
        getCommand("openrpg")?.setExecutor(OpenRPGCommand())
            ?: logger.warning("No se pudo registrar /openrpg")

        // ── API ──
        Bukkit.getServicesManager().register(OpenRPGAPI::class.java, OpenRPGAPIImpl(), this, org.bukkit.plugin.ServicePriority.Normal)

        logger.info("openRPG habilitado correctamente.")
    }

    override fun onDisable() {
        logger.info("Guardando datos de jugadores online...")
        Bukkit.getOnlinePlayers().forEach { player ->
            val clazz = PlayerClassManager.getPlayerClass(player)
            if (clazz != null) {
                PlayerClassManager.saveToPDC(player, clazz.id)
            }
            PlayerDataManager.save(player)
        }
        logger.info("openRPG deshabilitado.")
    }

    companion object {
        lateinit var instance: OpenRPG
            private set
    }
}
