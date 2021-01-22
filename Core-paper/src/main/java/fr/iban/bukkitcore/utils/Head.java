package fr.iban.bukkitcore.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.arcaniax.hdb.api.HeadDatabaseAPI;

public enum Head {
	
	CHEST("227"),
	CHEST_DIRT("775");
	
	private static HeadDatabaseAPI api;
	private String id;
	
	private Head(String id) {
		this.id = id;
	}
		
	private boolean isAPILoaded() {
		return api != null;
	}
	
	public ItemStack get() {
		if(isAPILoaded()) {
			return api.getItemHead(id);
		}else {
			return new ItemStack(Material.PLAYER_HEAD);
		}
	}
	
	public static void loadAPI() {
		api = new HeadDatabaseAPI();
	}

}
