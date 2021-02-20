package fr.iban.survivalcore.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.iban.common.data.AccountProvider;
import fr.iban.common.data.Boost;
import fr.iban.common.data.GlobalBoosts;
import fr.iban.spartacube.data.Account;
import fr.iban.survivalcore.SurvivalCorePlugin;

public class XPProvider {
	
	private static GlobalBoosts gb = new GlobalBoosts();;

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
			int multiplieur = 1+((getTotalBoost(account, ap) + XPProvider.getTotalGlobalBoost()) /100);
			account.addExp(amount*multiplieur > 12*multiplieur ? 12*multiplieur : amount*multiplieur);
			short levelafter = account.getLevel();

			player.sendActionBar(LevelUtils.getLevelProgressBar(account, 20));

			if(levelbefore < levelafter)
				LevelUtils.sendLevelUpReward(account, levelbefore, levelafter);

			ap.sendAccountToRedis(account);
		});
	}
	
	public static int getTotalBoost(Account account, AccountProvider ap) {
		int somme = 0;
		Iterator<Boost> it = account.getBoosts().iterator();
		while(it.hasNext()) {
			Boost boost = it.next();
			if(boost.getEnd() > System.currentTimeMillis()) {
				somme += boost.getValue();
			}else {
            	Bukkit.getScheduler().runTaskAsynchronously(SurvivalCorePlugin.getInstance(), new Runnable() {
                    @Override
                      public void run() {
                    	ap.deleteBoostFromDB(boost.getId(), boost.getEnd(), boost.getValue());
                    }
                });
				it.remove();
			}
		}
		return somme > 100 ? 100 : somme;
	}
	
	public static int getTotalGlobalBoost() {
		int somme = 0;
		Iterator<Boost> it = gb.getBoosts().iterator();
		while(it.hasNext()) {
			Boost boost = it.next();
			if(boost.getEnd() > System.currentTimeMillis()) {
				somme += boost.getValue();
			}else {
            	Bukkit.getScheduler().runTaskAsynchronously(SurvivalCorePlugin.getInstance(), new Runnable() {
                    @Override
                      public void run() {
                    	gb.deleteGlobalBoostFromDB(boost.getId(), boost.getEnd(), boost.getValue());
                    }
                });
				it.remove();
			}
		}
		return somme > 100 ? 100 : somme;
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

}
