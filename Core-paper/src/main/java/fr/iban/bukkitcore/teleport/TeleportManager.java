package fr.iban.bukkitcore.teleport;

import org.bukkit.entity.Player;

import fr.iban.bukkitcore.CoreBukkitPlugin;
import fr.iban.common.teleport.SLocation;
import fr.iban.common.teleport.TeleportToLocation;

public class TeleportManager {
	
	private CoreBukkitPlugin plugin;

	public TeleportManager(CoreBukkitPlugin plugin) {
		this.plugin = plugin;
	}
	
	public void teleport(Player player, SLocation sloc) {
		plugin.getRedisClient().getTopic("TeleportToSLoc").publish(new TeleportToLocation(player.getUniqueId(), sloc));
	}
	
	public void teleport(Player player, SLocation sloc, int delay) {
		plugin.getRedisClient().getTopic("TeleportToSLoc").publish(new TeleportToLocation(player.getUniqueId(), sloc, delay));
	}
}
