package fr.iban.bungeecore.chat;

import java.util.UUID;

import fr.iban.bungeecore.commands.StaffChatToggle;
import fr.iban.bungeecore.utils.HexColor;
import fr.iban.common.data.AccountProvider;
import fr.iban.spartacube.data.Account;
import jodd.util.StringUtil;
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

	public void sendGlobalMessage(UUID uuid, String message) {
		ProxyServer server = ProxyServer.getInstance();
		ProxiedPlayer player = server.getPlayer(uuid);
		Account account = new AccountProvider(player.getUniqueId()).getAccount();
		String msg = message;

		if(player.hasPermission("spartacube.colors")) {
			msg = translateColors(HexColor.translateHexColorCodes("#", "", msg));
		}

		if(message.startsWith("$") && player.hasPermission("spartacube.staffchat")) {
			sendStaffMessage(player, msg.substring(1));
			return;
		}

		if(isMuted && !player.hasPermission("spartacube.chatmanage")) {
			return;
		}

		for (ProxiedPlayer p: ProxyServer.getInstance().getPlayers()) {
			String player1 = p.getName();
			String player2 = "§6" + "@" + player1 + "§r";
			String actionbar = "§6" + player.getName() + " vous a mentionné dans le tchat";

			if (message.toLowerCase().contains(player1.toLowerCase())) {
				msg = msg.replace(player1, player2);
				p.sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionbar));
			} 
		}
		ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(translateColors(HexColor.translateHexColorCodes("#", "", "§7[" + account.getLevel() + "§7] " +getPrefix(player)+ " " + player.getName() + getSuffix(player) + " ➤ §r")) + msg));
	}

	public void sendAnnonce(UUID uuid, String annonce) {
		ProxyServer server = ProxyServer.getInstance();
		ProxiedPlayer player = server.getPlayer(uuid);
		String msg = annonce;

		if(isMuted && !player.hasPermission("spartacube.chatmanage")) {
			return;
		}
		ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(translateColors(HexColor.translateHexColorCodes("#", "", "#f07e71§lAnnonce de #fbb29e§l"+ player.getName() + " #f07e71➤ #7bc8fe§l" + msg))));
	}
	
	public void sendRankup(UUID uuid, String group) {
		ProxyServer server = ProxyServer.getInstance();
		ProxiedPlayer player = server.getPlayer(uuid);
		String groupe = group;
		
	    if(groupe.toLowerCase().equals("spartiate")) {
		    ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(HexColor.GOLD.getColor() + player.getName() + " a été promu " + HexColor.ORANGE.getColor() + group + HexColor.GOLD.getColor() + "!" + " Félicitations à lui pour avoir atteint le dernier grade !"));
	    } else {
	    	ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(HexColor.GOLD.getColor() + player.getName() + " a été promu " + group + "!"));
	    }
	    
	}

	public boolean isMuted() {
		return isMuted;
	}

	public void setMuted(boolean isMuted) {
		this.isMuted = isMuted;
	}

	private String translateColors(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	private void sendStaffMessage(ProxiedPlayer sender, String message) {
		ProxyServer.getInstance().getPlayers().forEach( p -> {
			if(p.hasPermission("spartacube.staffchat")) { 
				if (!StaffChatToggle.sc.contains(p)) {
					p.sendMessage(TextComponent.fromLegacyText("§8[§c§l"+ StringUtil.capitalize(sender.getServer().getInfo().getName()) +"§8] §6§lStaff §e"+sender.getName()+" §8➤ §6§l"+ message));
				}
			}
		});
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
