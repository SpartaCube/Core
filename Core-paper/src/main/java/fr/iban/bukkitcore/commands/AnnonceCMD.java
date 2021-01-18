package fr.iban.bukkitcore.commands;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.iban.bukkitcore.CoreBukkitPlugin;
import fr.iban.bukkitcore.utils.HexColor;
import fr.iban.bukkitcore.utils.PluginMessageHelper;
import net.milkbowl.vault.economy.Economy;

public class AnnonceCMD implements CommandExecutor {

	private HashMap<String, Long> cooldowns = new HashMap<>();
	private CoreBukkitPlugin plugin;

	public AnnonceCMD(CoreBukkitPlugin plugin) {
		this.plugin = plugin;
	}

	private String translateColors(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Economy econ = plugin.getEcon();
		Player player = (Player) sender;
		int cooldownTime = 3600; // Get number of seconds from wherever you want
		if(cooldowns.containsKey(sender.getName())) {
			long secondsLeft = ((cooldowns.get(sender.getName())/1000)+cooldownTime) - (System.currentTimeMillis()/1000);
			if(secondsLeft>0) {
				String argent = "#ff6b6bVous pourrez refaire une annonce dans " +TimeUnit.SECONDS.toMinutes(secondsLeft)+ " minutes!"; {
					argent = translateColors(HexColor.translateHexColorCodes("#", "", argent));
					sender.sendMessage(argent);
					return true;
				}
			}
		}
		if(args.length <= 0) {
			String arg = "#f07e71Utilisation: /annonce (message)"; {
				arg = translateColors(HexColor.translateHexColorCodes("#", "", arg));
				player.sendMessage(arg);
				cooldowns.remove(sender.getName());
			}
		} else {
			cooldowns.put(sender.getName(), System.currentTimeMillis());
			String message = ""; {
				for (String part : args) {
					if (message != "") message += " ";
					message += part;
				}
				if (econ.getBalance(player) >= 250) {
					econ.withdrawPlayer(player, 250);
					PluginMessageHelper.sendAnnonce(player, message);
				} else
					if (econ.getBalance(player) <= 250) {
						String perm = "#ff6b6bIl vous faut 250$ pour faire une annonce !"; {
							perm = translateColors(HexColor.translateHexColorCodes("#", "", perm));
							player.sendMessage(perm);
							cooldowns.remove(sender.getName());
						}
					}
			}
		}
		return true;
	}

}
