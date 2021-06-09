package fr.iban.survivalcore.listeners;

import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import fr.iban.survivalcore.tools.SpecialTools;


public class InteractListeners implements Listener {
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		BlockFace bf = e.getBlockFace();
		if(bf != null) {
			SpecialTools.faces.put(e.getPlayer().getUniqueId(), bf);
		}

	}

}
