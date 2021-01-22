package fr.iban.bukkitcore.menu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/*

A class extending the functionality of the regular Menu, but making it Paginated

This pagination system was made from Jer's code sample. <3

 */

public abstract class PaginatedMenu extends Menu {

    //Keep track of what page the menu is on
    protected int page = 0;
    //28 is max items because with the border set below,
    //28 empty slots are remaining.
    protected int maxItemsPerPage = 28;
    //the index represents the index of the slot
    //that the loop is on
    protected int index = 0;
    
    protected int elementAmount = -1;
    
    public PaginatedMenu(Player player) {
    	super(player);
    }
    
    
    //Set the border and menu buttons for the menu
    @Override
    public void addMenuBorder(){
    	
    	if(elementAmount != -1 && elementAmount > maxItemsPerPage) {
            inventory.setItem(50, makeItem(Material.GREEN_STAINED_GLASS_PANE, ChatColor.GREEN + "Suivant"));
    	}
    	
    	if(page > 0) {
            inventory.setItem(48, makeItem(Material.GREEN_STAINED_GLASS_PANE, ChatColor.GREEN + "Précédent"));
    	}

        
        inventory.setItem(49, makeItem(Material.RED_STAINED_GLASS_PANE, ChatColor.DARK_RED + "Fermer"));
        
    	super.addMenuBorder();
    }

    public int getMaxItemsPerPage() {
        return maxItemsPerPage;
    }
}

