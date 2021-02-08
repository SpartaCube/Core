package fr.iban.survivalcore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BoostCMD implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player)sender;
//			int boost = XPProvider.getBoost(player);
//			if(boost == 0) {
//				player.sendMessage("§cVous n'avez pas de boost actif.");
//			}else {
//				player.sendMessage(message);
//			}
		}
		return false;
	}

}
