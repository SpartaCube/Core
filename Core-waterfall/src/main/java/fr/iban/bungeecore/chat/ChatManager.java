package fr.iban.bungeecore.chat;

import java.util.UUID;

import fr.iban.bungeecore.utils.HexColor;
import fr.iban.common.data.AccountProvider;
import fr.iban.spartacube.data.Account;
import jodd.util.StringUtil;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.ChatColor;
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

		ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(translateColors(HexColor.translateHexColorCodes("#", "", "§7[" + account.getLevel() + "§7] " +getPrefix(player)+ " " + player.getName() + getSuffix(player) + " ➤ §r")) + msg));
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
				p.sendMessage(TextComponent.fromLegacyText("§8[§c§l"+ StringUtil.capitalize(sender.getServer().getInfo().getName()) +"§8] §6§lStaff §e"+sender.getName()+" §8➤ §6§l"+ message));
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
