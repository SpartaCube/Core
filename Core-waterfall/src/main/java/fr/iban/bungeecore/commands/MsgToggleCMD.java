package fr.iban.bungeecore.commands;

import java.util.ArrayList;
import java.util.List;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class MsgToggleCMD extends Command {
	
	public MsgToggleCMD(String name, String permission) {
		super(name, permission);
	}
	public static List<ProxiedPlayer> tmsg = new ArrayList<>();
	  
	public void execute(CommandSender sender, String[] args) {
	  if (sender instanceof ProxiedPlayer) {
	      ProxiedPlayer player = (ProxiedPlayer)sender;
	      if (tmsg.contains(player)) {
	        tmsg.remove(player);
	        player.sendMessage(TextComponent.fromLegacyText("§cVous ne pouvez plus recevoir de message"));
	        return;
	    } 
	      tmsg.add(player);
	      player.sendMessage(TextComponent.fromLegacyText("§aVous pouvez à nouveau recevoir des messages de message"));
	    } else {
	    sender.sendMessage(TextComponent.fromLegacyText("§cSeul la console peut faire cette commande"));
	  } 
	}
  }