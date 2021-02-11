package fr.iban.survivalcore.utils;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import fr.iban.common.data.AccountProvider;
import fr.iban.spartacube.data.Account;

public class XPProvider {

	private static Map<Player, Map<Long, Integer>> xpLogs = new ConcurrentHashMap<>();

	public static CompletableFuture<Void> addXP(Player player, int amount, boolean applynerf) {
		return CompletableFuture.runAsync(() -> {
			AccountProvider ap = new AccountProvider(player.getUniqueId());
			Account account = ap.getAccount();
			
			if(applynerf && checkLast(player, 6) > 12) {
				return;
			}
			
			//On log l'xp ajouté
			getXPLogs(player).put(System.currentTimeMillis(), amount);

			short levelbefore = account.getLevel();
			account.addExp(amount > 12 ? 12 : amount);
			short levelafter = account.getLevel();

			player.sendActionBar(LevelUtils.getLevelProgressBar(account, 20));

			if(levelbefore < levelafter)
				LevelUtils.sendLevelUpReward(account, levelbefore, levelafter);

			ap.sendAccountToRedis(account);
		});
	}

	private static Map<Long, Integer> getXPLogs(Player player){
		if(!xpLogs.containsKey(player)) {
			xpLogs.put(player, new ConcurrentHashMap<>());
		}
		return xpLogs.get(player);
	}

	private static int checkLast(Player player, int periodInSec) {
		int amountLastTenMin = 0;
		if(!getXPLogs(player).isEmpty()) {
			for(Entry<Long, Integer> log : getXPLogs(player).entrySet()) {
				if(System.currentTimeMillis() - log.getKey() > 1000*periodInSec) {
					getXPLogs(player).remove(log.getKey());
				}else {
					amountLastTenMin += log.getValue();
				}
			}
		}
		return amountLastTenMin;
	}

//	public static int getBoost(Player player){
//		int boost = 0;
//		for (PermissionAttachmentInfo perms : player.getEffectivePermissions()) {
//			if (perms.getPermission().startsWith("boost")) {
//				int i = Integer.parseInt(perms.getPermission().split("boost")[1]);
//				if(i > boost) {
//					boost = i;
//				}
//			}
//		}
//		return boost;
//	}

}
