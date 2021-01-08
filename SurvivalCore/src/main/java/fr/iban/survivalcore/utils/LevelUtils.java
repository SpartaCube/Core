package fr.iban.survivalcore.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.base.Strings;

import fr.iban.spartacube.data.Account;
import fr.iban.survivalcore.SurvivalCorePlugin;
import net.md_5.bungee.api.ChatColor;

public class LevelUtils {
	
	private LevelUtils() {}

	public static void sendActionBar(Player player, int exp) {
		short currentlvl = getLevel(exp);
		short nextlvl = (short) (currentlvl+1);

		int expfromto = getLevelFromToLevelToEXP(currentlvl, nextlvl);
		int currentprogress = exp - getLevelExp(currentlvl);
		player.sendActionBar("§2"+currentlvl+ " " + getProgressBar(currentprogress, expfromto, 20, '|', ChatColor.GREEN, ChatColor.YELLOW) + " §2"+nextlvl);
	}

	public static short getLevel(int exp) {
		return (short) (Math.floor(25 + Math.sqrt(625 + 100 * exp)) / 50);
	}

	public static int getLevelExp(int level) {
		return 25 * level * level - 25 * level;
	}


	private static int getLevelFromToLevelToEXP(short from, short to) {
		int expFrom = getLevelExp(from);
		int expTo = getLevelExp(to);
		return expTo - expFrom;
	}

	public static String getLevelProgressBar(Account account, int amount) {
		short currentlvl = account.getLevel();
		short nextlvl = (short) (currentlvl+1);

		int expfromto = getLevelFromToLevelToEXP(currentlvl, nextlvl);
		int currentprogress = (int) (account.getExp() - getLevelExp(currentlvl));
		ChatColor bleu = HexColor.FLAT_BLUE2.getColor();
		return bleu+ "§l" +currentlvl+ " " + getProgressBar(currentprogress, expfromto, amount, '|', HexColor.FLAT_BLUE.getColor(), ChatColor.AQUA) + " "+ bleu+"§l"+nextlvl;
	}


	private static String getProgressBar(int current, int max, int totalBars, char symbol, ChatColor completedColor, ChatColor notCompletedColor) {
		float percent = (float) current / max;
		int progressBars = (int) (totalBars * percent);

		return Strings.repeat("" + completedColor + symbol, progressBars)
				+ Strings.repeat("" + notCompletedColor + symbol, totalBars - progressBars);
	}

	public static void sendLevelUpReward(Account account, short levelBefore, short level) {
		Player player = Bukkit.getPlayer(account.getUUID());
		player.sendMessage(HexColor.FLAT_PINK.getColor() + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
		player.sendMessage("");
		player.sendMessage(HexColor.FLAT_PURPLE.getColor() + "Vous avez atteint le niveau " + HexColor.FLAT_BLUE_GREEN.getColor() + "§l" + level + HexColor.FLAT_PURPLE.getColor()+" !");
		player.sendMessage("");
		player.sendMessage(HexColor.FLAT_BLUE.getColor() + "Vous gagnez :");
		promote(player, level);
		player.sendMessage(HexColor.FLAT_BLUE_GREEN .getColor()+ "- Un claim supplémentaire");
		int togive = 100+level*10;
		player.sendMessage(HexColor.FLAT_BLUE_GREEN .getColor()+ "- "+ togive +"§e⛃§r");
		SurvivalCorePlugin.getEconomy().depositPlayer(player, togive);
		if(level%10==0) {
			new BukkitRunnable() {
				
				@Override
				public void run() {
					LuckPermsUtils.addHomes(player, 1);
				}
			}.runTaskLater(SurvivalCorePlugin.getInstance(), 20L);
			player.sendMessage(HexColor.FLAT_BLUE_GREEN .getColor()+ "- Une résidence supplémentaire");
		}
		player.sendMessage("");
		player.sendMessage(HexColor.FLAT_PINK.getColor() + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

		account.setMaxClaims((short) (account.getMaxClaims()+1));
	}


	private static void promoteAndBroadcast(Player player, String group) {
		if(!player.hasPermission("group."+group.toLowerCase())) {
			player.sendMessage(HexColor.FLAT_BLUE_GREEN .getColor()+ "- Promotion au grade " + group);
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player.getName() + " group add "+group);
			new BukkitRunnable() {
				
				@Override
				public void run() {
					Bukkit.broadcastMessage(HexColor.FLAT_PINK.getColor() + player.getName() + " a été promu " + group + "!");					
				}
			}.runTaskLater(SurvivalCorePlugin.getInstance(), 2L);
		}
	}

	private static void promote(Player player, int level) {
		String group = null;
		switch (level) {
		case 5:
			group = "Citoyen";
			promoteAndBroadcast(player, group);
			break;
		case 20:
			group = "Hoplite";
			promoteAndBroadcast(player, group);
			break;
		case 30:
			group = "Commandant";
			promoteAndBroadcast(player, group);
			break;
		case 40:
			group = "Seigneur";
			promoteAndBroadcast(player, group);
			break;
		case 50:
			group = "Roi";
			promoteAndBroadcast(player, group);
			break;
		case 75:
			group = "Légende";
			promoteAndBroadcast(player, group);
			break;
		case 100:
			group = "Titan";
			promoteAndBroadcast(player, group);
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spawners give " + player.getName() + " IRON_GOLEM 1");
			break;
		default:
			break;
		}
	}

	//	private static void giveEmeralds(Player player, int amount) {
	//		ItemStack it = new ItemStack(Material.EMERALD, (amount <= 64 ? amount : 64));
	//		if(player.getInventory().firstEmpty() != -1) {
	//			player.getInventory().addItem(it);
	//		}else {
	//			player.getWorld().dropItem(player.getLocation(), it);
	//		}
	//	}
}
