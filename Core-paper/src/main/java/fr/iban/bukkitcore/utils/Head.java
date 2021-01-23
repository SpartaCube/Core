package fr.iban.bukkitcore.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.arcaniax.hdb.api.HeadDatabaseAPI;

public enum Head {
	
	CHEST("227"),
	CHEST_DIRT("775"),
	HAL("41548"),
	NO_ENTRY("19778"),
	GRASS("24064"),
	OAK_RIGHT("7826"),
	OAK_LEFT("7827"),
	OAK_PLUS("2336")
	;
	
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
