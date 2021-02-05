package fr.iban.survivalcore.listeners;


import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
		
		if(e.getMessage().toLowerCase().contains("kill") && e.getMessage().toLowerCase().contains("all")) {
			e.setCancelled(true);
		}
	}

}
