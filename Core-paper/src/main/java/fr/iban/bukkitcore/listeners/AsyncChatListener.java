package fr.iban.bukkitcore.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import fr.iban.bukkitcore.utils.PluginMessageHelper;

public class AsyncChatListener implements Listener {
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onChat(AsyncPlayerChatEvent e) {
		if(!e.isCancelled()) {
			PluginMessageHelper.sendGlobalMessage(e.getPlayer(), e.getMessage());
			e.setCancelled(true);
		}
	}
}
