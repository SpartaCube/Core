package fr.iban.bukkitcore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import fr.iban.bukkitcore.CoreBukkitPlugin;

public class PacketLogCMD implements CommandExecutor {
	
	private CoreBukkitPlugin plugin;
	
	

	public PacketLogCMD(CoreBukkitPlugin plugin) {
		this.plugin = plugin;
	}



	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender.hasPermission("spartacube.packetlogger")) {
			plugin.setPacketlogging(!plugin.isPacketlogging());
			sender.sendMessage("Packet toggle -> " + plugin.isPacketlogging());
		}
		return false;
	}

}
