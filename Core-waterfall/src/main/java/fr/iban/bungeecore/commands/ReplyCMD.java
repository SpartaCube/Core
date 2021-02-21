package fr.iban.bungeecore.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class ReplyCMD extends Command implements TabExecutor {

	public static HashMap<ProxiedPlayer, ProxiedPlayer> r = new HashMap<>();

	public ReplyCMD(String name, String permission, String name2) {
		super(name, permission, name2);
	}

	public void execute(CommandSender sender, String[] args) {
		if (args.length == 0)
			sender.sendMessage(TextComponent.fromLegacyText( "§e/r [Message]" + ChatColor.RESET));  
		if (args.length > 0)
			if (sender instanceof ProxiedPlayer) {
				ProxiedPlayer player = (ProxiedPlayer)sender;
				if (ReplyCMD.r.containsKey(player)) {
					ProxiedPlayer target = ReplyCMD.r.get(player);
					if (target == null) {
						player.sendMessage(TextComponent.fromLegacyText("§c" + ((ProxiedPlayer)ReplyCMD.r.get(player)).getName() + " est hors-ligne!" + ChatColor.RESET));
						return;
					} 
					StringBuilder sb = new StringBuilder("");
					for (int i = 0; i < args.length; i++)
						sb.append(args[i]).append(" "); 
					String msg = sb.toString();
					ProxyServer.getInstance().getPlayers().forEach( p -> {    
						if (SocialSpyCMD.sp.contains(p)) {
							p.sendMessage(TextComponent.fromLegacyText("§8[§cSocialSpy§8] §c" + player.getName() + " §7➔ " +  "§8" + target.getName() + " §6➤ " +  "§7 " + msg));
						}  
					});
					if (!MsgToggleCMD.tmsg.contains(target) || player.hasPermission("spartacube.msgtogglebypass")) {
						player.sendMessage(TextComponent.fromLegacyText("§8Moi §7➔ §c" + target.getName() + " §6➤§7 " + msg + ChatColor.RESET));
						target.sendMessage(TextComponent.fromLegacyText("§c" + player.getName() + " §7➔ §8Moi §6➤§7 " + msg + ChatColor.RESET));
						System.out.println("§c" + player.getName() + " §7 ➔  " +  "§8" + target.getName() + " §6➤ " +  "§7 " + msg);
					} else {
						player.sendMessage(TextComponent.fromLegacyText("§c" + target.getName() + " a désactivé ses messages"));
					}  
				} else {
					sender.sendMessage(TextComponent.fromLegacyText("§cTu ne peux pas répondre, car personne ne t'a écrit." + ChatColor.RESET));
				} 
			} 

	} 

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		List<String> playernames = new ArrayList<>();
		if(args.length == 1) {
			for(ProxiedPlayer p: ProxyServer.getInstance().getPlayers()){
				if(!sender.getName().equals(p.getName()) && p.getName().toLowerCase().startsWith(args[0].toLowerCase()))
					playernames.add(p.getName());
			}
		}
		return playernames;
	}
}
