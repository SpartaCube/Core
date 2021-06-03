package fr.iban.survivalcore.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SpecialTools {

	public static final Set<Material> canBreakWith3x3Pickaxe = EnumSet.of(
			Material.COBBLESTONE, Material.STONE, Material.DIRT, Material.NETHERRACK, Material.GRASS_BLOCK, Material.DIORITE,
			Material.ANDESITE, Material.GRANITE, Material.IRON_ORE, Material.COAL_ORE, Material.GOLD_ORE, Material.LAPIS_ORE,
			Material.REDSTONE_ORE, Material.GRAVEL, Material.BASALT, Material.BLACKSTONE, Material.END_STONE, Material.DIRT,
			Material.BASALT, Material.MAGMA_BLOCK
			);
	
	public static final Set<Material> canBreakWith3x3Shovel = EnumSet.of(
			Material.GRASS_BLOCK, Material.DIRT, Material.SAND, Material.RED_SAND, Material.GRAVEL
			);

	public static HashMap<UUID, BlockFace> facesShovel = new HashMap<UUID, BlockFace>();

	public static HashMap<UUID, BlockFace> facesPickaxe = new HashMap<UUID, BlockFace>();


	public static ItemStack get3x3Pickaxe() {
		ItemStack hades = new ItemStack(Material.NETHERITE_PICKAXE);

		ItemMeta cus = hades.getItemMeta();
		cus.setDisplayName("§9§lMarteau du Dieu");
		cus.setLore(Arrays.asList("§3|| §bMine du §93x3§3 ||","§c§l[ITEM LEGENDAIRE]"));
		hades.setItemMeta(cus);
		return hades;
	}

	public static ItemStack getCutCleanPickaxe() {
		ItemStack hades = new ItemStack(Material.NETHERITE_PICKAXE);

		ItemMeta cus = hades.getItemMeta();
		cus.setDisplayName("§e§lPioche d'Hades");
		cus.setLore(Arrays.asList("§e§l-------------","§e§lCette pioche fait fondre","§e§lles minerais que vous minez.","§e§l-------------","§c§l[ITEM LEGENDAIRE]"));
		hades.setItemMeta(cus);
		return hades;	
	}
	
	public static ItemStack get3x3Shovel() {
		ItemStack hades = new ItemStack(Material.NETHERITE_PICKAXE);

		ItemMeta cus = hades.getItemMeta();
		cus.setDisplayName("§9§lPelle du Dieu");
		cus.setLore(Arrays.asList("§3|| §bMine du §93x3§3 ||","§c§l[ITEM LEGENDAIRE]"));
		hades.setItemMeta(cus);
		return hades;
	}

	public static ItemStack getLumberjackAxe() {
		ItemStack hades = new ItemStack(Material.NETHERITE_PICKAXE);

		ItemMeta cus = hades.getItemMeta();
		cus.setDisplayName("§e§lHache du Bûcheron");
		cus.setLore(Arrays.asList("§e§l-------------","§e§lCette hache détruit entièrement l'arbre","§e§lque vous cassez.","§e§l-------------","§c§l[ITEM LEGENDAIRE]"));
		hades.setItemMeta(cus);
		return hades;	
	}
	
	public static boolean isLumberjackAxe(ItemStack item) {
		if(item.hasItemMeta() && item.getItemMeta().hasLore() && item.getItemMeta().getLore().contains("§e§lCette hache détruit entièrement")) {
			return true;
		}
		return false;
	}

	public static boolean is3x3Shovel(ItemStack item) {
		if(item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().contains("§e§lPelle du Dieu")) {
			return true;
		}
		return false;	
	}

	public static boolean isCutCleanPickaxe(ItemStack item) {
		if(item.hasItemMeta() && item.getItemMeta().hasLore() && item.getItemMeta().getLore().contains("§e§lCette pioche fait fondre")) {
			return true;
		}
		return false;
	}

	public static boolean is3x3Pickaxe(ItemStack item) {
		if(item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().contains("§9§lMarteau du Dieu")) {
			return true;
		}
		return false;	
	}


	public static List<Block> getSurroundingBlocksShovel(Player player, Block targetBlock) {
		return getSurroundingBlocks(facesShovel.get(player.getUniqueId()), targetBlock).stream().filter(block -> canBreakWith3x3Shovel.contains(block.getType())).collect(Collectors.toList());
	}
	
	public static List<Block> getSurroundingBlocksPickaxe(Player player, Block targetBlock) {
		return getSurroundingBlocks(facesPickaxe.get(player.getUniqueId()), targetBlock).stream().filter(block -> canBreakWith3x3Pickaxe.contains(block.getType())).collect(Collectors.toList());
	}

	private static List<Block> getSurroundingBlocks(BlockFace blockFace, Block targetBlock) {
		ArrayList<Block> blocks = new ArrayList<Block>();
		World world = targetBlock.getWorld();

		int x, y, z;
		x = targetBlock.getX();
		y = targetBlock.getY();
		z = targetBlock.getZ();

		switch(blockFace) {
		case UP:
		case DOWN:
			blocks.add(world.getBlockAt(x+1, y, z));
			blocks.add(world.getBlockAt(x-1, y, z));
			blocks.add(world.getBlockAt(x, y, z+1));
			blocks.add(world.getBlockAt(x, y, z-1));
			blocks.add(world.getBlockAt(x+1, y, z+1));
			blocks.add(world.getBlockAt(x-1, y, z-1));
			blocks.add(world.getBlockAt(x+1, y, z-1));
			blocks.add(world.getBlockAt(x-1, y, z+1));
			break;
		case EAST:
		case WEST:
			blocks.add(world.getBlockAt(x, y, z+1));
			blocks.add(world.getBlockAt(x, y, z-1));
			blocks.add(world.getBlockAt(x, y+1, z));
			blocks.add(world.getBlockAt(x, y-1, z));
			blocks.add(world.getBlockAt(x, y+1, z+1));
			blocks.add(world.getBlockAt(x, y-1, z-1));
			blocks.add(world.getBlockAt(x, y-1, z+1));
			blocks.add(world.getBlockAt(x, y+1, z-1));
			break;
		case NORTH:
		case SOUTH:
			blocks.add(world.getBlockAt(x+1, y, z));
			blocks.add(world.getBlockAt(x-1, y, z));
			blocks.add(world.getBlockAt(x, y+1, z));
			blocks.add(world.getBlockAt(x, y-1, z));
			blocks.add(world.getBlockAt(x+1, y+1, z));
			blocks.add(world.getBlockAt(x-1, y-1, z));
			blocks.add(world.getBlockAt(x+1, y-1, z));
			blocks.add(world.getBlockAt(x-1, y+1, z));
			break;
		default:
			break;
		}

		blocks.removeAll(Collections.singleton(null));
		return blocks;
	}

}
