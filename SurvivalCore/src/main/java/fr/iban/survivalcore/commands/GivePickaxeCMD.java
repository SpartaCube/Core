package fr.iban.survivalcore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.iban.survivalcore.pickaxe.SpecialPickaxe;

public class GivePickaxeCMD implements CommandExecutor {


	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		
		if(sender instanceof Player && sender.hasPermission("spartacube.givepioche")) {
			Player player = (Player)sender;
			
			if(args.length == 0) {
				player.sendMessage("/givepioche hades/3x3");
			}else if(args.length == 1) {
				switch (args[0]) {
				case "hades":
					player.getInventory().addItem(SpecialPickaxe.getCutCleanPickaxe());
					break;
				case "3x3":
					player.getInventory().addItem(SpecialPickaxe.get3x3Pickaxe());
					break;
				default:
					player.sendMessage("/givepioche hades/3x3");
					break;
				}
			}
		}
		
		return false;

	}

	
}
