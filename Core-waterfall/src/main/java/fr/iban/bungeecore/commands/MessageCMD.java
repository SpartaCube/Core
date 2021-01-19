package fr.iban.bungeecore.commands;

import java.util.ArrayList;
import java.util.List;
import fr.iban.bungeecore.CoreBungeePlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class MessageCMD extends Command implements TabExecutor {

	public MessageCMD(String name, String permission, String name2, String name3) {
		super(name, permission, name2, name3);
	}
	 
	public void execute(CommandSender sender, String[] args) {
	    if (args.length == 0)
	        sender.sendMessage(TextComponent.fromLegacyText("§e/msg [Joueur] [Message]" + ChatColor.RESET));  
	    if (args.length > 0)
	      if (sender instanceof ProxiedPlayer) {
	        ProxiedPlayer player = (ProxiedPlayer)sender;
	        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
	        if (target == null) {
	          player.sendMessage(TextComponent.fromLegacyText("§c" + target.getName() + "  est hors-ligne!" + ChatColor.RESET));
	          return;
	        } 
	         
	        if (args.length > 1) {
	          StringBuilder sb = new StringBuilder("");
	          for (int i = 1; i < args.length; i++)
	            sb.append(args[i]).append(" "); 
	          String msg = sb.toString();
	          String message = "§c" + player.getName() + " §7 ➔  " +  "§8" + target.getName() + " §6➤ " +  "§7 " + msg;
	          if (!MsgToggleCMD.tmsg.contains(target) || player.hasPermission("spartacube.msgtogglebypass")) {
		          player.sendMessage(TextComponent.fromLegacyText("§8Moi §7➔ §c" + target.getName() + "  §6➤§7 " + msg  + ChatColor.RESET));
		          target.sendMessage(TextComponent.fromLegacyText("§c" + player.getName() + "  §7➔ §8Moi §6➤§7 " + msg + ChatColor.RESET));
		          System.out.println(message);
	            } else {
	              player.sendMessage(TextComponent.fromLegacyText("§c" + target.getName() + " a désactivé ses messages"));
	            }  	
	          for (ProxiedPlayer staff : CoreBungeePlugin.getInstance().getProxy().getPlayers()) {    
	              if (SocialSpyCMD.sp.contains(staff)) {
	                 String format = "§8[§cSocialSpy§8] §c" + player.getName() + " §7➔ " +  "§8" + target.getName() + " §6➤ " +  "§7 " + msg;
	                 staff.sendMessage(TextComponent.fromLegacyText(format));
	                }  
	             }
	          if (!CoreBungeePlugin.r.containsKey(player)) {
	        	  CoreBungeePlugin.r.put(player, target);
	          } else {
	        	  CoreBungeePlugin.r.replace(player, target);
	          } 
	          if (!CoreBungeePlugin.r.containsKey(target)) {
	        	CoreBungeePlugin.r.put(target, player);
	          } else {
	        	CoreBungeePlugin.r.replace(target, player);
	          } 
	        } else {
	          sender.sendMessage(TextComponent.fromLegacyText("§cVeuillez entrez un message." + ChatColor.RESET));
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
