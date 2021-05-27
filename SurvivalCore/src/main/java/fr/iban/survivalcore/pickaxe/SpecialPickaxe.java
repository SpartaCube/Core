package fr.iban.survivalcore.pickaxe;

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

public class SpecialPickaxe {

	public static final Set<Material> canBreakWith3x3 = EnumSet.of(
			Material.COBBLESTONE, Material.STONE, Material.DIRT, Material.NETHERRACK, Material.GRASS_BLOCK, Material.DIORITE,
			Material.ANDESITE, Material.GRANITE, Material.IRON_ORE, Material.COAL_ORE, Material.GOLD_ORE, Material.LAPIS_ORE,
			Material.REDSTONE_ORE, Material.GRAVEL, Material.BASALT, Material.BLACKSTONE, Material.END_STONE, Material.DIRT,
			Material.BASALT, Material.MAGMA_BLOCK
			);

	public static HashMap<UUID, BlockFace> faces = new HashMap<UUID, BlockFace>();


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

	public static boolean isCutCleanPickaxe(ItemStack item) {
		if(item.hasItemMeta() && item.getItemMeta().hasLore() && item.getItemMeta().getLore().contains("§e§lCette pioche fait fondre")) {
			return true;
		}
		return false;
	}

	public static boolean is3x3Pickaxe(ItemStack item) {
		if(item.hasItemMeta() && item.getItemMeta().hasLore() && item.getItemMeta().getLore().contains("§3|| §bMine du §93x3§3 ||")) {
			return true;
		}
		return false;	
	}


	public static List<Block> getSurroundingBlocks(Player player, Block targetBlock) {
		return getSurroundingBlocks(faces.get(player.getUniqueId()), targetBlock).stream().filter(block -> canBreakWith3x3.contains(block.getType())).collect(Collectors.toList());
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
