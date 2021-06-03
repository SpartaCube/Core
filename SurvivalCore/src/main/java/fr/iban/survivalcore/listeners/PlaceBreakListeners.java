package fr.iban.survivalcore.listeners;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import fr.iban.lands.LandManager;
import fr.iban.lands.LandsPlugin;
import fr.iban.lands.enums.Action;
import fr.iban.lands.objects.Land;
import fr.iban.survivalcore.tools.SpecialTools;

public class PlaceBreakListeners implements Listener {

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		LandManager landManager = LandsPlugin.getInstance().getLandManager();
		Player player = e.getPlayer();
		Block block = e.getBlock();
		ItemStack itemInHand = player.getInventory().getItemInMainHand();
		Location loc = block.getLocation();
		
		if (player.isSneaking()) return;

		Chunk chunk = block.getChunk();
		Land land = landManager.getLandAt(chunk);

		if(!landManager.isWilderness(land) && !land.isBypassing(player, Action.BLOCK_BREAK)) return;

		//Pioche 3x3
		if(SpecialTools.is3x3Shovel(itemInHand)) {
			for(Block b : SpecialTools.getSurroundingBlocksPickaxe(player, block)){
				Chunk c = b.getChunk();
				Land l = landManager.getLandAt(c);
				if(!landManager.isWilderness(l) && !l.isBypassing(player, Action.BLOCK_BREAK)) 
					continue;
				
				b.breakNaturally(itemInHand);
			}
		}
		
		//Pelle 3x3
		if(SpecialTools.is3x3Pickaxe(itemInHand)) {
			for(Block b : SpecialTools.getSurroundingBlocksShovel(player, block)){
				Chunk c = b.getChunk();
				Land l = landManager.getLandAt(c);
				if(!landManager.isWilderness(l) && !l.isBypassing(player, Action.BLOCK_BREAK)) 
					continue;
				
				b.breakNaturally(itemInHand);
			}
		}
		
		//Hache bûcheron
		if(SpecialTools.is3x3Pickaxe(itemInHand)) {
		    if (!e.getBlock().getState().getType().toString().equalsIgnoreCase("LOG") && !e.getBlock().getState().getType().toString().equalsIgnoreCase("LOG_2"))
		        return; 
		    breakBlock(e.getBlock(), e.getPlayer());
		}
		
		

		//Pioche Hades
		if(SpecialTools.isCutCleanPickaxe(itemInHand)) {
			switch (block.getType()) {
			case GOLD_ORE:
				drop(e, Material.GOLD_INGOT, 1, loc, true);
				break;
			case IRON_ORE:
				drop(e, Material.IRON_INGOT, 0.7, loc, true);
				break;
			case ANCIENT_DEBRIS:
				drop(e, Material.NETHERITE_SCRAP, 2, loc, false);
				break;
			case NETHER_GOLD_ORE:
				drop(e, Material.GOLD_INGOT, 1, loc, false);
				break;
			default:
				break;
			}
		}
	}
	
	  private void breakBlock(Block b, Player p) {
		    b.breakNaturally();
		    Location above = new Location(b.getWorld(), b.getLocation().getBlockX(), (b.getLocation().getBlockY() + 1), b.getLocation().getBlockZ());
		    Block blockAbove = above.getBlock();
		    
			Chunk c = b.getChunk();
			Land l = landManager.getLandAt(c);
			if(!landManager.isWilderness(l) && !l.isBypassing(player, Action.BLOCK_BREAK)) 
				continue;
			
		    if (blockAbove.getState().getType().toString().equalsIgnoreCase("LOG") || blockAbove.getState().getType().toString().equalsIgnoreCase("LOG_2")) {
		      breakBlock(blockAbove, p);
		      p.getItemInHand().setDurability((short)(p.getItemInHand().getDurability() + 1));
		      if (p.getItemInHand().getDurability() > p.getItemInHand().getType().getMaxDurability()) {
		        p.getInventory().remove(p.getItemInHand());
		        p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
		      } 
		    } 
		  }
		  
	
	private void drop(BlockBreakEvent e, Material newDrop, double xp, Location loc, boolean fortuneMultiply) {
		Player player = e.getPlayer();
		ItemStack toDrop = new ItemStack(newDrop);
		int amountToDrop = 1;
		int expToDrop = 0;
		
		//Centre du bloc : 
		loc.add(0.5, 0.5, 0.5);
		
		if(fortuneMultiply) {
			int fortuneLevel = player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
			amountToDrop = getMultiplier(fortuneLevel);
			toDrop.setAmount(amountToDrop);
		}
		
		for (int i = 0; i < amountToDrop ; i++) {
			if(xp >= 1) {
				expToDrop += xp;
			}else {
				if(Math.random() <= xp) {
					expToDrop++;
				}
			}
		}
		
		e.setDropItems(false);
		e.setExpToDrop(expToDrop);
		player.getWorld().dropItem(loc, toDrop);
		player.getWorld().spawnParticle(Particle.FLAME, loc, 10 ,0.3, 0.3, 0.3, 0.02);
	}

	private int getMultiplier(int fortunelevel) {
		Random rand = new Random();
		int alz2 = rand.nextInt(100 + 1);
		switch(fortunelevel) {

		case(3):
			if(alz2 <= 20 && alz2 >= 1) {
				return 2;
			}else if(alz2 <= 40 && alz2 > 20) {
				return 3;
			}else if(alz2 <= 60 && alz2 > 40) {
				return 4;
			}else {
				return 1;
			}
		case(2):
			if(alz2 <= 25 && alz2 >= 1) {
				return 2;
			}else if(alz2 <= 50 && alz2 > 25) {
				return 3;
			}else {
				return 1;
			}
		case(1):
			if(alz2 <= 33 && alz2 >= 1) {
				return 2;
			}else {
				return 1;
			}
		}

		return 1;
	}

}
