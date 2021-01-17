package fr.iban.bungeecore.listeners;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import org.ocpsoft.prettytime.PrettyTime;

import fr.iban.bungeecore.CoreBungeePlugin;
import fr.iban.bungeecore.utils.ChatUtils;
import fr.iban.common.data.AccountProvider;
import fr.iban.common.data.redis.RedisAccess;
import fr.iban.common.utils.ArrayUtils;
import fr.iban.spartacube.data.Account;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ProxyJoinQuitListener implements Listener {

	private String[] joinMessages =
		{
				"%s s'est connecté !",
				"%s est dans la place !",
				"%s a rejoint le serveur !",
				"Un %s sauvage apparaît ! "
		};

	private String[] quitMessages =
		{
				"%s nous a quitté :(",
				"%s s'est déconnecté."
		};

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onProxyJoin(PostLoginEvent e) {
		ProxiedPlayer player = e.getPlayer();
		UUID uuid = player.getUniqueId();
		ProxyServer.getInstance().getScheduler().runAsync(CoreBungeePlugin.getInstance(), () -> {
			AccountProvider accountProvider = new AccountProvider(uuid);
			Account account = accountProvider.getAccount();
			account.setName(player.getName());
			account.setIp(player.getAddress().getHostString());
			accountProvider.sendAccountToRedis(account);

			if(accountProvider.hasPlayedBefore()) {
				TextComponent message = new TextComponent(new StringBuilder().append("§8[§a+§8] §8").append(String.format(ArrayUtils.getRandomFromArray(joinMessages), player.getName())).toString());
				message.setHoverEvent(ChatUtils.getShowTextHoverEvent(ChatColor.GRAY+"Vu pour la dernière fois " + getLastSeen(account.getLastSeen())));
				ProxyServer.getInstance().broadcast(message);
			}else {
				ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText("§8≫ §7" + player.getName() + " s'est connecté pour la première fois !" ));
			}
		});

	}

	@EventHandler
	public void onDisconnect(PlayerDisconnectEvent e) {
		ProxiedPlayer player = e.getPlayer();
		ProxyServer.getInstance().getScheduler().runAsync(CoreBungeePlugin.getInstance(), () -> {
			AccountProvider accountProvider = new AccountProvider(player.getUniqueId());
			Account account = accountProvider.getAccount();
			account.setLastSeen(System.currentTimeMillis());
			accountProvider.sendAccountToDB(account);
			accountProvider.removeAccountFromRedis();
			RedisAccess.getInstance().getRedissonClient().getMap("PendingTeleports").fastRemove(player.getUniqueId());
		});
		ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText("§8[§c-§8] §8" + String.format(ArrayUtils.getRandomFromArray(quitMessages), player.getName())));
	}

	private String getLastSeen(long time){
		if(time == 0) return "jamais";
		PrettyTime prettyTime = new PrettyTime(new Locale("fr"));
		return prettyTime.format(new Date(time));
	}

}