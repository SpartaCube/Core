package fr.iban.bungeecore.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import fr.iban.bungeecore.CoreBungeePlugin;
import fr.iban.bungeecore.utils.HexColor;
import fr.iban.common.data.AccountProvider;
import fr.iban.common.data.Option;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class MessageCMD extends Command implements TabExecutor {

	public MessageCMD(String name, String permission, String name2, String name3, String name4, String name5, String name6) {
		super(name, permission, name2, name3, name4, name5, name6);
	}
	
	String msg;
	
	private LoadingCache<UUID, Boolean> msgCache = Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).build(uuid -> new AccountProvider(uuid).getAccount().getOption(Option.MSG));

	public void execute(CommandSender sender, String[] args) {
		if (args.length == 0)
			sender.sendMessage(TextComponent.fromLegacyText("§e/msg [Joueur] [Message]" + ChatColor.RESET));  
		if (args.length > 0)
			if (sender instanceof ProxiedPlayer) {
				ProxiedPlayer player = (ProxiedPlayer)sender;
				ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
				if (target == null) {
					player.sendMessage(TextComponent.fromLegacyText("§c" + args[0] + " est hors-ligne!" + ChatColor.RESET));
					return;
				} 

				if (args.length > 1) {
					StringBuilder sb = new StringBuilder("");
					for (int i = 1; i < args.length; i++)
						sb.append(args[i]).append(" "); 
					msg = sb.toString();
					
					if(player.hasPermission("spartacube.colors")) {
						msg = translateColors(HexColor.translateHexColorCodes("#", "", msg));
					}
					
					ProxyServer.getInstance().getPlayers().forEach( p -> {    
						if (SocialSpyCMD.sp.contains(p)) {
							p.sendMessage(TextComponent.fromLegacyText("§8[§cSocialSpy§8] §c" + player.getName() + " §7➔ " +  "§8" + target.getName() + " §6➤ " +  "§7 " + msg));
						}  
					});
					
					ProxyServer.getInstance().getScheduler().runAsync(CoreBungeePlugin.getInstance(), () -> {
					   if (!msgCache.get(target.getUniqueId()).booleanValue() || player.hasPermission("spartacube.msgtogglebypass")) {
						 if(!CoreBungeePlugin.getInstance().ignoredPlayersChache(player.getUniqueId()).contains(player.getUniqueId())) {
							 if(target.hasPermission("spartacube.staff")) {
								 player.sendMessage(TextComponent.fromLegacyText("§8Moi §7➔ §8[§6Staff§8] §c" + target.getName() + " §6➤§7 " + msg));
							 } else {
								 player.sendMessage(TextComponent.fromLegacyText("§8Moi §7➔ §c" + target.getName() + " §6➤§7 " + msg));
							 }
							 if(player.hasPermission("spartacube.staff")) {
								 target.sendMessage(TextComponent.fromLegacyText("§8[§6Staff§8] §c" + player.getName() + " §7➔ §8Moi §6➤§7 " + msg));
							 } else {
								 target.sendMessage(TextComponent.fromLegacyText("§c" + player.getName() + " §7➔ §8Moi §6➤§7 " + msg));
							 }
						   ProxyServer.getInstance().getLogger().info("§c" + player.getName() + " §7 ➔  " + "§8" + target.getName() + " §6➤ " +  "§7 " + msg);
						 } 
					   } else {
						   player.sendMessage(TextComponent.fromLegacyText("§c" + target.getName() + " a désactivé ses messages"));
					   }  	
					});
					if (!ReplyCMD.getReplies().containsKey(player)) {
						ReplyCMD.getReplies().put(player, target);
					} else {
						ReplyCMD.getReplies().replace(player, target);
					} 
					if (!ReplyCMD.getReplies().containsKey(target)) {
						ReplyCMD.getReplies().put(target, player);
					} else {
						ReplyCMD.getReplies().replace(target, player);
					} 
				} else {
					sender.sendMessage(TextComponent.fromLegacyText("§cVeuillez entrez un message." + ChatColor.RESET));
				} 
			} 
	} 
	
	private String translateColors(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
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
