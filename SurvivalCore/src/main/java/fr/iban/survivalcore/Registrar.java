package fr.iban.survivalcore;

import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import fr.iban.survivalcore.commands.ActionBarCMD;
import fr.iban.survivalcore.commands.DolphinCMD;
import fr.iban.survivalcore.commands.HomesManageCMD;
import fr.iban.survivalcore.commands.LevelsCMD;
import fr.iban.survivalcore.commands.SimpleCommands;
import fr.iban.survivalcore.listeners.CommandListener;
import fr.iban.survivalcore.listeners.DamageListeners;
import fr.iban.survivalcore.listeners.EntityDeathListener;
import fr.iban.survivalcore.listeners.InventoryListener;
import fr.iban.survivalcore.listeners.PlayerExpChangeListener;
import fr.iban.survivalcore.listeners.PlayerFishListener;
import fr.iban.survivalcore.listeners.PortalListeners;
import fr.iban.survivalcore.listeners.RaidTriggerListener;
import fr.iban.survivalcore.listeners.ServerListPingListener;
import fr.iban.survivalcore.listeners.VillagerEvents;
import fr.iban.survivalcore.utils.papi.SpartaCubePlaceHolder;

public class Registrar {

	private SurvivalCorePlugin main;

	public Registrar(SurvivalCorePlugin main) {
		this.main = main;
		registerAll();
	}

	private void registerAll() {
		registerEvent(new ServerListPingListener());
		registerEvent(new InventoryListener());
		registerEvent(new DamageListeners());
		registerEvent(new PlayerFishListener());
		registerEvent(new EntityDeathListener());
		registerEvent(new PlayerExpChangeListener());
		registerEvent(new CommandListener());
		registerEvent(new VillagerEvents());
		registerEvent(new RaidTriggerListener());
		registerEvent(new PortalListeners());
		
		new SpartaCubePlaceHolder(main).register();
		
		main.getCommand("site").setExecutor(new SimpleCommands());
		main.getCommand("discord").setExecutor(new SimpleCommands());
		main.getCommand("vote").setExecutor(new SimpleCommands());
		main.getCommand("dynmap").setExecutor(new SimpleCommands());
		main.getCommand("boutique").setExecutor(new SimpleCommands());
		main.getCommand("tutoriel").setExecutor(new SimpleCommands());
		main.getCommand("stoptuto").setExecutor(new SimpleCommands());
		main.getCommand("abbc").setExecutor(new ActionBarCMD());
		main.getCommand("level").setExecutor(new LevelsCMD());
		main.getCommand("addhomes").setExecutor(new HomesManageCMD());
		main.getCommand("grades").setExecutor(new SimpleCommands());
		main.getCommand("pvp").setExecutor(new SimpleCommands());
		main.getCommand("dolphin").setExecutor(new DolphinCMD());
	}

	private void registerEvent(Listener listener) {
		PluginManager pm = main.getServer().getPluginManager();
		pm.registerEvents(listener, main);
	}

}

