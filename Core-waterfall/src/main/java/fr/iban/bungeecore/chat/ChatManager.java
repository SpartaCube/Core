package fr.iban.bungeecore.chat;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import fr.iban.bungeecore.CoreBungeePlugin;
import fr.iban.bungeecore.commands.StaffChatToggle;
import fr.iban.bungeecore.utils.HexColor;
import fr.iban.common.data.AccountProvider;
import fr.iban.common.data.Option;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ChatManager {

	private boolean isMuted = false;
	String msg;
	
	private LoadingCache<UUID, Boolean> tchatCache = Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).build(uuid -> new AccountProvider(uuid).getAccount().getOption(Option.TCHAT));
	private LoadingCache<UUID, Boolean> mentionCache = Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).build(uuid -> new AccountProvider(uuid).getAccount().getOption(Option.MENTION));
	private LoadingCache<UUID, Short> levelCache = Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).build(uuid -> new AccountProvider(uuid).getAccount().getLevel());

	public void sendGlobalMessage(UUID uuid, String message) {
		ProxyServer server = ProxyServer.getInstance();
		ProxiedPlayer player = server.getPlayer(uuid);
		setMSG(message);

		
		if(player.hasPermission("spartacube.colors")) {
			setMSG(translateColors(HexColor.translateHexColorCodes("#", "", msg)));
		}
		
		
		if(getMSG().startsWith("$") && player.hasPermission("spartacube.staffchat")) {
			sendStaffMessage(player, getMSG().substring(1));
			return;
		}
		

		if(isMuted && !player.hasPermission("spartacube.chatmanage")) {
			return;
		}
		
		if(!tchatCache.get(player.getUniqueId()).booleanValue()) {
		    player.sendMessage(TextComponent.fromLegacyText("§cVous ne pouvez pas envoyer ce message car votre tchat est désactivé"));
			ProxyServer.getInstance().getLogger().info(translateColors(HexColor.translateHexColorCodes("#", "", "§8[§CDÉSACTIVÉ§8]§r " + getSuffix(player) + getPrefix(player)+ " " + player.getName() + getSuffix(player) + " ➤ §r")) + getMSG());
			return;
		}

		for (ProxiedPlayer p: ProxyServer.getInstance().getPlayers()) {
			String player1 = p.getName();
			String player2 = "#fdcb6e" + "@" + player1 + "§r";
			String actionbar = "§6" + player.getName() + " vous a mentionné dans le tchat";
			
			if (message.toLowerCase().contains(player1.toLowerCase())) {
			  if(mentionCache.get(p.getUniqueId()).booleanValue()) {
				setMSG(getMSG().replace(player1, translateColors(HexColor.translateHexColorCodes("#", "", player2))));
				p.sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionbar));
			   } 
			} 
		}
		
		ProxyServer.getInstance().getPlayers().forEach( p -> {
			if(tchatCache.get(p.getUniqueId()).booleanValue()) { 
				if (!CoreBungeePlugin.getInstance().ignoredPlayersChache(p.getUniqueId()).contains(player.getUniqueId())) {
					p.sendMessage(TextComponent.fromLegacyText(translateColors(HexColor.translateHexColorCodes("#", "", "§7[" + levelCache.get(uuid).shortValue() + "§7] " + getSuffix(player) + getPrefix(player)+ " " + player.getName() + getSuffix(player) + " ➤ §r")) + getMSG()));
				}
			}
		});
		ProxyServer.getInstance().getLogger().info(translateColors(HexColor.translateHexColorCodes("#", "", getSuffix(player) + getPrefix(player)+ " " + player.getName() + getSuffix(player) + " ➤ §r")) + getMSG());
	}

	public void sendAnnonce(UUID uuid, String annonce) {
		ProxyServer server = ProxyServer.getInstance();
		ProxiedPlayer player = server.getPlayer(uuid);
		String message = annonce;

		if(isMuted && !player.hasPermission("spartacube.chatmanage")) {
			return;
		}
		
		ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(translateColors(HexColor.translateHexColorCodes("#", "", "#f07e71§lAnnonce de #fbb29e§l"+ player.getName() + " #f07e71➤ #7bc8fe§l" + message))));
	}
	
	public void sendRankup(UUID uuid, String group) {
		ProxyServer server = ProxyServer.getInstance();
		ProxiedPlayer player = server.getPlayer(uuid);
		
	    	ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(HexColor.FLAT_PINK.getColor() + player.getName() + " a été promu " + group + "!"));
	    
	}

	public boolean isMuted() {
		return isMuted;
	}

	public void setMuted(boolean isMuted) {
		this.isMuted = isMuted;
	}
	
	public void setMSG(String msg) {
		this.msg = msg;
	}
	
	private String getMSG() {
		return msg;
	}

	private String translateColors(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	private void sendStaffMessage(ProxiedPlayer sender, String message) {
		ProxyServer.getInstance().getPlayers().forEach( p -> {
			if(p.hasPermission("spartacube.staffchat")) { 
				if (!StaffChatToggle.sc.contains(p)) {
					p.sendMessage(TextComponent.fromLegacyText(translateColors(HexColor.translateHexColorCodes("#", "" , "§8[§3§lStaff§8] " + getSuffix(sender) + "§l" + getPrefix(sender) + "§l " + sender.getName() + " §8§l➤ " + getSuffix(sender) + "§l" + message))));
				}
			}
		});
		ProxyServer.getInstance().getLogger().info(translateColors(HexColor.translateHexColorCodes("#", "" , "§8[§3§lStaff§8] " + getSuffix(sender) + "§l" + getPrefix(sender) + "§l " + sender.getName() + " §8§l➤ " + getSuffix(sender) + "§l" + message)));
	}

	public void toggleChat(CommandSender sender) {
		isMuted = !isMuted;
		if(isMuted) {
			ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText("§cLe chat a été rendu muet par "+ sender.getName()+"."));
		}else {
			ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText("§aLe chat n'est plus muet."));
		}
	}

	/*
	 * Luckperms
	 */

	private LuckPerms luckapi = LuckPermsProvider.get();

	private User loadUser(ProxiedPlayer player) {
		if (!player.isConnected())
			throw new IllegalStateException("Player is offline!"); 
		return luckapi.getUserManager().getUser(player.getUniqueId());
	}

	private CachedMetaData playerMeta(ProxiedPlayer player) {
		return loadUser(player).getCachedData().getMetaData(luckapi.getContextManager().getQueryOptions(player));
	}

	private String getPrefix(ProxiedPlayer player) {
		String prefix = playerMeta(player).getPrefix();
		return (prefix != null) ? prefix : "";
	}

	private String getSuffix(ProxiedPlayer player) {
		String suffix = playerMeta(player).getSuffix();
		return (suffix != null) ? suffix : "";
	}

}