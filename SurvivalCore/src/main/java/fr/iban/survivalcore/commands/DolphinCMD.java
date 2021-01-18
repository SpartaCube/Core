package fr.iban.survivalcore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DolphinCMD implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player)sender;
			if(player.hasPermission("spartacube.dolphin")) {
				if(player.hasPotionEffect(PotionEffectType.DOLPHINS_GRACE)) {
					player.removePotionEffect(PotionEffectType.DOLPHINS_GRACE);
					player.sendMessage("§cEffet dophin désactivé.");
				}else {
					player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, Integer.MAX_VALUE, 0));
					player.sendMessage("§aEffet dophin activé.");
				}
			}
			
		}
		return false;
	}
	
	

}
