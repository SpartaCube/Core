package fr.iban.bukkitcore.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.iban.bukkitcore.CoreBukkitPlugin;
import fr.iban.bukkitcore.utils.PluginMessageHelper;

public class JoinQuitListeners implements Listener {
	
	private CoreBukkitPlugin plugin;
	
	
	public JoinQuitListeners(CoreBukkitPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		e.setJoinMessage(null);
		if(plugin.getServerName() == null) {
			Bukkit.getScheduler().runTaskLater(plugin, () -> PluginMessageHelper.askServerName(e.getPlayer()), 10l);
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		e.setQuitMessage(null);
		if(plugin.getTextInputs().containsKey(player.getUniqueId())) {
			plugin.getTextInputs().remove(player.getUniqueId());
		}
	}

}
