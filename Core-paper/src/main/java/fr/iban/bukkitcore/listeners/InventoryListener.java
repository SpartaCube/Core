package fr.iban.bukkitcore.listeners;

import org.bukkit.event.EventHandler;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

import fr.iban.bukkitcore.CoreBukkitPlugin;
import fr.iban.bukkitcore.menu.Menu;

public class InventoryListener implements Listener {
	
	  @EventHandler
	  public void onInventoryClick(InventoryClickEvent e) {
	    if (e.getInventory() instanceof org.bukkit.inventory.AnvilInventory && e.getWhoClicked() instanceof Player) {
	      Player p = (Player)e.getWhoClicked();
	      if (e.getCurrentItem() != null)
	        if (e.getCurrentItem().hasItemMeta()) {
	          ItemMeta meta = e.getCurrentItem().getItemMeta();
	          if (meta.hasLore()) {
	            List<String> lore = meta.getLore();
	            for (int i = 0; i < CoreBukkitPlugin.getInstance().getLores().length; i++) {
	              if (CoreBukkitPlugin.blockall) {
	                if (p.hasPermission("spartacube.spartacube.repair.nolore") || p.hasPermission("spartacube.spartacube.norepair.nolore")) {
	                  p.sendMessage(ChatColor.RED + "Error: " + ChatColor.DARK_RED + "Cet item ne peut pas être réparé.");
	                  e.setCancelled(true);
	                  break;
	                } 
	              } else if (lore.contains(CoreBukkitPlugin.getInstance().getLores()[i])) {
	                if (p.hasPermission("spartacube.repair.nolore") || p.hasPermission("spartacube.norepair.nolore")) {
	                  p.sendMessage(ChatColor.RED + "Erreur: " + ChatColor.DARK_RED + "Cet item ne peut pas être réparé");
	                  e.setCancelled(true);
	                  break;
	                } 
	              } 
	            } 
	          } 
	        }  
	    } 
	  }

    @EventHandler
    public void onMenuClick(InventoryClickEvent e){

        InventoryHolder holder = e.getInventory().getHolder();
        //If the inventoryholder of the inventory clicked on
        // is an instance of Menu, then gg. The reason that
        // an InventoryHolder can be a Menu is because our Menu
        // class implements InventoryHolder!!
        if (holder instanceof Menu) {
            e.setCancelled(true); //prevent them from fucking with the inventory
            if (e.getCurrentItem() == null) { //deal with null exceptions
                return;
            }
            //Since we know our inventoryholder is a menu, get the Menu Object representing
            // the menu we clicked on
            Menu menu = (Menu) holder;
            //Call the handleMenu object which takes the event and processes it
            menu.handleMenu(e);
        }

    }
    
    @EventHandler
    public void onMenuClose(InventoryCloseEvent e){

        InventoryHolder holder = e.getInventory().getHolder();

        if (holder instanceof Menu) {
            Menu menu = (Menu) holder;
            menu.handleMenuClose(e);
        }

    }



}
