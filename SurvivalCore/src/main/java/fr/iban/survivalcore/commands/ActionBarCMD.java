package fr.iban.survivalcore.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import fr.iban.survivalcore.SurvivalCorePlugin;
import fr.iban.survivalcore.utils.ChatUtils;

public class ActionBarCMD implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender.hasPermission("spartacube.actionbar") && args.length >= 1) {
			StringBuilder bc = new StringBuilder();
			for (int i = 0; i < args.length; i++) {
				bc.append(args[i] + " ");
			}
			new BukkitRunnable() {
				int time = 0;
				@Override
				public void run() {
					Bukkit.getOnlinePlayers().forEach(p -> p.sendActionBar(ChatUtils.translateColors(bc.toString())));
					time++;
					if(time == 3)
						cancel();
				}
			}.runTaskTimer(SurvivalCorePlugin.getInstance(), 0, 20L);
		}
		return false;
	}



}
